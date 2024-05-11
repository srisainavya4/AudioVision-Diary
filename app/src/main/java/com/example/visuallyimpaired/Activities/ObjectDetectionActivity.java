package com.example.visuallyimpaired.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.Contract.Face;
import edmt.dev.edmtdevcognitivevision.Contract.Tag;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

import static com.example.visuallyimpaired.Utility.Constants.DETECT_OBJ;

public class ObjectDetectionActivity extends AppCompatActivity{

    private final String API_KEY = "13e5a00f7e3d4ac59a58744c1c581687";
    private final String API_LINK = "https://centralindia.api.cognitive.microsoft.com/vision/v1.0";
    Uri mImageUri;
    Bitmap bitmap;
    VisionServiceClient visionServiceClient = new VisionServiceRestClient(API_KEY, API_LINK);
    ArrayList<String> Tags = new ArrayList<>();
    HashMap<String,String> Faces = new HashMap<String,String>();
    boolean isAdultContent = false;
    String description = "";
    Context context;
    TextView txtResult;
    ImageView image;
    RelativeLayout objLay;

    private TextToSpeech textToSpeech;

    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        // If the rotate angle is 0, then return the original image, else return the rotated image
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }
    StringBuilder finalResult = new StringBuilder();
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                finish();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        initUI();
    }

    private void initUI() {
        context = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("photoUri")) {
            mImageUri = Uri.parse(extras.getString("photoUri"));
        }
        txtResult = findViewById(R.id.txtResult);
        image = findViewById(R.id.image);
        objLay = findViewById(R.id.objLay);

        objLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public void onLongPress(MotionEvent e) {
                    Intent intent = new Intent(context,CameraActivity.class);
                    intent.putExtra("isAddActivity",3);
                    startActivity(intent);
                    finish();
                    super.onLongPress(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                   Helper.speak(context,finalResult.toString()+DETECT_OBJ,false);
                    return super.onDoubleTap(e);
                }
            });
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        analysisSurrounding();
    }

    private void analysisSurrounding() {
        try {
            InputStream ims = getContentResolver().openInputStream(mImageUri);
            bitmap = BitmapFactory.decodeStream(ims);
            bitmap = scaleBitmapDown(bitmap, 500);
            bitmap = rotateBitmap(bitmap, 90);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            @SuppressLint("StaticFieldLeak")
            AsyncTask<InputStream, String, String> visionTask = new AsyncTask<InputStream, String, String>() {
                ProgressDialog progressDialog = new ProgressDialog(ObjectDetectionActivity.this);

                @Override
                protected void onPreExecute() {
                    progressDialog.show();
                }

                @Override
                protected String doInBackground(InputStream... inputStreams) {
                    try {
                        publishProgress("Reconizing...");
                        String[] features = {"Description", "Tags", "Faces", "Adult"};
                        String[] details = {};

                        AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0], features, details);

                        String jsonResult = new Gson().toJson(result);
                        return jsonResult;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("TAG", e.getMessage());
                    } catch (VisionServiceException e) {
                        Log.d("TAG", e.getMessage());
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String s) {
                    Log.d("TAG S", s);

                    if (TextUtils.isEmpty(s)) {
                        progressDialog.dismiss();
                        Toast.makeText(ObjectDetectionActivity.this, "API Return Empty Result", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(s);
                            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                            StringBuilder result_Text = new StringBuilder();
                            for (Caption caption : result.description.captions)
                                result_Text.append(caption.text);
                            description = result_Text.toString();

                            for(Tag tag : result.tags){
                                Tags.add(tag.name);
                            }

                            for(Face face : result.faces){
                                Faces.put(String.valueOf(face.age),String.valueOf(face.gender));
                            }

                            isAdultContent = result.adult.isAdultContent;

                            Log.d("TAG FACES",String.valueOf(Faces.size()));
                            Log.d("TAG TAGS",String.valueOf(Tags.size()));
                            Log.d("TAG isAdultContent",String.valueOf(isAdultContent));
                            Log.d("TAG DESPCRIPTION",description);
                            speakResults();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                protected void onProgressUpdate(String... values) {
                    progressDialog.setMessage(values[0]);

                }

            };
            visionTask.execute(inputStream);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);

    }

    private void speakResults(){
        finalResult.append("This Photo Consists Of ");
        finalResult.append(description);
        finalResult.append("This Photo Have Following Objects in it ");
        for(int i = 0 ; i<Tags.size();i++){
            finalResult.append(Tags.get(i)+ ",");
        }

        finalResult.append(isAdultContent?" This Photo Have Adult Content in it ":" This Photo Doesn't Have Adult Content in it ");

        if(Faces.size() !=0) {
            finalResult.append("This Photo Consists Of "+Faces.size()+" People in it ");
            Iterator myVeryOwnIterator = Faces.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                String value = (String) Faces.get(key);
                finalResult.append(key+" "+value);
            }
        }
        image.setImageBitmap(bitmap);
        txtResult.setText(finalResult.toString());

        try {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("TAG", "onInit succeeded");

                        speakData(finalResult.toString() + DETECT_OBJ);
                    }
                } else {
                    Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show();
                }

            });

        } catch (Exception e) {
            Log.d("TAG ERROR", e.getMessage());
        }
    }

    void speakData(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            textToSpeech.setSpeechRate(1);
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            textToSpeech.setSpeechRate(1);
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, param);

        }
    }



}