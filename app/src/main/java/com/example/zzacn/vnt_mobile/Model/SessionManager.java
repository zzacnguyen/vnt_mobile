package com.example.zzacn.vnt_mobile.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;
import static com.example.zzacn.vnt_mobile.View.MainActivity.password;
import static com.example.zzacn.vnt_mobile.View.MainActivity.username;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.fullName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;


public class SessionManager {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String PREF_NAME = "Session";
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String username, String password) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing username in pref
        editor.putString(KEY_USERNAME, username);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (this.isLoggedIn()) {
            // get user data from session
            HashMap<String, String> user = getUserDetails();

            username = user.get(KEY_USERNAME);
            password = user.get(KEY_PASSWORD);
            try {
                JSONObject jsonPost = new JSONObject("{"
                        + Config.POST_KEY_JSON_LOGIN_REGISTER.get(0) + ":\"" + username + "\","
                        + Config.POST_KEY_JSON_LOGIN_REGISTER.get(1) + ":\"" + password + "\"}");
                String rs = new HttpRequestAdapter.httpPost(jsonPost).execute(Config.URL_HOST + Config.URL_LOGIN).get();
                JSONObject jsonGet = new JSONObject(rs);
                ArrayList<String> arrayUser =
                        JsonHelper.parseJson(new JSONObject(jsonGet.getString(Config.GET_KEY_JSON_LOGIN.get(0))),
                                Config.GET_KEY_JSON_USER);

                if (arrayUser != null) {
                    userId = Integer.parseInt(arrayUser.get(0));
                    userName = arrayUser.get(1);
                    fullName = arrayUser.get(2);
                    if (!arrayUser.get(3).equals(Config.NULL)) {
                        avatar = setImage(Config.URL_HOST + Config.URL_GET_AVATAR + arrayUser.get(3),
                                Config.FOLDER_AVATAR, arrayUser.get(3));
                    } else {
                        avatar = null;
                    }
                    String type = arrayUser.get(4);
                    type = type.substring(1, type.length() - 1);
                    if (type.length() == 1) {
                        userType.add(type);
                    } else {
                        userType.addAll(Arrays.asList(type.split(",")));
                    }
                }
            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get stored session data
     */
    private HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        // username
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        username = null;
        password = null;
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
