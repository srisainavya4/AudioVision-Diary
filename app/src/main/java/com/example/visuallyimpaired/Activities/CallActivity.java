package com.example.visuallyimpaired.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.visuallyimpaired.R;
import com.example.visuallyimpaired.Utility.Helper;

import static com.example.visuallyimpaired.Utility.Constants.COMPOSE_MSG;
import static com.example.visuallyimpaired.Utility.Constants.CONTACT_MSG;

public class CallActivity extends Fragment {

    TextView txtNumber;
    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn0;
    ImageView iv_call,iv_back;
    StringBuilder PhoneNumber = new StringBuilder();
    Context context;
    RelativeLayout callLay;
    String finalPhoneNumber = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_call, container, false);
        initUI(view);
        return  view;
    }

    private void initUI(View view){
        context = getContext();

        txtNumber = view.findViewById(R.id.txtNumber);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btn7 = view.findViewById(R.id.btn7);
        btn8 = view.findViewById(R.id.btn8);
        btn9 = view.findViewById(R.id.btn9);
        btn0 = view.findViewById(R.id.btn0);
        iv_call = view.findViewById(R.id.iv_call);
        iv_back = view.findViewById(R.id.iv_back);
        callLay = view.findViewById(R.id.callLay);

        btn0.setOnClickListener(v -> addNumber(0));
        btn1.setOnClickListener(v -> addNumber(1));
        btn2.setOnClickListener(v -> addNumber(2));
        btn3.setOnClickListener(v -> addNumber(3));
        btn4.setOnClickListener(v -> addNumber(4));
        btn5.setOnClickListener(v -> addNumber(5));
        btn6.setOnClickListener(v -> addNumber(6));
        btn7.setOnClickListener(v -> addNumber(7));
        btn8.setOnClickListener(v -> addNumber(8));
        btn9.setOnClickListener(v -> addNumber(9));




        callLay.setOnLongClickListener(v -> {
            if(PhoneNumber.length() != 0){
                finalPhoneNumber = PhoneNumber.toString().replaceAll("\\B|\\b", " ");
                Helper.speak(context,"Number Type By You Is "+ finalPhoneNumber,true);
            }
            return false;
        });


        iv_back.setOnClickListener(v -> {
            if(PhoneNumber.length() !=0) {
                PhoneNumber.deleteCharAt(PhoneNumber.length()-1);
                txtNumber.setText(PhoneNumber);
                Helper.speak(context, "You Have Pressed Backspace",false);

            }
        });

        iv_call.setOnClickListener(v -> {
            if(PhoneNumber.length() == 10) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + PhoneNumber));
                context.startActivity(intent);
            }else{
                Helper.speak(context, "Invalid Phone Number",true);
            }
        });

    }

    private void addNumber(int no){
        if(PhoneNumber.length()<10) {
            Helper.speak(context, String.valueOf(no),false);
            PhoneNumber.append(no);
            txtNumber.setText(PhoneNumber);
        }else{
            Helper.speak(context, "You Have Already Entered 10 Digit",true);
        }
    }
}