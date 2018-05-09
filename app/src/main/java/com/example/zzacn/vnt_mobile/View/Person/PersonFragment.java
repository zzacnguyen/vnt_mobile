package com.example.zzacn.vnt_mobile.View.Person;

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

        return view;
    }
}
