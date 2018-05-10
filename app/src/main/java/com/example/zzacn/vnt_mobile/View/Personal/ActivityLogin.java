package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Model.SessionManager;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Model.ModelService.setImage;
import static com.example.zzacn.vnt_mobile.View.Personal.PersonFragment.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.PersonFragment.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.PersonFragment.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.PersonFragment.userType;

public class ActivityLogin extends AppCompatActivity {

    EditText etUserId, etPassword;
    Button btnReg, btnLogin;
    int REQUEST_CODE_REGISTER = 1;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUserId = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnReg = findViewById(R.id.btnRegister);

        sessionManager = new SessionManager(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etUserId.getText().toString().equals("")) {
                    etUserId.setError(getResources().getString(R.string.text_UsernameIsNotAllowedToBeEmpty));
                } else if (etPassword.getText().toString().equals("")) {
                    etPassword.setError(getResources().getString(R.string.text_PasswordIsNotAllowedToBeEmpty));
                } else {
                    try {
                        JSONObject jsonPost = new JSONObject("{" + Config.POST_KEY_LOGIN.get(0) + ":\""
                                + etUserId.getText().toString() + "\"," + Config.POST_KEY_LOGIN.get(1) + ":\""
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
                            userType = arrayUser.get(3);
                            if (!arrayUser.get(2).equals(Config.NULL)) {
                                avatar = setImage("", Config.FOLDER_AVATAR, arrayUser.get(2));
                            } else {
                                avatar = null;
                            }

                            sessionManager.createLoginSession(userId + "", userName, userType, avatar);

                            Fragment personalFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_Personal);
                            if (getCallingActivity() != null) {
                                if (personalFragment.isAdded()) {
                                    finish();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new PersonFragment()).addToBackStack("person").commit();
                                } else {
                                    finishActivity(1);
                                    finish();
                                }
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
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });

    }
}
