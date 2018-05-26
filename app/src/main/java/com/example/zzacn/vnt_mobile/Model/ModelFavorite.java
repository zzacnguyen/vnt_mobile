package com.example.zzacn.vnt_mobile.Model;


import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;


public class ModelFavorite {
    public ArrayList<Service> getFavoriteList(String getJson) {

        ArrayList<String> arrayList;
        ArrayList<Service> services = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                Service service = new Service();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList = parseJson(jsonObject, Config.GET_KEY_JSON_SERVICE_LIST);

                //Set hình ảnh
                service.setImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(7),
                        Config.FOLDER_THUMB1, arrayList.get(7)));
                //Set mã dịch vụ
                service.setId(Integer.parseInt(arrayList.get(0)));
                //Set tên dịch vụ yêu thích
                service.setName(!arrayList.get(1).equals(Config.NULL) ? arrayList.get(1) :
                        !arrayList.get(2).equals(Config.NULL) ? arrayList.get(2) :
                                !arrayList.get(3).equals(Config.NULL) ? arrayList.get(3) :
                                        !arrayList.get(4).equals(Config.NULL) ? arrayList.get(4) : arrayList.get(5));

                services.add(service);
                arrayList.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return services;
    }
}
