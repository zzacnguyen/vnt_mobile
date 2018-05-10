package com.example.zzacn.vnt_mobile.View.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.R;


public class ActivityUpgradeMember extends AppCompatActivity implements View.OnClickListener {

    TextView btnCancel, btnSend;
    RadioGroup rgUserType;
    RadioButton rbTourGuide, rbEnterprise, rbPartner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrademember);

        btnCancel = findViewById(R.id.button_Cancel);
        btnSend = findViewById(R.id.button_Send);
        rgUserType = findViewById(R.id.radioGroup_UserType);
        rbTourGuide = findViewById(R.id.radioButton_TourGuide);
        rbEnterprise = findViewById(R.id.radioButton_Enterprise);
        rbPartner = findViewById(R.id.radioButton_Partner);

        btnCancel.setOnClickListener(this); //Gọi sự kiện click
    }

    @Override
    public void onClick(View view) { //Set sự kiện click dựa vào id
        switch (view.getId()){
            case R.id.button_Cancel:
                finish();
                break;
            case R.id.button_Send:
                break;
        }
    }
}
