package com.example.zzacn.vnt_mobile.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JsonHelper {

    public static ArrayList<String> parseJson(JSONArray json, ArrayList<String> arr) {
        ArrayList<String> stringJson = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++) {
                stringJson.add(json.getJSONObject(i).getInt("id") + "");
                for (int j = 0; j < arr.size(); j++) {
                    stringJson.add(json.getJSONObject(i).getString(arr.get(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringJson;
    }

    public static ArrayList<String> parseJson(JSONObject json, ArrayList<String> arr) {
        ArrayList<String> stringJson = new ArrayList<>();
        try {
            stringJson.add(json.getInt("id") + "");
            for (int j = 0; j < arr.size(); j++) {
                stringJson.add(json.getString(arr.get(j)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringJson;
    }

    public static ArrayList<String> parseJsonNoId(JSONArray json, ArrayList<String> arr) {
        ArrayList<String> stringJson = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++) {
                for (int j = 0; j < arr.size(); j++) {
                    stringJson.add(json.getJSONObject(i).getString(arr.get(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringJson;
    }

    public static ArrayList<String> parseJsonNoId(JSONObject json, ArrayList<String> arr) {
        ArrayList<String> stringJson = new ArrayList<>();
        try {
            for (int j = 0; j < arr.size(); j++) {
                stringJson.add(json.getString(arr.get(j)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringJson;
    }

    public static JSONArray mergeJson(JSONArray json1, JSONArray json2) {
        for (int i = 0; i < json2.length(); i++) {
            try {
                json1.put(json2.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json1;
    }

    public static JSONArray mergeJson(JSONArray json1, JSONObject json2) {
        return json1.put(json2);
    }

    public static JSONArray mergeJson(JSONObject json1, JSONObject json2) {
        JSONArray jsonArray = null;
        jsonArray.put(json1);
        jsonArray.put(json2);
        return jsonArray;
    }

    public static JSONArray mergeJson(JSONObject json1, JSONArray json2) {
        try {
            return json2.put(0, json1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json2;
    }

    public static void writeJson(File file, JSONObject json) {
        try {
            JSONArray jsonFile = new JSONArray();
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                JSONArray jsonRead = null;
                try {
                    jsonRead = new JSONArray(new String(buffer));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonFile = mergeJson(jsonRead, json);
            } else {
                jsonFile.put(json);
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonFile.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJson(File file, JSONArray json) {
        try {
            JSONArray jsonFile;
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                JSONArray jsonRead = null;
                try {
                    jsonRead = new JSONArray(new String(buffer));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonFile = mergeJson(jsonRead, json);
            } else {
                jsonFile = json;
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonFile.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readJson(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}