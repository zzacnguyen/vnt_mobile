package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentPersonal extends Fragment {

    public static int userId;
    public static String userName, userType;
    public static Bitmap avatar;
    Button btnUpgradeMember, btnGeneral, btnLogin, btnLogout;
    TextView txtUserName, txtUserType;
    CircleImageView Cavatar;
    LinearLayout linearUpgradeMember, Logout, Login, editProfile;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        btnUpgradeMember = view.findViewById(R.id.buttonRegEnterprise);
        btnGeneral = view.findViewById(R.id.buttonGeneral);
        btnLogin = view.findViewById(R.id.buttonLogin);
        btnLogout = view.findViewById(R.id.buttonLogout);
        txtUserName = view.findViewById(R.id.textView_UserName);
        txtUserType = view.findViewById(R.id.textView_UserType);
        Cavatar = view.findViewById(R.id.avatar);
        linearUpgradeMember = view.findViewById(R.id.RegEnterprise);
        Logout = view.findViewById(R.id.Logout);
        Login = view.findViewById(R.id.Login);
        editProfile = view.findViewById(R.id.EditProfile);

        if (userId == 0) {
            linearUpgradeMember.setVisibility(View.GONE);
            editProfile.setVisibility(View.GONE);
            Logout.setVisibility(View.GONE);
        } else {
            editProfile.setVisibility(View.VISIBLE);
            if (avatar != null) {
                Cavatar.setImageBitmap(avatar);
            }
            txtUserName.setText(userName);
            switch (userType) {
                case "1": // cá nhân
                    txtUserType.setText(getResources().getString(R.string.text_Personal));
                    linearUpgradeMember.setVisibility(View.VISIBLE);
                    break;
                case "2": // doanh nghiệp
                    txtUserType.setText(getResources().getString(R.string.text_Enterprise));
                    linearUpgradeMember.setVisibility(View.VISIBLE);
                    break;
                case "3": // hướng dẫn viên
                    txtUserType.setText(getResources().getString(R.string.text_TourGuide));
                    linearUpgradeMember.setVisibility(View.VISIBLE);
                    break;
                case "4": // cộng tác viên
                    txtUserType.setText(getResources().getString(R.string.text_Partner));
                    linearUpgradeMember.setVisibility(View.VISIBLE);
                    break;
                case "5": // mod
                    txtUserType.setText(getResources().getString(R.string.text_Moderator));
                    linearUpgradeMember.setVisibility(View.GONE);
                    break;
                case "6": // admin
                    txtUserType.setText(getResources().getString(R.string.text_Admin));
                    linearUpgradeMember.setVisibility(View.GONE);
                    break;
            }

            Logout.setVisibility(View.VISIBLE);
            Login.setVisibility(View.GONE);
        }

        btnUpgradeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ActivityUpgradeMember.class));
            }
        });

        btnGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ActivityGeneral.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager = new SessionManager(getApplicationContext());
                userId = 0;
                userName = null;
                userType = null;
                avatar = null;
                sessionManager.logoutUser();

                getActivity().getSupportFragmentManager().popBackStack();
                reload();
            }
        });

        return view;
    }

    void reload() {
        FragmentPersonal fragmentPersonal = new FragmentPersonal();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentPersonal);
        fragmentTransaction.commit();
    }
}
