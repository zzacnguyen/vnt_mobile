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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.BottomNavigationViewHelper;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Favorite.FavoriteFragment;
import com.example.zzacn.vnt_mobile.View.Home.FragmentEnterpriseHome;
import com.example.zzacn.vnt_mobile.View.Home.FragmentHome;
import com.example.zzacn.vnt_mobile.View.Home.FragmentListService;
import com.example.zzacn.vnt_mobile.View.Notification.FragmentNotification;
import com.example.zzacn.vnt_mobile.View.Personal.FragmentEditProfile;
import com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal;
import com.example.zzacn.vnt_mobile.View.Personal.Login_Register.FragmentLogin;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    public static Fragment childFragment = null;

    private void BottomNavigation(){
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
                        if (userId != 2){
                            selectedFragment = new FragmentHome();
                        }else{
                            selectedFragment = new FragmentEnterpriseHome();
                        }
                        break;

                    case R.id.nav_favorite:
                        i = 1;
                        selectedFragment = new FavoriteFragment();
                        break;

                    case R.id.nav_notification:
                        i = 2;
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
                        selectedFragment).addToBackStack("main").commit(); //Gọi fragment ra view

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

        BottomNavigation();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentHome()).commit();
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

            case R.id.btnAllService: //Danh sách các địa điểm dịch vụ của doanh nghiệp đó
                bundle.putString("url", "link");
                childFragment.setArguments(bundle);
                break;

            case R.id.buttonLogin:
                childFragment = new FragmentLogin();
                break;

            case R.id.button_EditProfile:
                childFragment = new FragmentEditProfile();
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
