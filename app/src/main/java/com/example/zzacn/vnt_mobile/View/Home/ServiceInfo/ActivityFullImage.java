package com.example.zzacn.vnt_mobile.View.Home.ServiceInfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;

import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;

public class ActivityFullImage extends AppCompatActivity {

    ImageView imgFullView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        imgFullView = findViewById(R.id.imageFullView);

        String[] imgDetail = getIntent().getStringArrayExtra("img");
        imgFullView.setImageBitmap(setImage(Config.URL_HOST + imgDetail[0], imgDetail[1], imgDetail[2]));
    }
}
