package com.example.visuallyimpaired.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visuallyimpaired.Activities.ComposeMsgActivity;
import com.example.visuallyimpaired.Models.Contact;
import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import java.util.ArrayList;

public class ContactAdaptor extends RecyclerView.Adapter<ContactAdaptor.ViewHolder>{

    private final Context context;
    private ArrayList<Contact> contactArrayList = null;
    int pos = 0;
    boolean isClicked = false;
    boolean isLongPressed = false;

    public ContactAdaptor(ArrayList<Contact> contactArrayList, Context context) {
        this.contactArrayList = contactArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.contact_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int tempPost = position;
        holder.txtContactNumber.setText("Contact Name: " + contactArrayList.get(position).getName());
        holder.txtContactName.setText("Contact Number: " + contactArrayList.get(position).getNumber());
        holder.contactCard.setOnClickListener(v -> {
            if(isClicked){
                if(tempPost == pos){
                    isClicked = false;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contactArrayList.get(position).getNumber()));
                    context.startActivity(intent);
                }else{
                    String msg = "YOU HAVE CLICKED " + contactArrayList.get(position).getName() + ",CLICK AGAIN TO CALL HIM";
                    Helper.speak(context, msg,true);
                    pos = tempPost;
                    isClicked = true;
                }
            }else {
                String msg = "YOU HAVE CLICKED " + contactArrayList.get(position).getName() + ",CLICK AGAIN TO CALL HIM";
                Helper.speak(context, msg,true);
                pos = tempPost;
                isClicked = true;
            }
        });

        holder.contactCard.setOnLongClickListener(v -> {
            if(isLongPressed) {
                isLongPressed = false;
                Intent intent = new Intent(context, ComposeMsgActivity.class);
                intent.putExtra("Number", contactArrayList.get(position).getNumber());
                context.startActivity(intent);
                return false;
            }else{
                String msg = "YOU HAVE CLICKED " + contactArrayList.get(position).getName() + ",LONG CLICK AGAIN TO Message HIM";
                Helper.speak(context, msg,true);
                isLongPressed = true;
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactNumber, txtContactName;
        CardView contactCard;

        public ViewHolder(View view) {
            super(view);
            this.txtContactNumber = view.findViewById(R.id.txtContactNumber);
            this.txtContactName = view.findViewById(R.id.txtContactName);
            this.contactCard = view.findViewById(R.id.contactCard);
        }
    }
}
