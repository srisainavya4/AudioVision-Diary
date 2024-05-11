package com.example.visuallyimpaired.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visuallyimpaired.Activities.ComposeMsgActivity;
import com.example.visuallyimpaired.Models.CallLogModel;
import com.example.visuallyimpaired.Models.Message;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.ArrayList;

public class MessageAdaptor   extends RecyclerView.Adapter<MessageAdaptor.ViewHolder>{

    private final Context context;
    private ArrayList<Message> messages = null;

    public MessageAdaptor(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.message_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdaptor.ViewHolder holder, int position) {
        holder.txtTitle.setText("Title: " + messages.get(position).getTitle());
        holder.txtBody.setText("Message: " + messages.get(position).getBody());
        holder.txtDt.setText("Date & Time: " + messages.get(position).getDt());
        holder.msgCard.setOnClickListener(v -> {
            String msg = messages.get(position).getTitle() +" HAS SEND YOU A MESSAGE AT "+messages.get(position).getDt()+" STATING THAT "
                    +  messages.get(position).getBody();
            Helper.speak(context,msg,true);
        });

        holder.msgCard.setOnLongClickListener(v -> {
            Helper.startActivity(context, ComposeMsgActivity.class, false);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtBody,txtDt;
        CardView msgCard;

        public ViewHolder(View view) {
            super(view);
            this.txtTitle = view.findViewById(R.id.txtTitle);
            this.txtBody = view.findViewById(R.id.txtBody);
            this.txtDt = view.findViewById(R.id.txtDt);
            this.msgCard = view.findViewById(R.id.msgCard);
        }
    }
}
