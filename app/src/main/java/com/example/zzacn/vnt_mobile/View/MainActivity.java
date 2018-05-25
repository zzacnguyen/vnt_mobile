package com.example.zzacn.vnt_mobile.View;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.BottomNavigationViewHelper;
import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Favorite.FavoriteFragment;
import com.example.zzacn.vnt_mobile.View.Home.FragmentHome;
import com.example.zzacn.vnt_mobile.View.Home.FragmentListService;
import com.example.zzacn.vnt_mobile.View.Notification.FragmentNotification;
import com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal;
import com.example.zzacn.vnt_mobile.View.Personal.FragmentProfile;
import com.example.zzacn.vnt_mobile.View.Personal.Login_Register.FragmentLogin;

import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class MainActivity extends AppCompatActivity {

    public static Fragment childFragment = null;
    BottomNavigationView bottomNavigationView;
    int badgeNumber = 0;
    SessionManager sessionManager;
    View badge;
    BottomNavigationItemView itemView;
    TextView txtBadge;

    public void BottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation); //Bottom navigation view
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                int i = 0;
                Menu menu = bottomNavigationView.getMenu();

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        i = 0;
                        selectedFragment = new FragmentHome();
                        break;

                    case R.id.nav_favorite:
                        i = 1;
                        selectedFragment = new FavoriteFragment();
                        break;

                    case R.id.nav_notification:
                        i = 2;
                        itemView.removeView(badge);
                        selectedFragment = new FragmentNotification();
                        break;

                    case R.id.nav_person:
                        i = 3;
                        selectedFragment = new FragmentPersonal();
                        break;
                }

                MenuItem menuItem = menu.getItem(i);
                menuItem.setChecked(true);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit(); //Gọi fragment ra view

                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();
        startActivity(getIntent());

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        BottomNavigation();

        //region Badge Number

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        itemView = (BottomNavigationItemView) v;

        badge = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.notification_badge, bottomNavigationMenuView, false);

        txtBadge = badge.findViewById(R.id.textView_BadgeNumber);
        try {
            String rs = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_EVENT_NUMBER + userId).get();
            badgeNumber = Integer.parseInt(!rs.equals("") ? rs : "0");
            if (badgeNumber != 0) {
                txtBadge.setText(String.valueOf(badgeNumber)); //Set số hiển thị
                itemView.addView(badge); //Hiển thị ra ngoài
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //endregion

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentHome()).commit();

        Thread t = new Thread() {
            @Override
            public void run() {

                while (!isInterrupted()) try {
                    Thread.sleep(60000);  //1000ms = 1 sec

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                badgeNumber = Integer.parseInt(new HttpRequestAdapter.httpGet()
                                        .execute(Config.URL_HOST + Config.URL_GET_EVENT_NUMBER + userId).get());
                                if (badgeNumber != 0) {
                                    txtBadge.setText(String.valueOf(badgeNumber)); //Set số hiển thị
                                    itemView.removeView(badge);
                                    itemView.addView(badge); //Hiển thị ra ngoài
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) { //đếm số lượng trong ngăn xếp còn bao nhiêu số lượng fragment
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void addFragment(View view) {
        childFragment = new FragmentListService();
        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.btnAllPlace:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_PLACES);
                bundle.putInt("type", 4);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllEat:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_EATS);
                bundle.putInt("type", 1);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllHotel:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_HOTELS);
                bundle.putInt("type", 2);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllEntertain:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_ENTERTAINMENTS);
                bundle.putInt("type", 5);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllVehicle:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_VEHICLES);
                bundle.putInt("type", 3);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllService: //Danh sách các địa điểm dịch vụ của doanh nghiệp đó
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_ENTERPRISE_SERVICE + userId);
                bundle.putInt("type", 0);
                childFragment.setArguments(bundle);
                break;

            case R.id.buttonLogin:
                childFragment = new FragmentLogin();
                break;

            case R.id.button_EditProfile:
                childFragment = new FragmentProfile();
                break;
        }

        replaceFragment(childFragment);

    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
