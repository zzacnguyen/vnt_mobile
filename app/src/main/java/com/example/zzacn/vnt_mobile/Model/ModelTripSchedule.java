package com.example.zzacn.vnt_mobile.Model;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;


public class ModelTripSchedule {

    public ArrayList<TripSchedule> getTripScheduleList(String url) {

        ArrayList<String> dataJson;
        ArrayList<TripSchedule> tripSchedules = new ArrayList<>();

        try {
            String result = new HttpRequestAdapter.httpGet().execute(url).get();
            dataJson = parseJsonNoId(new JSONObject(result), Config.GET_KEY_JSON_LOAD);
            JSONArray jsonArray = new JSONArray(dataJson.get(0));

            for (int i = 0; i < jsonArray.length(); i++) {

                TripSchedule tripSchedule = new TripSchedule();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataJson.clear();
                dataJson = parseJson(jsonObject, Config.GET_KEY_JSON_TRIP_SCHEDULE);

                tripSchedule.setTripID(Integer.parseInt(dataJson.get(0)));
                tripSchedule.setTripName(dataJson.get(1));
                tripSchedule.setTripStartDate(dataJson.get(2));
                tripSchedule.setTripEndDate(dataJson.get(3));

                tripSchedules.add(tripSchedule);
            }

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return tripSchedules;
    }

}
