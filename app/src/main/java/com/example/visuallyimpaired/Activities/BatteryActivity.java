package com.example.visuallyimpaired.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.Objects;

import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.example.visuallyimpaired.Utility.Constants.GO_BACK_MESSAGE;

public class BatteryActivity extends AppCompatActivity {

    TextView txtNumber;
    CircularSeekBar csIndicator;
    Context context;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    BatteryManager myBatteryManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        getSupportActionBar().setTitle("Battery");
        initUI();
    }

    private void initUI(){
        context = this;

        csIndicator = findViewById(R.id.csIndicator);
        txtNumber = findViewById(R.id.txtNumberBT);

        myBatteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = myBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        csIndicator.setProgress(batLevel);

        txtNumber.setText(batLevel +"%");
        if(isUSBCharging()) {
            Helper.speak(context, "Battery Level Is " + batLevel + "% AND DEVICE IS CHARGING" +"\n" + GO_BACK_MESSAGE, true);
        }else{
            Helper.speak(context, "Battery Level Is " + batLevel + "% AND DEVICE IS NOT CHARGING" +"\n"  + GO_BACK_MESSAGE, true);

        }

    }

    public boolean isUSBCharging(){
        return  myBatteryManager.isCharging();
    }


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

}