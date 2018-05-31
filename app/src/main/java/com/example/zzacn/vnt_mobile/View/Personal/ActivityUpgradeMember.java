package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;


public class ActivityUpgradeMember extends AppCompatActivity implements View.OnClickListener {

    ImageView btnBack;
    int privilege;
    Button btnUpgrade;
    TextView tvUserName, tvUserType, tvChangeAvatar;
    EditText etFullName, etPhoneNumber, etWebsite, etEmail, etLanguage, etCountry;
    Spinner spnrUserType;
    CircleImageView Cavatar;
    Bitmap bitmapAvatar;
    Boolean isChangeAvatar = false;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrademember);

        btnBack = findViewById(R.id.button_Back);
        Cavatar = findViewById(R.id.avatar);
        tvUserName = findViewById(R.id.textView_UserName);
        tvUserType = findViewById(R.id.textView_UserType);
        tvChangeAvatar = findViewById(R.id.textView_UpdateAvatar);
        btnUpgrade = findViewById(R.id.button_Upgrade);
        spnrUserType = findViewById(R.id.spinner_UserType);
        etFullName = findViewById(R.id.editText_FullName);
        etPhoneNumber = findViewById(R.id.editText_PhoneNumber);
        etWebsite = findViewById(R.id.editText_Website);
        etEmail = findViewById(R.id.editText_Email);
        etLanguage = findViewById(R.id.editText_Language);
        etCountry = findViewById(R.id.editText_Country);

        tvUserName.setText(userName);
        if (avatar != null) {
            Cavatar.setImageBitmap(avatar);
        }
        StringBuilder stringUserType = null;
        for (int i = 0; i < userType.size(); i++) {
            if (userType.get(i).equals("2")) {
                stringUserType.append(getResources().getString(R.string.text_Enterprise));
            } else if (userType.get(i).equals("3")) {
                stringUserType.append(getResources().getString(R.string.text_TourGuide));
            } else {
                stringUserType = new StringBuilder(getResources().getString(R.string.text_Personal));
                break;
            }
            if (i < userType.size() - 1) {
                stringUserType.append(", ");
            }
        }
        tvUserType.setText(stringUserType.toString());

        // load thông tin người dùng lên nếu có
        loadProfile();

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

        btnBack.setOnClickListener(this);

        // Mở thư viện
        tvChangeAvatar.setOnClickListener(this);

        // post thông tin người dùng + nâng cấp quyền
        btnUpgrade.setOnClickListener(this);
    }

    void loadProfile() {
        etFullName.setInputType(InputType.TYPE_NULL);
        etPhoneNumber.setInputType(InputType.TYPE_NULL);
        etEmail.setInputType(InputType.TYPE_NULL);
        etWebsite.setInputType(InputType.TYPE_NULL);
        etCountry.setInputType(InputType.TYPE_NULL);
        etLanguage.setInputType(InputType.TYPE_NULL);
        ArrayList<String> arrayContact;
        try {
            String rs = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_CONTACT_INFO + userId).get();
            arrayContact = parseJsonNoId(new JSONObject(rs), Config.GET_KEY_JSON_CONTACT_INFO);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            arrayContact = null;
        }
        if (arrayContact != null) {
            etFullName.setText(arrayContact.get(0));
            etPhoneNumber.setText(arrayContact.get(1));
            etWebsite.setText(arrayContact.get(2));
            etEmail.setText(arrayContact.get(3));
            etLanguage.setText(arrayContact.get(4));
            etCountry.setText(arrayContact.get(5));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_Back:
                finish();
                break;
            case R.id.textView_UpdateAvatar:
                PickImageFromGallery(REQUEST_CODE);
                break;
            case R.id.button_Upgrade:
                try {
                    boolean isPostContactSuccess = false, isPostPrivilegeSuccess = false, isPostImage = false;

                    // region post thông tin người dùng
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
                    // endregion

                    // region post nâng quyền
                    JSONObject jsonPrivilege = new JSONObject("{\"quyen\":\"" + privilege + "\"}");
                    String sttPostPrivilege = new HttpRequestAdapter.httpPost(jsonPrivilege)
                            .execute(Config.URL_HOST + Config.URL_POST_UPGRADE_MEMBER + userId).get();
                    if (sttPostPrivilege.equals("1"))
                        isPostPrivilegeSuccess = true;
                    // endregion

                    // region post avatar
                    if (isChangeAvatar) {
                        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                        ByteArrayOutputStream ban = new ByteArrayOutputStream();
                        avatar.compress(Bitmap.CompressFormat.JPEG, 80, ban);
                        ContentBody contentAvatar = new ByteArrayBody(ban.toByteArray(), "a.jpg");
                        reqEntity.addPart("avatar", contentAvatar);
                        try {
                            // post hình lên
                            String response = new HttpRequestAdapter.httpPostImage(reqEntity).execute(Config.URL_HOST
                                    + Config.URL_POST_IMAGE + userId).get();
                            // nếu post thành công trả về "status:200"
                            if (response.equals("\"status:200\"")) {
                                isPostImage = true;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    // endregion

                    if (isPostContactSuccess && isPostPrivilegeSuccess && isPostImage) {
                        finish();
                    } else {
                        if (!isPostContactSuccess) {
                            Toast.makeText(ActivityUpgradeMember.this,
                                    getResources().getString(R.string.text_AddProfileFailed), Toast.LENGTH_SHORT).show();
                        } else if (!isPostPrivilegeSuccess) {
                            Toast.makeText(ActivityUpgradeMember.this,
                                    getResources().getString(R.string.text_UpgradePrivilegeFailed), Toast.LENGTH_SHORT).show();
                        } else if (!isPostImage) {
                            Toast.makeText(ActivityUpgradeMember.this,
                                    getResources().getString(R.string.text_ChangeAvatarFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void PickImageFromGallery(int requestCode) { //Chọn 1 tấm hình từ thư viện
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình..."), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmapAvatar = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Cavatar.setImageBitmap(bitmapAvatar);
                if (avatar != bitmapAvatar) {
                    isChangeAvatar = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
