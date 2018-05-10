package com.example.zzacn.vnt_mobile.View.Home.ServiceInfo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfReviewAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.Review;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;


public class ActivityReviewList extends AppCompatActivity {
    ArrayList<String> finalArr = new ArrayList<>();
    ImageView btnnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewlist);

        btnnBack = findViewById(R.id.button_Back);

        btnnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int id = getIntent().getIntExtra("id", 1);
        load(Config.URL_HOST + Config.URL_GET_ALL_REVIEWS + id, Config.GET_KEY_JSON_ALL_REVIEW);

    }

    private void load(String url, final ArrayList<String> formatJson) {

        final RecyclerView recyclerView = findViewById(R.id.RecyclerView_ReviewList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Review> reviews = new ModelService().getReviewList(url, formatJson);

        final ListOfReviewAdapter listOfReviewAdapter =
                new ListOfReviewAdapter(recyclerView, reviews, getApplicationContext());
        recyclerView.setAdapter(listOfReviewAdapter);
        listOfReviewAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        final ArrayList<Review> finalListService = reviews;
        try {
            finalArr = parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        listOfReviewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            listOfReviewAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            listOfReviewAdapter.notifyItemRemoved(finalListService.size());

                            ArrayList<Review> reviewArrayList = new ModelService().getReviewList(finalArr.get(1), formatJson);
                            for (int i = 0; i < reviewArrayList.size(); i++) {
                                finalListService.add(reviewArrayList.get(i));
                            }
                            try {
                                finalArr = parseJsonNoId(new JSONObject
                                        (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()), Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            listOfReviewAdapter.notifyDataSetChanged();
                            listOfReviewAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }
}
