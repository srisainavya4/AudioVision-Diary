package com.example.visuallyimpaired.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.visuallyimpaired.Adaptor.TabAdaptor;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static com.example.visuallyimpaired.Utility.Constants.CONTACT_MSG;
import static com.example.visuallyimpaired.Utility.Constants.GO_BACK_MESSAGE;
import static com.example.visuallyimpaired.Utility.Constants.TAB_MSG;
import static com.example.visuallyimpaired.Utility.Constants.TAB_MSG_1;
import static com.example.visuallyimpaired.Utility.Constants.TAB_MSG_2;
import static com.example.visuallyimpaired.Utility.Constants.TAB_MSG_3;

public class TabForContactActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_for_contact);
        getSupportActionBar().hide();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.speak(this,TAB_MSG +"\n"+ GO_BACK_MESSAGE,true);
    }

    private void initUI(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Contact's"));
        tabLayout.addTab(tabLayout.newTab().setText("Call Logs"));
        tabLayout.addTab(tabLayout.newTab().setText("Call Dialer"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        final TabAdaptor adapter = new TabAdaptor(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    Helper.speak(TabForContactActivity.this,TAB_MSG_1,true);
                }else if(tab.getPosition() == 1){
                    Helper.speak(TabForContactActivity.this,TAB_MSG_2,true);
                }else {
                    Helper.speak(TabForContactActivity.this,TAB_MSG_3,true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
