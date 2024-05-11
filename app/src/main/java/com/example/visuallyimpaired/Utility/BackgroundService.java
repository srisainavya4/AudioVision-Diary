package com.example.visuallyimpaired.Utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.visuallyimpaired.Activities.HomeActivity;
import com.example.visuallyimpaired.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    public static String CHANNEL_ID = "";
    String TAG = "RESPONSE:-";
    Handler hand = new Handler();
    private TimerTask timerTask;
    private Timer timer;
    Context mContext;
    BatteryManager myBatteryManager;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void createNotificationChannel(@NonNull Context context, @NonNull String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Covid Geo Fence App";
            String description = "Background Location";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.d("NotificationLog", "NotificationManagerNull");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startServices();
        mContext = this;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                hand.post(new TimerTask() {
                    @Override
                    public void run() {
                        myBatteryManager = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
                        int batLevel = myBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                        if(batLevel < 15 && !isUSBCharging()){
                            Log.d("TAG","15 Charge");
                            Helper.speak(mContext,"Battery is Low,Charge your device",false);
                        }else if(batLevel < 5 && !isUSBCharging()){
                            Helper.speak(mContext,"Battery is Critically Low,Charge your device as soon as possible",false);
                            Log.d("TAG","5 Charge");

                        }else if(batLevel == 100 && isUSBCharging()){
                            Log.d("TAG","100 Charge");
                            Helper.speak(mContext,"Battery is 100% Remove your charger",false);
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1800000);
        registerReceiver(message, new IntentFilter("msg"));
    }

    private BroadcastReceiver message = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String ans=intent.getStringExtra("call");
            if(textToSpeech!=null) {
                textToSpeech.stop();
            }
            textToSpeech=new TextToSpeech(BackgroundService.this, i -> {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setPitch(1);
                textToSpeech.setSpeechRate(0);
                textToSpeech.speak(ans, TextToSpeech.QUEUE_FLUSH, null);
            });
        }
    };

    private void startServices() {

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel(getApplicationContext(), CHANNEL_ID);

        Notification notification = new NotificationCompat
                .Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("TEST")
                .setContentIntent(pendingIntent)
                //.setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(false)
                .build();

        startForeground(123, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    public boolean isUSBCharging(){
        return  myBatteryManager.isCharging();
    }

}
