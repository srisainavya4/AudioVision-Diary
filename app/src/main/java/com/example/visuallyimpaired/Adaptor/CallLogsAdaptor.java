package com.example.visuallyimpaired.Adaptor;

import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visuallyimpaired.Models.CallLogModel;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.ArrayList;

public class CallLogsAdaptor extends RecyclerView.Adapter<CallLogsAdaptor.ViewHolder>{
    private final Context context;
    private ArrayList<CallLogModel> callLogModels = null;

    public CallLogsAdaptor(ArrayList<CallLogModel> callLogModels, Context context) {
        this.callLogModels = callLogModels;
        this.context = context;
    }

    @NonNull
    @Override
    public CallLogsAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.call_logs_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogsAdaptor.ViewHolder holder, int position) {
        holder.txtContactNumber.setText("Contact Number: " + callLogModels.get(position).getContactNumber());
        holder.txtCallType.setText("Call Type: " + callLogModels.get(position).getCallType());
        holder.txtCallDuration.setText("Call Duration: " + callLogModels.get(position).getCallDuration()+" Minutes");
        holder.txtCallTime.setText("Call Time:" + callLogModels.get(position).getCallTime());
        holder.callLogCard.setOnClickListener(v -> {
            String dt = callLogModels.get(position).getCallTime().replaceAll("-","");
            String msg = callLogModels.get(position).getCallType()+"CALL WAS MADE TO "+callLogModels.get(position).getContactNumber() + " ON "+ dt;
            Helper.speak(context,msg,true);
        });
    }

    @Override
    public int getItemCount() {
        return callLogModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactNumber, txtCallType,txtCallDuration,txtCallTime;
        CardView callLogCard;

        public ViewHolder(View view) {
            super(view);
            this.txtContactNumber = view.findViewById(R.id.txtContactNumber);
            this.txtCallType = view.findViewById(R.id.txtCallType);
            this.txtCallDuration = view.findViewById(R.id.txtCallDuration);
            this.txtCallTime = view.findViewById(R.id.txtCallTime);
            this.callLogCard = view.findViewById(R.id.callLogCard);
        }
    }
}
