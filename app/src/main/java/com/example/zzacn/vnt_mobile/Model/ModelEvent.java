package com.example.zzacn.vnt_mobile.Model;

import android.content.Context;
import android.os.Environment;


import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Event;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.readJson;
import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;


/**
 * Created by sieut on 4/26/2018.
 */

public class ModelEvent {

    public static JSONArray getJsonFileEvent() {
        File path = new File(Environment.getExternalStorageDirectory() + Config.FOLDER);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, Config.FILE_EVENT);
        JSONArray jsonFile = new JSONArray();
        if (file.exists()) {
            try {
                jsonFile = new JSONArray(readJson(file));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonFile;
    }

    public ArrayList<Event> getEventList(Context context, String url) { //Get danh sách thông báo sự kiện

        ArrayList<String> arrayList;
        JSONArray jsonArray;
        ArrayList<Event> events = new ArrayList<>();
        try {
            //Sử dụng parseJsonNoId vì KEY_JSON_LOAD ko có id
            arrayList = parseJsonNoId(new JSONObject(new HttpRequestAdapter.httpGet().execute(url).get()), Config.GET_KEY_JSON_LOAD);
            jsonArray = new JSONArray(arrayList.get(0));

            for (int i = 0; i < jsonArray.length(); i++) {

                Event event = new Event();
                arrayList.clear();
                arrayList = parseJson(jsonArray.getJSONObject(i), Config.GET_KEY_JSON_EVENT); //Parse json event

                event.setEventId(Integer.parseInt(arrayList.get(0))); //Set mã sự kiện
                event.setEventName(arrayList.get(1)); //Set tên

                //Set ngày bắt đầu và ngày kết thúc sự kiện
                event.setEventDate(context.getResources().getString(R.string.text_From) + " " + arrayList.get(2) +
                        " " + context.getResources().getString(R.string.text_To) + " " + arrayList.get(3));
                event.setEventImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(5),
                        arrayList.get(4), arrayList.get(5)));

                // kiểm tra đã xem

                event.setSeen(getJsonFileEvent().toString().contains("\"id\":\"" + event.getEventId() + "\""));
                events.add(event);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return events;
    }
}
