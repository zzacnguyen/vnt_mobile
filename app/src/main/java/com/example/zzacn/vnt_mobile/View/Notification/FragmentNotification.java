package com.example.zzacn.vnt_mobile.View.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zzacn.vnt_mobile.Adapter.EventAdapter;
import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelEvent;
import com.example.zzacn.vnt_mobile.Model.Object.Event;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.Event.ActivityAddEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;
import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentNotification extends Fragment {

    ArrayList<String> finalArr = new ArrayList<>();
    FloatingActionButton fabAddEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        getNotify(Config.URL_HOST + Config.URL_GET_ALL_EVENTS + "/" + userId, view);

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ActivityAddEvent.class));
            }
        });

        return view;
    }

    private void getNotify(String url, View view) {

        fabAddEvent = view.findViewById(R.id.fabAddEvent);

        if (userType != null && (userType.equals("2")||userType.equals("4"))) {
            fabAddEvent.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.RecyclerView_EventList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject
                    (new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<Event> events = new ModelEvent().getEventList(getContext(), finalArr.get(0));
        final ArrayList<Event> finalListService = events;

        final EventAdapter eventAdapter = new EventAdapter(recyclerView, events, getContext());
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        eventAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            eventAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            eventAdapter.notifyItemRemoved(finalListService.size());

                            try {
                                finalArr = JsonHelper.parseJsonNoId(new JSONObject
                                                (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()),
                                        Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            ArrayList<Event> serviceArrayList = new ModelEvent().
                                    getEventList(getApplicationContext(), finalArr.get(0));
                            finalListService.addAll(serviceArrayList);

                            eventAdapter.notifyDataSetChanged();
                            eventAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }
}
