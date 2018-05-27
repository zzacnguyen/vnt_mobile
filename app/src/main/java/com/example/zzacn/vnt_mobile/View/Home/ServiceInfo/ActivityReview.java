package com.example.zzacn.vnt_mobile.View.Home.ServiceInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class ActivityReview extends AppCompatActivity {
    Button btnSend, btnCancel;
    TextView tvTitle, tvReview;
    RatingBar rbRating;
    int id;
    String idReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        btnSend = findViewById(R.id.button_Send);
        btnCancel = findViewById(R.id.button_Cancel);
        rbRating = findViewById(R.id.ratingBar_Review);
        tvTitle = findViewById(R.id.editText_Title);
        tvReview = findViewById(R.id.textView_Comment);

        id = getIntent().getIntExtra("id", 1);
        idReview = getIntent().getStringExtra("idRating");
        if (!idReview.equals("0")) {
            try {
                String rs =
                        new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_REVIEW + "/" + idReview).get();
                ArrayList<String> arr = JsonHelper.parseJsonNoId(new JSONArray(rs), Config.GET_KEY_JSON_REVIEW);
                rbRating.setRating(Float.parseFloat(arr.get(0)));
                tvTitle.setText(arr.get(1));
                tvReview.setText(arr.get(2));
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        }
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvTitle.getText().toString().equals("")
                        && tvReview.getText().toString().equals("")
                        && (int) rbRating.getRating() == 0) {
                    Toast.makeText(ActivityReview.this, getResources().getString(R.string.text_PleaseReviewBeforeSend), Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject json = null;
                    if (idReview.equals("0")) {
                        try {
                            json = new JSONObject("{"
                                    + Config.POST_KEY_JSON_REVIEW.get(0) + ":\"" + id + "\","
                                    + Config.POST_KEY_JSON_REVIEW.get(1) + ":\"" + userId + "\","
                                    + Config.POST_KEY_JSON_REVIEW.get(2) + ":\"" + (int) rbRating.getRating() + "\","
                                    + Config.POST_KEY_JSON_REVIEW.get(3) + ":\"" + tvTitle.getText() + "\","
                                    + Config.POST_KEY_JSON_REVIEW.get(4) + ":\"" + tvReview.getText() + "\"}");
                            String stt = new HttpRequestAdapter.httpPost(json)
                                    .execute(Config.URL_HOST + Config.URL_POST_REVIEW).get();
                            if (stt.equals("\"status:200\""))
                                intend(getResources().getString(R.string.text_ReviewCompleted));
                            else
                                Toast.makeText(ActivityReview.this,
                                        getResources().getString(R.string.text_ReviewFailure), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException | ExecutionException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            json = new JSONObject("{"
                                    + Config.PUT_KEY_JSON_REVIEW.get(0) + ":\"" + (int) rbRating.getRating() + "\","
                                    + Config.PUT_KEY_JSON_REVIEW.get(1) + ":\"" + tvTitle.getText() + "\","
                                    + Config.PUT_KEY_JSON_REVIEW.get(2) + ":\"" + tvReview.getText() + "\"}");
                            String stt = new HttpRequestAdapter.httpPost(json)
                                    .execute(Config.URL_HOST + Config.URL_PUT_REVIEW + idReview).get();
                            if (stt.equals("\"status:200\""))
                                intend(getResources().getString(R.string.text_ReviewCompleted));
                            else
                                Toast.makeText(ActivityReview.this,
                                        getResources().getString(R.string.text_ReviewFailure), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException | ExecutionException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void intend(String mess) {
        Intent intent = new Intent();
        intent.putExtra("mess", mess);
        setResult(RESULT_OK, intent);
        finishActivity(1);
        finish();
    }
}
