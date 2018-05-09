package com.example.zzacn.vnt_mobile.View.Home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ListServiceFragment extends Fragment {

    ArrayList<String> finalArr = new ArrayList<>();
    ArrayList<String> formatJson = new ArrayList<>();
    Toolbar toolbar;
    TextView toolbarTitle;
    ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        btnBack = view.findViewById(R.id.button_Back);

        Bundle bundle = getArguments();

        String url = bundle.getString("url");

        if (url != null) {
            switch (url) { //Kiểm tra từng đường dẫn url
                case Config.URL_HOST + Config.URL_GET_ALL_EATS:
                    formatJson = Config.GET_KEY_JSON_EAT;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbEat));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfRestaurant));
                    break;
                case Config.URL_HOST + Config.URL_GET_ALL_PLACES:
                    formatJson = Config.GET_KEY_JSON_PLACE;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbPlace));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfPlaceToVisit));
                    break;
                case Config.URL_HOST + Config.URL_GET_ALL_HOTELS:
                    formatJson = Config.GET_KEY_JSON_HOTEL;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbHotel));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfHotel));
                    break;
                case Config.URL_HOST + Config.URL_GET_ALL_VEHICLES:
                    formatJson = Config.GET_KEY_JSON_VEHICLE;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfTransport));
                    break;
                default:
                    formatJson = Config.GET_KEY_JSON_ENTERTAINMENT;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbEntertain));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfEntertainment));
                    break;
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getFullServiceList(url, formatJson, view);

        return view;
    }

    private void getFullServiceList(String url, final ArrayList<String> formatJson, View view) { //Khai báo view

        final ListOfServiceAdapter listOfServiceAdapter;
        final RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.RecyclerView_ServiceList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Service> services = new ModelService().getFullServiceList(url, formatJson);

        listOfServiceAdapter = new ListOfServiceAdapter(recyclerView, services, getContext());
        recyclerView.setAdapter(listOfServiceAdapter);
        listOfServiceAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        final ArrayList<Service> finalListService = services;
        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

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

                            ArrayList<Service> serviceArrayList = new ModelService().
                                    getFullServiceList(finalArr.get(1), formatJson);
                            for (int i = 0; i < serviceArrayList.size(); i++) {
                                finalListService.add(serviceArrayList.get(i));
                            }
                            try {
                                finalArr = JsonHelper.parseJsonNoId(new JSONObject
                                        (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()),
                                        Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            listOfServiceAdapter.notifyDataSetChanged();
                            listOfServiceAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }
}
