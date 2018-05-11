package com.example.zzacn.vnt_mobile.View.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.R;


public class ActivityUpgradeMember extends AppCompatActivity{

    ImageView btnBack;
    Button btnUpgrade;
    RadioGroup rgUserType;
    RadioButton rbTourGuide, rbEnterprise, rbPartner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrademember);

        btnBack = findViewById(R.id.button_Back);
        rgUserType = findViewById(R.id.radioGroup_UserType);
        rbTourGuide = findViewById(R.id.radioButton_TourGuide);
        rbEnterprise = findViewById(R.id.radioButton_Enterprise);
        rbPartner = findViewById(R.id.radioButton_Partner);
        btnUpgrade = findViewById(R.id.button_Upgrade);
    }
}
