package com.example.zzacn.vnt_mobile.View;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.BottomNavigationViewHelper;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Favorite.FavoriteFragment;
import com.example.zzacn.vnt_mobile.View.Home.HomeFragment;
import com.example.zzacn.vnt_mobile.View.Home.ListServiceFragment;
import com.example.zzacn.vnt_mobile.View.Notification.NotificationFragment;
import com.example.zzacn.vnt_mobile.View.Personal.PersonFragment;

public class MainActivity extends AppCompatActivity {

    public static Fragment childFragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.nav_favorite:
                            selectedFragment = new FavoriteFragment();
                            break;

                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();
                            break;

                        case R.id.nav_person:
                            selectedFragment = new PersonFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).addToBackStack("main").commit(); //Gọi fragment ra view

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isStoragePermissionGranted()) {
            finish();
            startActivity(getIntent());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
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
        childFragment = new ListServiceFragment();
        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.btnAllPlace:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_PLACES);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllEat:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_EATS);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllHotel:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_HOTELS);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllEntertain:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_ENTERTAINMENTS);
                childFragment.setArguments(bundle);
                break;

            case R.id.btnAllVehicle:
                bundle.putString("url", Config.URL_HOST + Config.URL_GET_ALL_VEHICLES);
                childFragment.setArguments(bundle);
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, childFragment);
        fragmentTransaction.addToBackStack("stack");
        fragmentTransaction.commit();

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                childFragment).commit();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
