package com.example.visuallyimpaired.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.visuallyimpaired.Models.PersonModel;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.DatabaseHandler;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.visuallyimpaired.Utility.Constants.ADD_PERSON_TO_CONTINUE;
import static com.example.visuallyimpaired.Utility.Constants.PERSON_SCREEN_INFO;

public class FaceSelectionActivity extends AppCompatActivity {

    LinearLayout addPersonLay,detectPersonLay;
    Context context;
    ArrayList<PersonModel> personModels = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);
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
        setContentView(R.layout.activity_face_selection);
        initUI();
    }

    private void initUI(){
        context = this;
        addPersonLay = findViewById(R.id.addPersonLay);
        detectPersonLay = findViewById(R.id.detectPersonLay);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        Helper.speak(context,PERSON_SCREEN_INFO,true);

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
                    Intent intent = new Intent(context,CameraActivity.class);
                    intent.putExtra("isAddActivity",0);
                    startActivity(intent);
                    return super.onDoubleTap(e);
                }

            });
        });

        detectPersonLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    personModels = db.getAllPerson();
                    if (personModels.isEmpty()) {
                        Helper.speak(context,ADD_PERSON_TO_CONTINUE,false);
                    } else {
                        Intent intent = new Intent(context, CameraActivity.class);
                        intent.putExtra("isAddActivity", 1);
                        startActivity(intent);
                    }
                    return super.onDoubleTap(e);
                }

            });
        });

    }
}