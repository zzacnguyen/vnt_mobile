package com.example.zzacn.vnt_mobile.View.Personal.Login_Register;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;
import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentLogin extends Fragment {

    EditText etUserId, etPassword;
    Button btnReg, btnLogin;
    ImageView btnBack;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUserId = view.findViewById(R.id.editText_UserName);
        etPassword = view.findViewById(R.id.editText_Password);
        btnLogin = view.findViewById(R.id.button_Login);
        btnReg = view.findViewById(R.id.button_Register);
        btnBack = view.findViewById(R.id.button_Back);

        sessionManager = new SessionManager(getApplicationContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etUserId.getText().toString().equals("")) {
                    etUserId.setError(getResources().getString(R.string.text_UsernameIsNotAllowedToBeEmpty));
                } else if (etPassword.getText().toString().equals("")) {
                    etPassword.setError(getResources().getString(R.string.text_PasswordIsNotAllowedToBeEmpty));
                }else if(etUserId.getText().toString().equals("") && etPassword.getText().toString().equals("")){
                    Toast.makeText(getContext(), getResources().getString(R.string.Toast_UsernameOrPasswordIsEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonPost = new JSONObject("{" + Config.POST_KEY_JSON_LOGIN_REGISTER.get(0) + ":\""
                                + etUserId.getText().toString() + "\"," + Config.POST_KEY_JSON_LOGIN_REGISTER.get(1) + ":\""
                                + etPassword.getText().toString() + "\"}");
                        String rs = new HttpRequestAdapter.httpPost(jsonPost).execute(Config.URL_HOST + Config.URL_LOGIN).get();
                        JSONObject jsonGet = new JSONObject(rs);
                        // nếu status = error
                        if (jsonGet.getString(Config.GET_KEY_JSON_LOGIN.get(2)).equals(Config.GET_KEY_JSON_LOGIN.get(3))) {
                            Toast.makeText(getContext(), getResources().getString(R.string.text_TheUsernameOrPasswordIsIncorrect),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<String> arrayUser =
                                    JsonHelper.parseJson(new JSONObject(jsonGet.getString(Config.GET_KEY_JSON_LOGIN.get(0))),
                                            Config.GET_KEY_JSON_USER);

                            userId = Integer.parseInt(arrayUser.get(0));
                            userName = arrayUser.get(1);
                            userType = arrayUser.get(3);
                            if (!arrayUser.get(2).equals(Config.NULL)) {
                                avatar = setImage("", Config.FOLDER_AVATAR, arrayUser.get(2));
                            } else {
                                avatar = null;
                            }

                            sessionManager.createLoginSession(userId + "", userName, userType, avatar);

                            FragmentPersonal fragmentPersonal = new FragmentPersonal();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragmentPersonal);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityRegister.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
