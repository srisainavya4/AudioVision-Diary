package com.example.visuallyimpaired.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.visuallyimpaired.Utility.Helper;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
//                Helper.speak(context,"INCOMING CALL FROM"+incomingNumber,false);
                if(state == 1) {
                    if (!incomingNumber.isEmpty()) {
                        broadcastIntent(context, "INCOMING CALL FROM" + incomingNumber);
                    }
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void broadcastIntent(Context context,String number){
        Intent intent = new Intent();
        intent.setAction("msg");
        intent.putExtra("call",number);
        context.sendBroadcast(intent);
    }

}
