package com.example.zzacn.vnt_mobile.Model;

import android.content.Context;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Event;
import com.example.zzacn.vnt_mobile.Model.Object.Notification;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;

public class ModelEvent {

    public ArrayList<Event> getEventList(Context context, String getJson) { //Get danh sách thông báo sự kiện

        ArrayList<String> arrayList;
        JSONArray jsonArray;
        ArrayList<Event> events = new ArrayList<>();
        try {
            jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                Event event = new Event();
                arrayList = parseJson(jsonArray.getJSONObject(i), Config.GET_KEY_JSON_EVENT); //Parse json event

                event.setServiceId(Integer.parseInt(arrayList.get(0))); //Set mã dịch vụ
                event.setEventId(Integer.parseInt(arrayList.get(7)));
                event.setEventName(arrayList.get(1)); //Set tên

                //Set ngày bắt đầu và ngày kết thúc sự kiện
                event.setEventDate(context.getResources().getString(R.string.text_From) + " " + arrayList.get(2) +
                        " " + context.getResources().getString(R.string.text_To) + " " + arrayList.get(3));
                event.setEventImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(5),
                        Config.FOLDER_THUMB1, arrayList.get(5)));

                // kiểm tra đã xem
                event.setSeen(arrayList.get(6).equals("1"));

                events.add(event);
                arrayList.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public ArrayList<Notification> getNotificationList(String getJson) { //Get danh sách thông báo sự kiện

        ArrayList<String> arrayList;
        ArrayList<Notification> notifications = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                Notification notification = new Notification();
                arrayList = parseJson(jsonArray.getJSONObject(i), Config.GET_KEY_JSON_NOTIFICATION); //Parse json

                notification.setServiceId(Integer.parseInt(arrayList.get(0))); //Set mã dịch vụ
                notification.setEventId(Integer.parseInt(arrayList.get(5)));
                notification.setNotificationName(arrayList.get(1)); //Set tên
                notification.setNotificationImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(3),
                        Config.FOLDER_THUMB1, arrayList.get(3))); // set hình
                // kiểm tra đã xem
                notification.setSeen(arrayList.get(4).equals("1"));
                notification.setEventUser(Integer.parseInt(arrayList.get(6)));

                notifications.add(notification);
                arrayList.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notifications;
    }
}
