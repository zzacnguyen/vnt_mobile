package com.example.zzacn.vnt_mobile.View.Home;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Adapter.ServiceAdapter;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Search.ActivityAdvancedSearch;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    Button btnPlace, btnEat, btnHoTel, btnEntertain, btnVehicle;
    ImageView btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSearch = view.findViewById(R.id.button_Search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iSearch = new Intent(getContext(), ActivityAdvancedSearch.class);
                startActivity(iSearch);
            }
        });

        load(view);

        return view;
    }

    //Custom view service
    private void loadService(RecyclerView recyclerView, String url, ArrayList<String> formatJson) {
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Service> services = new ModelService().getServiceInMain(url, formatJson);

        ServiceAdapter serviceAdapter =
                new ServiceAdapter(services, getContext());
        recyclerView.setAdapter(serviceAdapter);
        serviceAdapter.notifyDataSetChanged();
    }

    void load(View view) {
        btnPlace = view.findViewById(R.id.btnAllPlace);
        btnEat = view.findViewById(R.id.btnAllEat);
        btnHoTel = view.findViewById(R.id.btnAllHotel);
        btnEntertain = view.findViewById(R.id.btnAllEntertain);
        btnVehicle = view.findViewById(R.id.btnAllVehicle);

        // region load service
        // load place
        RecyclerView recyclerViewDD = view.findViewById(R.id.RecyclerView_Place);
        loadService(recyclerViewDD, Config.URL_HOST + Config.URL_GET_ALL_PLACES, Config.GET_KEY_JSON_PLACE);

        // load eat
        RecyclerView recyclerViewAU = view.findViewById(R.id.RecyclerView_Eat);
        loadService(recyclerViewAU, Config.URL_HOST + Config.URL_GET_ALL_EATS, Config.GET_KEY_JSON_EAT);

        // load entertainment
        RecyclerView recyclerViewVC = view.findViewById(R.id.RecyclerView_Entertain);
        loadService(recyclerViewVC, Config.URL_HOST + Config.URL_GET_ALL_ENTERTAINMENTS, Config.GET_KEY_JSON_ENTERTAINMENT);

        // load hotel
        RecyclerView recyclerViewKS = view.findViewById(R.id.RecyclerView_Hotel);
        loadService(recyclerViewKS, Config.URL_HOST + Config.URL_GET_ALL_HOTELS, Config.GET_KEY_JSON_HOTEL);

        // load vehicle
        RecyclerView recyclerViewPT = view.findViewById(R.id.RecyclerView_Vehicle);
        loadService(recyclerViewPT, Config.URL_HOST + Config.URL_GET_ALL_VEHICLES, Config.GET_KEY_JSON_VEHICLE);
        // endregion
    }

}
