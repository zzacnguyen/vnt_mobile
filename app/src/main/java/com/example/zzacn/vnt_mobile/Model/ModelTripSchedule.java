package com.example.zzacn.vnt_mobile.Model;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;


public class ModelTripSchedule {

    public ArrayList<TripSchedule> getTripScheduleList(String getJson) {

        ArrayList<String> dataJson;
        ArrayList<TripSchedule> tripSchedules = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                TripSchedule tripSchedule = new TripSchedule();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataJson = parseJson(jsonObject, Config.GET_KEY_JSON_TRIP_SCHEDULE);

                tripSchedule.setTripID(Integer.parseInt(dataJson.get(0)));
                tripSchedule.setTripName(dataJson.get(1));
                tripSchedule.setTripStartDate(dataJson.get(2));
                tripSchedule.setTripEndDate(dataJson.get(3));

                tripSchedules.add(tripSchedule);
                dataJson.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tripSchedules;
    }

    public ArrayList<Service> getServiceInSchedule(String getJson) {
        return new ModelFavorite().getFavoriteList(getJson);
    }

    public ArrayList<TripSchedule> getScheduleInMain(String url) {
        ArrayList<String> arrayList;
        ArrayList<TripSchedule> tripSchedules = new ArrayList<>();

        try {
            String rs = new HttpRequestAdapter.httpGet().execute(url).get();
            arrayList = parseJsonNoId(new JSONObject(rs), Config.GET_KEY_JSON_LOAD);
            JSONArray jsonArray = new JSONArray(arrayList.get(0));

            int limit = jsonArray.length() > 5 ? 5 : jsonArray.length();

            for (int i = 0; i < limit; i++) {

                TripSchedule tripSchedule = new TripSchedule();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList.clear();
                arrayList = parseJson(jsonObject, Config.GET_KEY_JSON_TRIP_SCHEDULE);
                tripSchedule.setTripID(Integer.parseInt(arrayList.get(0)));
                tripSchedule.setTripName(arrayList.get(1));
                tripSchedule.setTripStartDate(arrayList.get(2));
                tripSchedule.setTripEndDate(arrayList.get(3));

                tripSchedules.add(tripSchedule);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return tripSchedules;
    }
}
