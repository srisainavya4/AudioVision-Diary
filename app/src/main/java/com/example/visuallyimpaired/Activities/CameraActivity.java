package com.example.visuallyimpaired.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;

import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.PermissionUtils;

import java.io.File;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 0;
    private Uri photoUri;
    int isAddActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        isAddActivity = getIntent().getIntExtra("isAddActivity",0);
        takePhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
//                    Intent intent = new Intent();
//                    intent.setData(photoUri);
//                    setResult(RESULT_OK, intent);
                    if(isAddActivity == 0) {
                        Intent intent = new Intent();
                        intent.setClass(this, AddPersonActivity.class);
                        intent.putExtra("photoUri", photoUri.toString());
                        startActivity(intent);
                        finish();
                    }else if(isAddActivity == 1){
                        Intent intent = new Intent();
                        intent.setClass(this, DetectPersonActivity.class);
                        intent.putExtra("photoUri", photoUri.toString());
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent();
                        intent.setClass(this, ObjectDetectionActivity.class);
                        intent.putExtra("photoUri", photoUri.toString());
                        startActivity(intent);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void takePhoto() {

        if (PermissionUtils.requestPermission(
                this,
                REQUEST_TAKE_PHOTO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

        public File getCameraFile() {
            File dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return new File(dir, "temp.jpg");
        }


}