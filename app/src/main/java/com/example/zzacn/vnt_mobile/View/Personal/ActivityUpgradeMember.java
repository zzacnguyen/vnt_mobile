package com.example.zzacn.vnt_mobile.View.Personal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;


public class ActivityUpgradeMember extends AppCompatActivity {

    ImageView btnBack;
    int privilege;
    Button btnUpgrade;
    TextView tvUserName, tvUserType, tvChangeAvatar;
    EditText etFullName, etPhoneNumber, etWebsite, etEmail, etLanguage, etCountry;
    Spinner spnrUserType;
    CircleImageView Cavatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrademember);

        btnBack = findViewById(R.id.btn_Back);
        Cavatar = findViewById(R.id.avatar);
        tvUserName = findViewById(R.id.txtUserName);
        tvUserType = findViewById(R.id.txtUserType);
        tvChangeAvatar = findViewById(R.id.txtUpdateAvatar);
        btnUpgrade = findViewById(R.id.btn_Upgrade);
        spnrUserType = findViewById(R.id.spnrUserType);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etWebsite = findViewById(R.id.etWebsite);
        etEmail = findViewById(R.id.etEmail);
        etLanguage = findViewById(R.id.etLanguage);
        etCountry = findViewById(R.id.etCountry);

        tvUserName.setText(userName);
        Cavatar.setImageBitmap(avatar);
        switch (userType) {
            case "1": // cá nhân
                tvUserType.setText(getResources().getString(R.string.text_Personal));
                break;
            case "2": // doanh nghiệp
                tvUserType.setText(getResources().getString(R.string.text_Enterprise));
                break;
            case "3": // hướng dẫn viên
                tvUserType.setText(getResources().getString(R.string.text_TourGuide));
                break;
            case "4": // cộng tác viên
                tvUserType.setText(getResources().getString(R.string.text_Partner));
                break;
            case "5": // mod
                tvUserType.setText(getResources().getString(R.string.text_Moderator));
                break;
            case "6": // admin
                tvUserType.setText(getResources().getString(R.string.text_Admin));
                break;
        }

        // load các loại người dùng có thể nâng cấp vào spinner
        final ArrayList<String> arrayNumberPrivileges = new ArrayList<>(), arrayNamePrivileges = new ArrayList<>();
        try {
            String privileges = new HttpRequestAdapter.httpGet()
                    .execute(Config.URL_HOST + Config.URL_GET_PRIVILEGE + userId).get();
            privileges = privileges.substring(1, privileges.length() - 1);
            arrayNumberPrivileges.addAll(Arrays.asList(privileges.split(",")));
            for (int i = 0; i < arrayNumberPrivileges.size(); i++) {
                switch (arrayNumberPrivileges.get(i)) {
                    // 1 là doanh nghiệp
                    case "1":
                        arrayNamePrivileges.add(getResources().getString(R.string.text_Enterprise));
                        break;
                    // 2 là hdv
                    case "2":
                        arrayNamePrivileges.add(getResources().getString(R.string.text_TourGuide));
                        break;
                    // 3 là partner
                    case "3":
                        arrayNamePrivileges.add(getResources().getString(R.string.text_Partner));
                        break;
                    // 4 là mod
//                    case "4":
//                        arrayNamePrivileges.add(getResources().getString(R.string.text_Moderator));
//                        break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapterPrivileges =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayNamePrivileges);

        arrayAdapterPrivileges.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrUserType.setAdapter(arrayAdapterPrivileges);

        spnrUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                privilege = Integer.parseInt(arrayNumberPrivileges.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // post avatar
        tvChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                ByteArrayOutputStream ban = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.JPEG, 80, ban);
                ContentBody contentAvatar = new ByteArrayBody(ban.toByteArray(), "a.jpg");
                reqEntity.addPart("banner", contentAvatar);
                try {
                    // post hình lên
                    String response = new HttpRequestAdapter.httpPostImage(reqEntity).execute(Config.URL_HOST
                            + Config.URL_POST_IMAGE + userId).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        // post thông tin người dùng + nâng cấp quyền
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean isPostContactSuccess = false, isPostPrivilegeSuccess = false;

                    JSONObject jsonContactInfo = new JSONObject("{"
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(0) + ":\"" + etFullName.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(1) + ":\"" + etPhoneNumber.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(2) + ":\"" + etWebsite.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(3) + ":\"" + etEmail.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(4) + ":\"" + etLanguage.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(5) + ":\"" + etCountry.getText() + "\"}");
                    String sttPostContact = new HttpRequestAdapter.httpPost(jsonContactInfo)
                            .execute(Config.URL_HOST + Config.URL_POST_CONTACT_INFO + userId).get();
                    if (sttPostContact.equals("1"))
                        isPostContactSuccess = true;

                    JSONObject jsonPrivilege = new JSONObject("{" + Config.POST_KEY_JSON_PRIVILEGE + ":\"" + privilege + "\"}");
                    String sttPostPrivilege = new HttpRequestAdapter.httpPost(jsonPrivilege)
                            .execute(Config.URL_HOST + Config.URL_POST_UPGRADE_MEMBER + userId).get();
                    if (sttPostPrivilege.equals("1"))
                        isPostPrivilegeSuccess = true;

                    if (isPostContactSuccess && isPostPrivilegeSuccess) {
                        finish();
                    } else {
                        Toast.makeText(ActivityUpgradeMember.this,
                                getResources().getString(R.string.text_AddFailed), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
