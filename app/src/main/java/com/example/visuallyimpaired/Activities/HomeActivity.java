package com.example.visuallyimpaired.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.visuallyimpaired.Models.PersonModel;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.BackgroundService;
import com.example.visuallyimpaired.Utility.DatabaseHandler;
import com.example.visuallyimpaired.Utility.Helper;

import static com.example.visuallyimpaired.Utility.Constants.ANALYSIS_SPEAK;
import static com.example.visuallyimpaired.Utility.Constants.BATTERY_SPEAK;
import static com.example.visuallyimpaired.Utility.Constants.CONTACT_SPEAK;
import static com.example.visuallyimpaired.Utility.Constants.MESSAGE_SPEAK;
import static com.example.visuallyimpaired.Utility.Constants.PEOPLE_SPEAK;
import static com.example.visuallyimpaired.Utility.Constants.WELCOME_MSG;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RelativeLayout HomeScreenLay;
    ImageView iv_contact, iv_msg, iv_people, iv_battery,iv_object_detection;
    Context context;
    boolean isContactClicked, isMsgClicked, isPeopleClicked, isBatteryClicked,isAnalysisClicked = false;
    int PERMISSION_ALL = 1;
    DatabaseHandler db = new DatabaseHandler(this);
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private CallReceiver callReceiver;


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
//        DatabaseHandler db = new DatabaseHandler(this);
//        ArrayList<PersonModel> pm=db.getAllPerson();
//        Toast.makeText(context, ""+pm.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.speak(this, WELCOME_MSG, true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        isContactClicked = false;
        isMsgClicked = false;
        isPeopleClicked = false;
        isBatteryClicked = false;
        isAnalysisClicked = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void initUI() {
        context = this;
        db.getWritableDatabase();
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        callReceiver = new CallReceiver();

        HomeScreenLay = findViewById(R.id.HomeScreenLay);
        iv_contact = findViewById(R.id.iv_contact);
        iv_msg = findViewById(R.id.iv_msg);
        iv_people = findViewById(R.id.iv_people);
        iv_battery = findViewById(R.id.iv_battery);
        iv_object_detection = findViewById(R.id.iv_object_detection);

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        iv_contact.setOnClickListener(v -> {
            if (!isContactClicked) {
                Helper.speak(context, CONTACT_SPEAK, true);
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                isContactClicked = true;
                isMsgClicked = false;
                isPeopleClicked = false;
                isBatteryClicked = false;
                isAnalysisClicked = false;
            } else {
                Helper.startActivity(context, TabForContactActivity.class, false);
            }
        });

        iv_msg.setOnClickListener(v -> {
            if (!isMsgClicked) {
                Helper.speak(context, MESSAGE_SPEAK, true);
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                isMsgClicked = true;
                isContactClicked = false;
                isPeopleClicked = false;
                isBatteryClicked = false;
                isAnalysisClicked = false;
            } else {
                Helper.startActivity(context, MessageActivity.class, false);

            }
        });

        iv_people.setOnClickListener(v -> {
            if (!isPeopleClicked) {
                Helper.speak(context, PEOPLE_SPEAK, true);
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                isPeopleClicked = true;
                isContactClicked = false;
                isMsgClicked = false;
                isBatteryClicked = false;
                isAnalysisClicked = false;
            } else {
                Helper.startActivity(context, FaceSelectionActivity.class, false);

            }
        });

        iv_battery.setOnClickListener(v -> {
            if (!isBatteryClicked) {
                Helper.speak(context, BATTERY_SPEAK, true);
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                isBatteryClicked = true;
                isContactClicked = false;
                isMsgClicked = false;
                isPeopleClicked = false;
                isAnalysisClicked = false;
            } else {
                Helper.startActivity(context, BatteryActivity.class, false);
            }
        });

        iv_object_detection.setOnClickListener(v -> {
            if (!isAnalysisClicked) {
                Helper.speak(context, ANALYSIS_SPEAK, true);
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                isContactClicked = false;
                isMsgClicked = false;
                isPeopleClicked = false;
                isBatteryClicked = false;
                isAnalysisClicked = true;
            } else {
                Intent intent = new Intent(this,CameraActivity.class);
                intent.putExtra("isAddActivity",3);
                startActivity(intent);
            }
        });


        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


    }

}