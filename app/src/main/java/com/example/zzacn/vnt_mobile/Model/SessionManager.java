package com.example.zzacn.vnt_mobile.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static com.example.zzacn.vnt_mobile.View.Person.PersonFragment.avatar;
import static com.example.zzacn.vnt_mobile.View.Person.PersonFragment.userId;
import static com.example.zzacn.vnt_mobile.View.Person.PersonFragment.userName;
import static com.example.zzacn.vnt_mobile.View.Person.PersonFragment.userType;


/**
 * Created by sieut on 4/14/2018.
 */

public class SessionManager {
    // Id (make variable public to access from outside)
    private static final String KEY_ID = "id";
    // User name (make variable public to access from outside)
    private static final String KEY_NAME = "username";
    // Email type (make variable public to access from outside)
    private static final String KEY_TYPE = "level";
    // Email avatar (make variable public to access from outside)
    private static final String KEY_AVATAR = "avatar";
    // Sharedpref file name
    private static final String PREF_NAME = "Session";
    // All Shared Preferences Keys
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
    public void createLoginSession(String id, String name, String type, Bitmap bitmap) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing id in pref
        editor.putString(KEY_ID, id);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing type in pref
        editor.putString(KEY_TYPE, type);

        // Storing avatar in pref
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
            editor.putString(KEY_AVATAR, encoded);
        } else {
            editor.putString(KEY_AVATAR, null);
        }
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
            userId = Integer.parseInt(user.get(KEY_ID));
            userName = user.get(KEY_NAME);
            userType = user.get(KEY_TYPE);
            if (user.get(KEY_AVATAR) != null) {
                byte[] imageAsBytes = Base64.decode(user.get(KEY_AVATAR).getBytes(), Base64.DEFAULT);
                avatar = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            } else {
                avatar = null;
            }
        }

    }

    /**
     * Get stored session data
     */
    private HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        // user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user type
        user.put(KEY_TYPE, pref.getString(KEY_TYPE, null));

        // user avatar
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
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
