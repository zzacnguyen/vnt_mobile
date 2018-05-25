package com.example.zzacn.vnt_mobile.View.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zzacn.vnt_mobile.Adapter.EnterpriseServiceAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.ActivityAddPlace;
import com.example.zzacn.vnt_mobile.View.Personal.TripSchedule.ActivityAddTripSchedule;
import com.example.zzacn.vnt_mobile.View.Search.ActivityAdvancedSearch;

import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;


public class FragmentHome extends Fragment {

    Button btnPlace, btnEat, btnHoTel, btnEntertain, btnVehicle;
    ImageView btnSearch;
    RecyclerView recyclerView;
    View view, viewLine;
    LinearLayout linearEnterpriseService;
    FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSearch = view.findViewById(R.id.button_Search);
        fabAdd = view.findViewById(R.id.fabAdd);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iSearch = new Intent(getContext(), ActivityAdvancedSearch.class);
                startActivity(iSearch);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // nếu người dùng là doanh nghiệp hoặc ctv thì mở activity thêm địa điểm
                if (userType.equals("2") || userType.equals("4")) {
                    startActivity(new Intent(getContext(), ActivityAddPlace.class));
                }
                // nếu người dùng là hdv thì mở activity thêm lịch trình
                else {
                    startActivity(new Intent(getContext(), ActivityAddTripSchedule.class));
                }
            }
        });

        if (getUserVisibleHint()) {
            load(view);
        }

        return view;
    }

    //Custom view service
    private void loadService(String url, ArrayList<String> arrayKey) {
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (url.equals(Config.URL_GET_ALL_ENTERPRISE_SERVICE + userId)) {
            EnterpriseServiceAdapter enterpriseServiceAdapter = new EnterpriseServiceAdapter(
                    new ModelService().getServiceInMain(Config.URL_HOST + url, arrayKey), getContext());
            recyclerView.setAdapter(enterpriseServiceAdapter);
            enterpriseServiceAdapter.notifyDataSetChanged();
        } else {
            ServiceAdapter serviceAdapter = new ServiceAdapter(
                    new ModelService().getServiceInMain(Config.URL_HOST + url, arrayKey), getContext());
            recyclerView.setAdapter(serviceAdapter);
            serviceAdapter.notifyDataSetChanged();
        }

    }

    void load(View view) {
        btnPlace = view.findViewById(R.id.btnAllPlace);
        btnEat = view.findViewById(R.id.btnAllEat);
        btnHoTel = view.findViewById(R.id.btnAllHotel);
        btnEntertain = view.findViewById(R.id.btnAllEntertain);
        btnVehicle = view.findViewById(R.id.btnAllVehicle);
        viewLine = view.findViewById(R.id.viewLine);
        linearEnterpriseService = view.findViewById(R.id.linearEnterpriseService);

        // region load service
        // load enterprise's service nếu người dùng là doanh nghiệp
        if (userType != null && userType.equals("2")) {
            linearEnterpriseService.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
            recyclerView = view.findViewById(R.id.RecyclerView_EnterpriseService);
            loadService(Config.URL_GET_ALL_ENTERPRISE_SERVICE + userId, Config.GET_KEY_JSON_ENTERPRISE_SERVICE);
            fabAdd.setVisibility(View.VISIBLE);
        }
        // hiện nút thêm nếu là người dùng ctv hoăc hdv
        if (userType != null && (userType.equals("4") || userType.equals("3"))) {
            fabAdd.setVisibility(View.VISIBLE);
        }

        // load place
        recyclerView = view.findViewById(R.id.RecyclerView_Place);
        loadService(Config.URL_GET_ALL_PLACES, Config.GET_KEY_JSON_PLACE);

        // load eat
        recyclerView = view.findViewById(R.id.RecyclerView_Eat);
        loadService(Config.URL_GET_ALL_EATS, Config.GET_KEY_JSON_EAT);

        // load entertainment
        recyclerView = view.findViewById(R.id.RecyclerView_Entertain);
        loadService(Config.URL_GET_ALL_ENTERTAINMENTS, Config.GET_KEY_JSON_ENTERTAINMENT);

        // load hotel
        recyclerView = view.findViewById(R.id.RecyclerView_Hotel);
        loadService(Config.URL_GET_ALL_HOTELS, Config.GET_KEY_JSON_HOTEL);

        // load vehicle
        recyclerView = view.findViewById(R.id.RecyclerView_Vehicle);
        loadService(Config.URL_GET_ALL_VEHICLES, Config.GET_KEY_JSON_VEHICLE);
        // endregion
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }
}
