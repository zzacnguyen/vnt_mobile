package com.example.zzacn.vnt_mobile.View.Personal.TripSchedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelFavorite;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ActivityTripScheduleInfo extends AppCompatActivity {
    TextView tvTripName, tvStartDate, tvEndDate;
    FloatingActionButton fabAddService;
    RecyclerView recyclerView;
    ArrayList<String> finalArr = new ArrayList<>();
    TripSchedule schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_schedule_info);
        getTripScheduleInfo();
    }

    void getTripScheduleInfo() {
        tvTripName = findViewById(R.id.textView_TripName);
        tvStartDate = findViewById(R.id.textView_StartDate);
        tvEndDate = findViewById(R.id.textView_EndDate);
        fabAddService = findViewById(R.id.fabAddServiceTripSchedule);

        schedules = (TripSchedule) getIntent().getSerializableExtra("schedules");
        recyclerView = findViewById(R.id.RecyclerView_PlaceToVisit);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(ActivityTripScheduleInfo.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getServiceList(String.valueOf(schedules.getTripID()));
        tvTripName.setText(schedules.getTripName());
        tvStartDate.setText(schedules.getTripStartDate());
        tvEndDate.setText(schedules.getTripEndDate());

        fabAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTripScheduleInfo.this, ActivityAddServiceToTripSchedule.class);
                intent.putExtra("id", schedules.getTripID());
                startActivityForResult(intent,11);
            }
        });
    }

    private void getServiceList(String id) {
        String url = Config.URL_HOST + Config.URL_GET_TRIP_SCHEDULE_INFO + id;
        // dùng chung hàm get danh sách yêu thích vì giống nhau chỉ khác file
        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject(new HttpRequestAdapter.httpGet().execute(url).get())
                    , Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList<Service> favoriteList = new ModelFavorite().getFavoriteList(finalArr.get(0));
        final ArrayList<Service> finalListService = favoriteList;

        final ListOfServiceAdapter listOfServiceAdapter =
                new ListOfServiceAdapter(recyclerView, favoriteList, getApplicationContext());
        recyclerView.setAdapter(listOfServiceAdapter);
        listOfServiceAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        listOfServiceAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            listOfServiceAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            listOfServiceAdapter.notifyItemRemoved(finalListService.size());

                            try {
                                finalArr = JsonHelper.parseJsonNoId(new JSONObject
                                        (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()), Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            ArrayList<Service> serviceArrayList = new ModelFavorite().
                                    getFavoriteList(finalArr.get(0));
                            finalListService.addAll(serviceArrayList);


                            listOfServiceAdapter.notifyDataSetChanged();
                            listOfServiceAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 11){
            startActivity(getIntent());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
