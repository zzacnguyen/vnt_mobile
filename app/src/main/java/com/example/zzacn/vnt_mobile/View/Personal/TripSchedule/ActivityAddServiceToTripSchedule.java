package com.example.zzacn.vnt_mobile.View.Personal.TripSchedule;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.AddServiceScheduleAdapter;
import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelSearch;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ActivityAddServiceToTripSchedule extends AppCompatActivity {
    ArrayList<String> finalArr = new ArrayList<>();
    EditText etSearch;
    LinearLayout linearPlace, linearEat, linearHotel, linearEntertaiment, linearVehicle, linearAll;
    TextView txtSearchHistory;
    Button btnCancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancedsearch);
        etSearch = findViewById(R.id.etSearch);
        btnCancel = findViewById(R.id.btnCancel);
        linearPlace = findViewById(R.id.checkPlace);
        linearEat = findViewById(R.id.checkEat);
        linearHotel = findViewById(R.id.checkHotel);
        linearEntertaiment = findViewById(R.id.checkEntertainment);
        linearVehicle = findViewById(R.id.checkVehicle);
        linearAll = findViewById(R.id.checkAll);
        txtSearchHistory = findViewById(R.id.textViewSearchHistory);

        linearEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(1);
            }
        });
        linearHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(2);
            }
        });
        linearVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(3);
            }
        });
        linearPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(4);
            }
        });
        linearEntertaiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(5);
            }
        });
        linearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByType(0);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finishActivity(11);
                finish();
            }
        });

        txtSearchHistory.setVisibility(View.GONE);

    }

    void searchByType(int serviceType) {

        String url = Config.URL_HOST + Config.URL_SEARCH_TYPE.get(0) + serviceType
                + Config.URL_SEARCH_TYPE.get(1) + etSearch.getText().toString().replaceAll(" ", "\\+");

        if (!etSearch.getText().toString().equals("")) {
            search(url, serviceType);
        } else {
            Toast.makeText(ActivityAddServiceToTripSchedule.this, getResources().getString(R.string.text_PleaseEnterASearchKey), Toast.LENGTH_SHORT).show();
        }
    }

    private void search(String url, final int serviceType) {

        final AddServiceScheduleAdapter addServiceScheduleAdapter;
        final RecyclerView recyclerView;
        recyclerView = findViewById(R.id.RecyclerView_SearchList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<Service> services = new ModelSearch().getAdvancedSearchList(finalArr.get(0), serviceType);
        final ArrayList<Service> finalListService = services;
        if (services.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.text_NoResults), Toast.LENGTH_SHORT).show();
        }

        int id = getIntent().getIntExtra("id", 0);
        addServiceScheduleAdapter = new AddServiceScheduleAdapter(recyclerView, services, getApplicationContext(), id);
        recyclerView.setAdapter(addServiceScheduleAdapter);
        addServiceScheduleAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        addServiceScheduleAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            addServiceScheduleAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            addServiceScheduleAdapter.notifyItemRemoved(finalListService.size());

                            try {
                                finalArr = JsonHelper.parseJsonNoId(new JSONObject
                                        (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()), Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            ArrayList<Service> serviceArrayList = new ModelSearch().getAdvancedSearchList(finalArr.get(0), serviceType);
                            finalListService.addAll(serviceArrayList);

                            addServiceScheduleAdapter.notifyDataSetChanged();
                            addServiceScheduleAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }
}
