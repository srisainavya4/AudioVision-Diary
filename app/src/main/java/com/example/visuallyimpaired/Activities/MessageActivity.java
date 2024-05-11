package com.example.visuallyimpaired.Activities;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visuallyimpaired.Adaptor.MessageAdaptor;
import com.example.visuallyimpaired.Models.Message;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.example.visuallyimpaired.Utility.Constants.GO_BACK_MESSAGE;
import static com.example.visuallyimpaired.Utility.Constants.MSG;

public class MessageActivity extends AppCompatActivity {

    RecyclerView msgRV;
    TextView txtNoResult;
    ArrayList<Message> messages;
    Context context;
    RelativeLayout msgLay;
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
        setContentView(R.layout.activity_message);
        getSupportActionBar().setTitle("Message's");
        initUI();

    }

    private void initUI() {
        context = this;
        Helper.speak(context,MSG+ GO_BACK_MESSAGE, false);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        msgRV = findViewById(R.id.msgRV);
        txtNoResult = findViewById(R.id.txtNoResult);
        msgLay = findViewById(R.id.msgLay);

        msgLay.setOnLongClickListener(v -> {
            Helper.startActivity(context, ComposeMsgActivity.class, false);
            return false;
        });

        getSMS();
    }

    public void getSMS() {
        messages = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            String datetime = cur.getString(cur.getColumnIndexOrThrow("date_sent"));
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM 'at' hh:mm");
            String dateString = formatter.format(new Date(Long.parseLong(datetime)));

            Log.d("TAG", dateString);
            messages.add(new Message(address, body, dateString));
        }
        if (messages.size() > 0) {
            txtNoResult.setVisibility(View.GONE);
            msgRV.setVisibility(View.VISIBLE);
            MessageAdaptor listAdapters = new MessageAdaptor(messages, context);
            msgRV.setHasFixedSize(true);
            msgRV.setLayoutManager(new LinearLayoutManager(context));
            msgRV.setAdapter(listAdapters);
        } else {
            txtNoResult.setVisibility(View.VISIBLE);
            msgRV.setVisibility(View.GONE);
        }

    }
}