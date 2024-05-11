package com.example.visuallyimpaired.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.visuallyimpaired.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Helper {

    final static String TAG = "RESPONSE:-";
    private static ProgressDialog progressDialog;
    private static TextToSpeech textToSpeech;

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getFormattedTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void startActivity(Context context, Class<?> activity, boolean isFinish) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
        if (isFinish) {
            ((Activity) context).finish();
        }
    }

    public static void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public static void speak(Context context, String data,boolean isStopSpeaking) {
        if(isStopSpeaking) {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }
        try {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v(TAG, "onInit succeeded");

                        speakData(data);
                    }
                } else {
                    Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Log.d("TAG ERROR", e.getMessage());
        }
    }

    public static void speakWithoutSkipping(Context context, String data) {
        try {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v(TAG, "onInit succeeded");

                        speakData(data);
                    }
                } else {
                    Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Log.d("TAG ERROR", e.getMessage());
        }
    }


    static void speakData(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            textToSpeech.setSpeechRate(1);
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            textToSpeech.setSpeechRate(1);
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, param);
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
