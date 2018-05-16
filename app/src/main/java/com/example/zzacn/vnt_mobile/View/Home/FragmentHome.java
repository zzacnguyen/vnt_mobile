package com.example.zzacn.vnt_mobile.View.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zzacn.vnt_mobile.Adapter.ServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Search.ActivityAdvancedSearch;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentHome extends Fragment {

    Button btnPlace, btnEat, btnHoTel, btnEntertain, btnVehicle;
    ImageView btnSearch;
    SessionManager sessionManager;
    ArrayList<Service> servicesPlace, servicesEat, servicesHotel, servicesEntertainment, servicesVehicle;
    RecyclerView recyclerView;
    Bundle save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        save = savedInstanceState;
        btnSearch = view.findViewById(R.id.button_Search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iSearch = new Intent(getContext(), ActivityAdvancedSearch.class);
                startActivity(iSearch);
            }
        });

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        load(view);

        return view;
    }

    //Custom view service
    private void loadService(String url) {
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ServiceAdapter serviceAdapter = null;

        switch (url) {
            case Config.URL_GET_ALL_PLACES:
                servicesPlace = setService("place", url, Config.GET_KEY_JSON_PLACE);
                serviceAdapter = new ServiceAdapter(servicesPlace, getContext());
                break;
            case Config.URL_GET_ALL_HOTELS:
                servicesHotel = setService("hotel", url, Config.GET_KEY_JSON_HOTEL);
                serviceAdapter = new ServiceAdapter(servicesHotel, getContext());
                break;
            case Config.URL_GET_ALL_EATS:
                servicesEat = setService("eat", url, Config.GET_KEY_JSON_EAT);
                serviceAdapter = new ServiceAdapter(servicesEat, getContext());
                break;
            case Config.URL_GET_ALL_ENTERTAINMENTS:
                servicesEntertainment = setService("entertainment", url, Config.GET_KEY_JSON_ENTERTAINMENT);
                serviceAdapter = new ServiceAdapter(servicesEntertainment, getContext());
                break;
            case Config.URL_GET_ALL_VEHICLES:
                servicesVehicle = setService("vehicle", url, Config.GET_KEY_JSON_VEHICLE);
                serviceAdapter = new ServiceAdapter(servicesVehicle, getContext());
                break;
        }

        recyclerView.setAdapter(serviceAdapter);
        serviceAdapter.notifyDataSetChanged();
    }

    ArrayList<Service> setService(String key, String url,
                              ArrayList<String> formatJson) {
        ArrayList<Service> services;
        if (save == null || !save.containsKey(key)) {
            services = new ModelService().getServiceInMain(Config.URL_HOST + url, formatJson);
        } else {
            services = save.getParcelableArrayList(key);
        }
        return services;
    }

    void load(View view) {
        btnPlace = view.findViewById(R.id.btnAllPlace);
        btnEat = view.findViewById(R.id.btnAllEat);
        btnHoTel = view.findViewById(R.id.btnAllHotel);
        btnEntertain = view.findViewById(R.id.btnAllEntertain);
        btnVehicle = view.findViewById(R.id.btnAllVehicle);

        // region load service
        // load place
        recyclerView = view.findViewById(R.id.RecyclerView_Place);
        loadService(Config.URL_GET_ALL_PLACES);

        // load eat
        recyclerView = view.findViewById(R.id.RecyclerView_Eat);
        loadService(Config.URL_GET_ALL_EATS);

        // load entertainment
        recyclerView = view.findViewById(R.id.RecyclerView_Entertain);
        loadService(Config.URL_GET_ALL_ENTERTAINMENTS);

        // load hotel
        recyclerView = view.findViewById(R.id.RecyclerView_Hotel);
        loadService(Config.URL_GET_ALL_HOTELS);

        // load vehicle
        recyclerView = view.findViewById(R.id.RecyclerView_Vehicle);
        loadService(Config.URL_GET_ALL_VEHICLES);
        // endregion

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("place", servicesPlace);
        outState.putParcelableArrayList("hotel", servicesHotel);
        outState.putParcelableArrayList("eat", servicesEat);
        outState.putParcelableArrayList("entertainment", servicesEntertainment);
        outState.putParcelableArrayList("vehicle", servicesVehicle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        btnSearch.setOnClickListener(null);
        recyclerView.setAdapter(null);
        super.onDestroyView();
    }
}
