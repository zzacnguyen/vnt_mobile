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
import com.example.zzacn.vnt_mobile.Adapter.ListNearbyAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfEnterpriseServiceAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfServiceAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfTripScheduleAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.ModelTripSchedule;
import com.example.zzacn.vnt_mobile.Model.Object.NearLocation;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;


public class FragmentListService extends Fragment {

    ArrayList<String> finalArr = new ArrayList<>();
    Toolbar toolbar;
    TextView toolbarTitle;
    ImageView btnBack;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        this.view = view;
        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        btnBack = view.findViewById(R.id.button_Back);

        Bundle bundle = getArguments();
        String url = bundle != null ? bundle.getString("url") : null;
        int type = bundle != null ? bundle.getInt("type") : -1;
        if (type != -1) {
            switch (type) { //Kiểm tra từng đường dẫn url
                case 1:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbEat));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfRestaurant));
                    getFullServiceList(url, Config.GET_KEY_JSON_EAT);
                    break;
                case 4:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbPlace));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfPlaceToVisit));
                    getFullServiceList(url, Config.GET_KEY_JSON_PLACE);
                    break;
                case 2:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbHotel));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfHotel));
                    getFullServiceList(url, Config.GET_KEY_JSON_HOTEL);
                    break;
                case 3:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfTransport));
                    getFullServiceList(url, Config.GET_KEY_JSON_VEHICLE);
                    break;
                case 0:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbPlace));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfEnterpriseService));
                    getFullListServiceEnterprise(url);
                    break;
                case 6:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.bgToolbar));
                    toolbarTitle.setText(getResources().getString(R.string.text_TripSchedule));
                    getFullListSchedule(url);
                    break;
                case 5:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.tbEntertain));
                    toolbarTitle.setText(getResources().getString(R.string.title_ListOfEntertainment));
                    getFullServiceList(url, Config.GET_KEY_JSON_ENTERTAINMENT);
                    break;
                case 7:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.bgToolbar));
                    toolbarTitle.setText(getResources().getString(R.string.text_Nearby));
                    getFullListNearby(url);
                    break;
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void getFullServiceList(String url, final ArrayList<String> formatJson) { //Khai báo view

        final ListOfServiceAdapter listOfServiceAdapter;
        final RecyclerView recyclerView;

        recyclerView = view.findViewById(R.id.RecyclerView_ServiceList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Service> services = getData(url, formatJson);
        final ArrayList<Service> finalListService = services;

        listOfServiceAdapter = new ListOfServiceAdapter(recyclerView, services, getContext());
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


                            ArrayList<Service> serviceArrayList = getData(finalArr.get(1), formatJson);
                            finalListService.addAll(serviceArrayList);

                            listOfServiceAdapter.notifyDataSetChanged();
                            listOfServiceAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void getFullListServiceEnterprise(String url) { //Khai báo view

        final ListOfEnterpriseServiceAdapter listOfEnterpriseServiceAdapter;
        final RecyclerView recyclerView;

        recyclerView = view.findViewById(R.id.RecyclerView_ServiceList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Service> services = getData(url, Config.GET_KEY_JSON_ENTERPRISE_SERVICE);
        final ArrayList<Service> finalListService = services;

        listOfEnterpriseServiceAdapter = new ListOfEnterpriseServiceAdapter(recyclerView, services, getContext());
        recyclerView.setAdapter(listOfEnterpriseServiceAdapter);
        listOfEnterpriseServiceAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        listOfEnterpriseServiceAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            listOfEnterpriseServiceAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            listOfEnterpriseServiceAdapter.notifyItemRemoved(finalListService.size());

                            ArrayList<Service> serviceArrayList = getData(finalArr.get(1), Config.GET_KEY_JSON_ENTERPRISE_SERVICE);
                            finalListService.addAll(serviceArrayList);

                            listOfEnterpriseServiceAdapter.notifyDataSetChanged();
                            listOfEnterpriseServiceAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void getFullListSchedule(String url) { //Khai báo view

        final ListOfTripScheduleAdapter listOfTripScheduleAdapter;
        final RecyclerView recyclerView;

        recyclerView = view.findViewById(R.id.RecyclerView_ServiceList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            finalArr = parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<TripSchedule> tripScheduleList = new ModelTripSchedule().getTripScheduleList(finalArr.get(0));
        final ArrayList<TripSchedule> finalListService = tripScheduleList;

        listOfTripScheduleAdapter = new ListOfTripScheduleAdapter(recyclerView, getContext(), tripScheduleList);
        recyclerView.setAdapter(listOfTripScheduleAdapter);
        listOfTripScheduleAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        listOfTripScheduleAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            listOfTripScheduleAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            listOfTripScheduleAdapter.notifyItemRemoved(finalListService.size());

                            try {
                                finalArr = parseJsonNoId(new JSONObject(new HttpRequestAdapter
                                        .httpGet().execute(finalArr.get(1)).get()), Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            ArrayList<TripSchedule> tripscheduleArrayList =
                                    new ModelTripSchedule().getTripScheduleList(finalArr.get(0));
                            finalListService.addAll(tripscheduleArrayList);

                            listOfTripScheduleAdapter.notifyDataSetChanged();
                            listOfTripScheduleAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void getFullListNearby(String url) {
        RecyclerView recyclerView = view.findViewById(R.id.RecyclerView_ServiceList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        ArrayList<NearLocation> nearLocations = new ModelService().getServiceNearby(url, Config.GET_KEY_JSON_NEARBY, 1);

        ListNearbyAdapter listNearbyAdapter = new ListNearbyAdapter(nearLocations, getContext());
        recyclerView.setAdapter(listNearbyAdapter);
        listNearbyAdapter.notifyDataSetChanged();
    }

    private ArrayList<Service> getData(String url, ArrayList<String> formatJson) {

        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return new ModelService().getFullServiceList(finalArr.get(0), formatJson);
    }
}
