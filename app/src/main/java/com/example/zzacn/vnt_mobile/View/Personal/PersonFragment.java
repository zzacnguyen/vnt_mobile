package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class PersonFragment extends Fragment {

    public static int userId;
    public static String userName, userType;
    public static Bitmap avatar;
    Button btnAddPlace, btnRegEnterprise, btnGeneral, btnLogin, btnLogout, btnTripSchedule, btnAddEvent;
    TextView txtUserName, txtUserType;
    CircleImageView Cavatar;
    LinearLayout addPlace, regEnterprise, Logout, Login, tripSchedule, addEvent;
    SessionManager sessionManager;
    int REQUEST_CODE_LOGIN = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_person, container, false);

        btnTripSchedule = view.findViewById(R.id.buttonTripSchedule);
        btnAddPlace = view.findViewById(R.id.buttonAddPlace);
        btnRegEnterprise = view.findViewById(R.id.buttonRegEnterprise);
        btnGeneral = view.findViewById(R.id.buttonGeneral);
        btnLogin = view.findViewById(R.id.buttonLogin);
        btnLogout = view.findViewById(R.id.buttonLogout);
        btnAddEvent = view.findViewById(R.id.buttonAddEvent);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserType = view.findViewById(R.id.txtUserType);
        Cavatar = view.findViewById(R.id.avatar);
        addPlace = view.findViewById(R.id.AddPlace);
        regEnterprise = view.findViewById(R.id.RegEnterprise);
        tripSchedule = view.findViewById(R.id.TripSchedule);
        Logout = view.findViewById(R.id.Logout);
        Login = view.findViewById(R.id.Login);
        addEvent = view.findViewById(R.id.AddEvent);

        if (userId == 0) {
            addPlace.setVisibility(View.GONE);
            tripSchedule.setVisibility(View.GONE);
            regEnterprise.setVisibility(View.GONE);
            Logout.setVisibility(View.GONE);
            addEvent.setVisibility(View.GONE);
        } else {
            Cavatar.setImageBitmap(avatar);
            txtUserName.setText(userName);
            switch (userType) {
                case "1": // cá nhân
                    txtUserType.setText(getResources().getString(R.string.text_Personal));
                    regEnterprise.setVisibility(View.VISIBLE);
                    break;
                case "2": // doanh nghiệp
                    txtUserType.setText(getResources().getString(R.string.text_Enterprise));
                    addPlace.setVisibility(View.VISIBLE);
                    addEvent.setVisibility(View.VISIBLE);
                    break;
                case "3": // hướng dẫn viên
                    txtUserType.setText(getResources().getString(R.string.text_TourGuide));
                    tripSchedule.setVisibility(View.VISIBLE);
                    break;
                case "4": // cộng tác viên
                    txtUserType.setText(getResources().getString(R.string.text_Partner));
                    addPlace.setVisibility(View.VISIBLE);
                    addEvent.setVisibility(View.VISIBLE);
                    regEnterprise.setVisibility(View.VISIBLE);
                    break;
                case "5": // mod
                    txtUserType.setText(getResources().getString(R.string.text_Moderator));
                    tripSchedule.setVisibility(View.VISIBLE);
                    addPlace.setVisibility(View.VISIBLE);
                    addEvent.setVisibility(View.VISIBLE);
                    break;
                case "6": // admin
                    txtUserType.setText(getResources().getString(R.string.text_Admin));
                    tripSchedule.setVisibility(View.VISIBLE);
                    addPlace.setVisibility(View.VISIBLE);
                    addEvent.setVisibility(View.VISIBLE);
                    break;
                default: // mặc định cá nhân
                    txtUserType.setText(getResources().getString(R.string.text_Personal));
                    regEnterprise.setVisibility(View.VISIBLE);
                    break;
            }

            Logout.setVisibility(View.VISIBLE);
            Login.setVisibility(View.GONE);
        }

//        btnTripSchedule.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent iTripSchedule = new Intent(ActivityPersonal.this, ActivityTripSchedule.class);
//                startActivity(iTripSchedule);
//            }
//        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), ActivityLogin.class), REQUEST_CODE_LOGIN);
            }
        });
//        btnAddPlace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent iThemDiaDiem = new Intent(ActivityPersonal.this, ActivityAddPlace.class);
//                startActivity(iThemDiaDiem);
//            }
//        });
//
//        btnRegEnterprise.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent iDangKyDoanhNghiep = new Intent(ActivityPersonal.this, ActivityRegCoop.class);
//                startActivity(iDangKyDoanhNghiep);
//            }
//        });
//
//        btnGeneral.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent iGeneral = new Intent(ActivityPersonal.this, ActivityGeneral.class);
//                startActivity(iGeneral);
//            }
//        });
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sessionManager = new SessionManager(getApplicationContext());
//                userId = 0;
//                userName = null;
//                userType = null;
//                avatar = null;
//                startActivity(getIntent());
//                sessionManager.logoutUser();
//                startActivity(new Intent(ActivityPersonal.this, ActivityPersonal.class));
//            }
//        });

        return view;
    }
}
