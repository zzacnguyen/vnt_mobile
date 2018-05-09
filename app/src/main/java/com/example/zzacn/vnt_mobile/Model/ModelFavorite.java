package com.example.zzacn.vnt_mobile.Model;


import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.mergeJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.readJson;
import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;


public class ModelFavorite {
    public ArrayList<Service> getFavoriteList(File file, String url) {

        ArrayList<String> arrayList;
        ArrayList<Service> services = new ArrayList<>();

        try {
            arrayList = parseJsonNoId(new JSONObject(new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
            JSONArray jsonArray;

            if (file.exists()) {
                jsonArray = mergeJson(new JSONArray(arrayList.get(0)), new JSONArray(readJson(file)));
            } else {
                jsonArray = new JSONArray(arrayList.get(0));
            }

            for (int i = 0; i < jsonArray.length(); i++) {

                Service service = new Service();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList.clear();
                arrayList = parseJson(jsonObject, Config.GET_KEY_JSON_SERVICE_LIST);

                //Set hình ảnh
                service.setImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(7),
                        arrayList.get(6), arrayList.get(7)));
                //Set mã dịch vụ
                service.setId(Integer.parseInt(arrayList.get(0)));
                //Set tên dịch vụ yêu thích
                service.setName(!arrayList.get(1).equals(Config.NULL) ? arrayList.get(1) :
                        !arrayList.get(2).equals(Config.NULL) ? arrayList.get(2) :
                                !arrayList.get(3).equals(Config.NULL) ? arrayList.get(3) :
                                        !arrayList.get(4).equals(Config.NULL) ? arrayList.get(4) : arrayList.get(5));

                services.add(service);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return services;
    }
}
