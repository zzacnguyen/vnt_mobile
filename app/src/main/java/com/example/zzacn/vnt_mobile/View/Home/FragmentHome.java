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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zzacn.vnt_mobile.Adapter.EnterpriseServiceAdapter;
import com.example.zzacn.vnt_mobile.Adapter.NearbyAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ScheduleAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.ModelTripSchedule;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.MainActivity;
import com.example.zzacn.vnt_mobile.View.Personal.ActivityAddPlace;
import com.example.zzacn.vnt_mobile.View.Personal.TripSchedule.ActivityAddTripSchedule;
import com.example.zzacn.vnt_mobile.View.Search.ActivityAdvancedSearch;

import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.View.MainActivity.lat;
import static com.example.zzacn.vnt_mobile.View.MainActivity.lon;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;


public class FragmentHome extends Fragment {

    ImageView btnSearch;
    RecyclerView recyclerView;
    View view, viewLineYourService, viewLineYourSchedule;
    LinearLayout linearEnterpriseService, linearSchedule;
    FloatingActionButton fabAdd, fabAddService, fabAddTrip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSearch = view.findViewById(R.id.button_Search);
        fabAdd = view.findViewById(R.id.fabAdd);
        fabAddService = view.findViewById(R.id.fab_AddService);
        fabAddTrip = view.findViewById(R.id.fab_AddTrip);

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
                // nếu người dùng là doanh nghiệp thì mở activity thêm địa điểm
                if (userType.size() == 1 && userType.contains("2")) {
                    startActivity(new Intent(getContext(), ActivityAddPlace.class));
                }
                // nếu người dùng là hdv thì mở activity thêm lịch trình
                else if (userType.size() == 1 && userType.contains("3")) {
                    startActivity(new Intent(getContext(), ActivityAddTripSchedule.class));
                } else {
                    // cả 2 người dùng thì mở thêm 2 fab
                    if (fabAddService.isShown()) {
                        HideFab();
                    } else {
                        ShowFab();
                    }
                }
            }
        });

        fabAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getContext(), ActivityAddPlace.class));
            }
        });

        fabAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getContext(), ActivityAddTripSchedule.class));
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
        } else if (url.equals(Config.URL_GET_TRIP_SCHEDULE + userId)) {
            ScheduleAdapter scheduleAdapter = new ScheduleAdapter(
                    new ModelTripSchedule().getScheduleInMain(Config.URL_HOST + url), getContext());
            recyclerView.setAdapter(scheduleAdapter);
            scheduleAdapter.notifyDataSetChanged();
        } else if (url.equals(Config.URL_GET_NEARBY.get(0) + lat + Config.URL_GET_NEARBY.get(1) + lon
                + Config.URL_GET_NEARBY.get(2) + "5000")) {
            NearbyAdapter nearbyAdapter = new NearbyAdapter(
                    new ModelService().getServiceNearby(Config.URL_HOST + url, arrayKey, 0), getContext());
            recyclerView.setAdapter(nearbyAdapter);
            nearbyAdapter.notifyDataSetChanged();
        } else {
            ServiceAdapter serviceAdapter = new ServiceAdapter(
                    new ModelService().getServiceInMain(Config.URL_HOST + url, arrayKey), getContext());
            recyclerView.setAdapter(serviceAdapter);
            serviceAdapter.notifyDataSetChanged();
        }
    }

    void load(View view) {
        viewLineYourService = view.findViewById(R.id.viewLineYourService);
        viewLineYourSchedule = view.findViewById(R.id.viewLineYourSchedule);
        linearEnterpriseService = view.findViewById(R.id.linearEnterpriseService);
        linearSchedule = view.findViewById(R.id.linearSchedule);

        // region load service
        if (userType != null && userType.contains("2")) {
            // region load enterprise's service nếu người dùng là doanh nghiệp + hiện nút thêm địa điểm
            linearEnterpriseService.setVisibility(View.VISIBLE);
            viewLineYourService.setVisibility(View.VISIBLE);
            recyclerView = view.findViewById(R.id.RecyclerView_EnterpriseService);
            loadService(Config.URL_GET_ALL_ENTERPRISE_SERVICE + userId, Config.GET_KEY_JSON_ENTERPRISE_SERVICE);
            fabAdd.setVisibility(View.VISIBLE);
            // endregion
        }

        if (userType != null && userType.contains("3")) {
            // region load lịch trình nếu là người dùng hdv + hiện nút thêm lịch trình
            linearSchedule.setVisibility(View.VISIBLE);
            viewLineYourSchedule.setVisibility(View.VISIBLE);
            recyclerView = view.findViewById(R.id.RecyclerView_Schedule);
            loadService(Config.URL_GET_TRIP_SCHEDULE + userId, new ArrayList<String>());
            fabAdd.setVisibility(View.VISIBLE);
            // endregion
        }

        // load lịch trình và địa điểm dịch vụ nếu là người dùng hdv và doanh nghiệp
        // hiện thêm 2 nút thêm lịch trình và thêm địa điểm
        if (userType != null && userType.contains("2") && userType.contains("3")) {

            // region load lịch trình và dịch vụ của doanh nghiệp

            // load lịch trình
            linearEnterpriseService.setVisibility(View.VISIBLE);
            viewLineYourService.setVisibility(View.VISIBLE);
            recyclerView = view.findViewById(R.id.RecyclerView_EnterpriseService);
            loadService(Config.URL_GET_ALL_ENTERPRISE_SERVICE + userId, Config.GET_KEY_JSON_ENTERPRISE_SERVICE);

            // load địa điểm của doanh nghiệp
            linearSchedule.setVisibility(View.VISIBLE);
            viewLineYourSchedule.setVisibility(View.VISIBLE);
            recyclerView = view.findViewById(R.id.RecyclerView_Schedule);
            loadService(Config.URL_GET_TRIP_SCHEDULE + userId, new ArrayList<String>());

            // hiện fab
            fabAdd.setVisibility(View.VISIBLE);
            fabAddService.setVisibility(View.VISIBLE);
            fabAddTrip.setVisibility(View.VISIBLE);
            HideFab();
            // endregion

        }

        // load nearby
        recyclerView = view.findViewById(R.id.RecyclerView_Nearby);
        loadService(Config.URL_GET_NEARBY.get(0) + lat + Config.URL_GET_NEARBY.get(1) + lon
                + Config.URL_GET_NEARBY.get(2) + "5000", Config.GET_KEY_JSON_NEARBY);

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

    private void HideFab() {
        fabAddService.hide();
        fabAddTrip.hide();
    }

    private void ShowFab() {
        fabAddService.show();
        fabAddTrip.show();
    }
}
