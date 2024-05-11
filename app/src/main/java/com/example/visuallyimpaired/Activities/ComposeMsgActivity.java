package com.example.visuallyimpaired.Activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.Objects;

import static com.example.visuallyimpaired.Utility.Constants.COMPOSE_MSG;
import static com.example.visuallyimpaired.Utility.Constants.GO_BACK_MESSAGE;
import static com.example.visuallyimpaired.Utility.Constants.SEND_MSG;

public class ComposeMsgActivity extends AppCompatActivity {

    EditText etSenderNumber, etMsg;
    Context context;
    RelativeLayout smsLay;
    String Number;
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
        setContentView(R.layout.activity_compose_msg);
        getSupportActionBar().setTitle("Compose New Message");
        initUI();
    }

    private boolean isValidate() {
        if (etSenderNumber.getText().toString().isEmpty()) {
            Helper.speak(context, "Sender Phone Number Cannot Be Empty", true);
            return false;
        }
        if (etSenderNumber.getText().length() < 10) {
            Helper.speak(context, "Invalid Phone Number", true);
            return false;
        }
        if (etMsg.getText().toString().isEmpty()) {
            Helper.speak(context, "Message Cannot Be Empty", true);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Number = getIntent().getStringExtra("Number");
        if (Number != null) {
            etSenderNumber.setText(Number);
            etMsg.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            Helper.speakWithoutSkipping(context, COMPOSE_MSG +GO_BACK_MESSAGE +"\n"+ "FOCUS IS ON MESSAGE TEXT BOX,TYPE YOUR MESSAGE," + "\n"+ SEND_MSG);

        } else {
            etSenderNumber.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            Helper.speakWithoutSkipping(context, COMPOSE_MSG + GO_BACK_MESSAGE + "\n"+ "FOCUS IS ON PHONE NUMBER TEXT BOX,TYPE THE PHONE NUMBER," + "\n"+ SEND_MSG);
        }
    }

    private void initUI() {
        context = this;


        etSenderNumber = findViewById(R.id.etSenderNumber);
        etMsg = findViewById(R.id.etMsg);
        smsLay = findViewById(R.id.smsLay);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;




        smsLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public void onLongPress(MotionEvent e) {
                    if (!etMsg.getText().toString().isEmpty() && !etSenderNumber.getText().toString().isEmpty()) {
                        Helper.speak(context, "Message Type By You Is " + etMsg.getText().toString(), true);
                    }
                    super.onLongPress(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (isValidate()) {

                    sendSMS(etSenderNumber.getText().toString(), etMsg.getText().toString());
                }
                    return super.onDoubleTap(e);
                }
            });
        });



        etSenderNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSenderNumber.getText().length() != 10) {
                    Helper.speak(context, String.valueOf(s.charAt(s.length() - 1)), false);
                }else{
                    Helper.speak(context, "10 Number Already Entered", false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMsg.addTextChangedListener(new TextWatcher() {
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
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
            Helper.speak(context,"Message Sended Successfully",false);
            finish();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}