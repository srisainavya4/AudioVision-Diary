package com.example.visuallyimpaired.Activities;

import static com.example.visuallyimpaired.Utility.Constants.ADD_PERSON;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DetectPersonActivity extends AppCompatActivity {

    RelativeLayout detectPersonLay;
    ArrayList<PersonModel> personModels = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);
    Uri mImageUri;
    int count = 0;
    private Bitmap mBitmap;
    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

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
        setContentView(R.layout.activity_detect_person);


        try {
            tfLite=new Interpreter(loadModelFile(DetectPersonActivity.this,modelFile));

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
        detectPersonLay = findViewById(R.id.detectPersonLay);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("photoUri")) {
            mImageUri = Uri.parse(extras.getString("photoUri"));
        }
        verifyPerson();
    }

    private void verifyPerson() {
        try {
            personModels = db.getAllPerson();
            InputStream ims = getContentResolver().openInputStream(mImageUri);
            mBitmap = BitmapFactory.decodeStream(ims);

//            mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.srk);

            Detectface(mBitmap);
        } catch (Exception e) {
            Log.d("TAG VERIFY", e.getMessage());
        }
    }

    Interpreter tfLite;
    static String modelFile="mobile_face_net.tflite";
    FaceDetector detector;
    float[][] embeedings;

    public void Detectface(Bitmap b)
    {
        InputImage img = InputImage.fromBitmap(b,0);
        Task<List<com.google.mlkit.vision.face.Face>> result=detector.process(img)
                .addOnCompleteListener(new OnCompleteListener<List<com.google.mlkit.vision.face.Face>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Face>> task) {
                        if(task.getResult().size()>0)
                        {
                            Bitmap fbt= FaceHelper.getScalledface(b,task.getResult().get(0));
                            if(fbt!=null)
                            {
                                mBitmap=fbt;
                                embeedings=FaceHelper.getEmbeedings(DetectPersonActivity.this,tfLite,mBitmap);
                                updateEmbeedings();
                            }
                            else {
                                Helper.speak(DetectPersonActivity.this,"No Face Detected",false);
                            }
                        }
                        else
                        {
                            Helper.speak(DetectPersonActivity.this,"No Face Detected",false);
                        }
                    }
                });
    }

    private void setUiAfterVerification(VerifyResult result) {

        if (result != null) {
            if (result.isIdentical) {
                DecimalFormat formatter = new DecimalFormat("#0.00");
                Helper.speak(this,"PERSON MATCHED BY "+ personModels.get(count-1).getPersonName()+" By " + formatter.format(result.confidence)+"%",false);
                finish();
            } else {
                if(count < personModels.size()){
                    verifyPerson();
                }else{
                    Helper.speak(this, "PERSON NOT MATCHED", false);
                    finish();
                }
            }
        }else{
            if(count < personModels.size()){
                verifyPerson();
            }else{
                Helper.speak(this, "PERSON NOT MATCHED", false);
                finish();
            }
        }
    }

    public void updateEmbeedings()
    {
        try {
            for(int i=0;i<personModels.size();i++)
            {
                Bitmap decodedByte = Helper.getImage(personModels.get(i).getPersonBitmap());
                float[][] e=FaceHelper.getEmbeedings(DetectPersonActivity.this,tfLite,decodedByte);
                personModels.get(i).setEmbeedings(e);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        verifywithdata();
    }

    public void verifywithdata()
    {
        float distance_local = Float.MAX_VALUE;
        String name="";
        for (PersonModel pm : personModels) {

            float[][] registeredEmbeddings = pm.getEmbeedings();
            float distance = FaceHelper.finddistance(embeedings, registeredEmbeddings);
            if (distance <= distance_local) {
                distance_local = distance;
                name=pm.getPersonName();
            }
        }

        if(name.length()>0)
        {
            Helper.speak(this,"PERSON MATCHED BY "+ name,false);
//            finish();
        }
        else {
            Helper.speak(this, "PERSON NOT MATCHED", false);
//            finish();
        }
    }

}