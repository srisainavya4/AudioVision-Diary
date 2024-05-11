package com.example.visuallyimpaired.Activities;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visuallyimpaired.Adaptor.CallLogsAdaptor;
import com.example.visuallyimpaired.Adaptor.ContactAdaptor;
import com.example.visuallyimpaired.Models.CallLogModel;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CallLogFragment extends Fragment {

    TextView txtNoResult;
    RecyclerView callLogRV;
    ArrayList<CallLogModel> callLogModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_log, container, false);
        initUI(view);
        return  view;
    }

    private void initUI(View view){
        txtNoResult = view.findViewById(R.id.txtNoResult);
        callLogRV = view.findViewById(R.id.callLogRV);
        getCallDetails();
    }

    private void getCallDetails() {
        callLogModels = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String fDate = simpleDateFormat.format(callDayTime.getTime());
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            callLogModels.add(new CallLogModel(phNumber,dir,callDuration,fDate));

        }

        if (callLogModels.size() > 0) {
            txtNoResult.setVisibility(View.GONE);
            callLogRV.setVisibility(View.VISIBLE);
            CallLogsAdaptor listAdapters = new CallLogsAdaptor(callLogModels, getContext());
            callLogRV.setHasFixedSize(true);
            callLogRV.setLayoutManager(new LinearLayoutManager(getContext()));
            callLogRV.setAdapter(listAdapters);
        } else {
            txtNoResult.setVisibility(View.VISIBLE);
            callLogRV.setVisibility(View.GONE);
        }

    }
}