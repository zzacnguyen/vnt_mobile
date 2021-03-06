package com.example.zzacn.vnt_mobile.View.Personal.Login_Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.fullName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;

public class ActivityLogin extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnReg, btnLogin;
    ImageView btnBack;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.editText_UserName);
        etPassword = findViewById(R.id.editText_Password);
        btnLogin = findViewById(R.id.button_Login);
        btnReg = findViewById(R.id.button_Register);
        btnBack = findViewById(R.id.button_Back);

        sessionManager = new SessionManager(getApplicationContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etUsername.getText().toString().equals("")) {
                    etUsername.setError(getResources().getString(R.string.text_UsernameIsNotAllowedToBeEmpty));
                } else if (etPassword.getText().toString().equals("")) {
                    etPassword.setError(getResources().getString(R.string.text_PasswordIsNotAllowedToBeEmpty));
                } else if (etUsername.getText().toString().equals("") && etPassword.getText().toString().equals("")) {
                    Toast.makeText(ActivityLogin.this, getResources().getString(R.string.Toast_UsernameOrPasswordIsEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonPost = new JSONObject("{" + Config.POST_KEY_JSON_LOGIN_REGISTER.get(0) + ":\""
                                + etUsername.getText().toString() + "\"," + Config.POST_KEY_JSON_LOGIN_REGISTER.get(1) + ":\""
                                + etPassword.getText().toString() + "\"}");
                        String rs = new HttpRequestAdapter.httpPost(jsonPost).execute(Config.URL_HOST + Config.URL_LOGIN).get();
                        JSONObject jsonGet = new JSONObject(rs);
                        // nếu status = error
                        if (jsonGet.getString(Config.GET_KEY_JSON_LOGIN.get(2)).equals(Config.GET_KEY_JSON_LOGIN.get(3))) {
                            Toast.makeText(ActivityLogin.this, getResources().getString(R.string.text_TheUsernameOrPasswordIsIncorrect),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<String> arrayUser =
                                    JsonHelper.parseJson(new JSONObject(jsonGet.getString(Config.GET_KEY_JSON_LOGIN.get(0))),
                                            Config.GET_KEY_JSON_USER);

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
                            sessionManager.createLoginSession(etUsername.getText().toString(), etPassword.getText().toString());

                            if (getCallingActivity() != null) {
                                finishActivity(1);
                                finish();
                            } else {
                                finish();
                            }
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
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 2) {
            if (data.hasExtra("mess"))
                Toast.makeText(this, data.getStringExtra("mess"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finishActivity(int requestCode) {
        Intent data = new Intent();
        data.putExtra("mess", getResources().getString(R.string.text_LoginSuccess));
        setResult(RESULT_OK, data);
        super.finishActivity(requestCode);
    }
}
