package com.example.visuallyimpaired.Activities;

import static com.example.visuallyimpaired.Utility.Constants.ADD_PERSON;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visuallyimpaired.Models.PersonModel;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.DatabaseHandler;
import com.example.visuallyimpaired.Utility.FaceHelper;
import com.example.visuallyimpaired.Utility.Helper;
import com.example.visuallyimpaired.Utility.ImageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;



public class AddPersonActivity extends AppCompatActivity {
    ImageView image;
    EditText etPersonName;
    LinearLayout addPersonLay;
    private Uri mImageUri;
    private Bitmap mBitmap;
    Context context;
    DatabaseHandler db = new DatabaseHandler(this);
    boolean isFaceDetected = false;
    PersonModel personModel;
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

    Interpreter tfLite;
    static String modelFile="mobile_face_net.tflite";

    FaceDetector detector;

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        try {
            tfLite=new Interpreter(loadModelFile(AddPersonActivity.this,modelFile));

            FaceDetectorOptions highAccuracyOpts =
                    new FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                            .build();
            detector = FaceDetection.getClient(highAccuracyOpts);

        } catch (Exception e) {
            Toast.makeText(this,"exception-"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        initUI();
    }

    private void initUI() {
        context = this;
        image = findViewById(R.id.image);
        etPersonName = findViewById(R.id.etPersonName);
        addPersonLay = findViewById(R.id.addPersonLay);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("photoUri")) {
            mImageUri = Uri.parse(extras.getString("photoUri"));
        }
        setImage();

        addPersonLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (!etPersonName.getText().toString().isEmpty() && isFaceDetected) {
                        savePerson();
                    }
                    return super.onDoubleTap(e);
                }
            });
        });

        etPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Helper.speak(context, String.valueOf(s.charAt(s.length() - 1)), false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ImageUri", mImageUri);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mImageUri = savedInstanceState.getParcelable("ImageUri");
        if (mImageUri != null) {
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
        }
    }

    private void setImage() {
        try {
            InputStream ims = getContentResolver().openInputStream(mImageUri);
            mBitmap = BitmapFactory.decodeStream(ims);
        } catch (Exception e) {
            Log.d("TAG ROTATE", e.getMessage());
        }
        if (mBitmap != null) {
            image.setImageBitmap(mBitmap);
            Detectface(mBitmap);
        }
    }

    private void savePerson() {
        byte[] image = Helper.getBytes(mBitmap);
        personModel = new PersonModel(etPersonName.getText().toString(), image);
        db.addPerson(AddPersonActivity.this,personModel);
        Helper.speak(context, "Data Saved Successfully", false);
        finish();
    }

    public void Detectface(Bitmap b)
    {
        InputImage img = InputImage.fromBitmap(b,0);
        Task<List<Face>> result=detector.process(img)
                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Face>> task) {
                        if(task.getResult().size()>0)
                        {
                            Bitmap fbt= FaceHelper.getScalledface(b,task.getResult().get(0));
                            if(fbt!=null)
                            {
                                mBitmap=fbt;
                                image.setImageBitmap(fbt);
                                isFaceDetected=true;
                                Helper.speak(context,ADD_PERSON,false);
                            }
                            else {
                                Helper.speak(context,"No Face Detected",false);
                            }
                        }
                        else
                        {
                            Helper.speak(context,"No Face Detected",false);
                        }
                    }
                });
    }

}