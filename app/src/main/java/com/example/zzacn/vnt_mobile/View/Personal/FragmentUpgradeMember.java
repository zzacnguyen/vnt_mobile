package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static android.app.Activity.RESULT_OK;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userName;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;

public class FragmentUpgradeMember extends Fragment implements View.OnClickListener {

    ImageView btnBack;
    int privilege;
    Button btnUpgrade;
    TextView tvUserName, tvUserType, tvChangeAvatar;
    EditText etFullName, etPhoneNumber, etWebsite, etEmail, etLanguage, etCountry,
            etObjectiveDetail, etStrengthDetail; // Mới thêm
    LinearLayout lnTourGuide;
    Spinner spnrUserType;
    CircleImageView Cavatar;
    Bitmap bitmapAvatar;
    Boolean isChangeAvatar = false;
    private int REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrademember, container, false);

        btnBack = view.findViewById(R.id.button_Back);
        Cavatar = view.findViewById(R.id.avatar);
        tvUserName = view.findViewById(R.id.textView_UserName);
        tvUserType = view.findViewById(R.id.textView_UserType);
        tvChangeAvatar = view.findViewById(R.id.textView_UpdateAvatar);
        btnUpgrade = view.findViewById(R.id.button_Upgrade);
        spnrUserType = view.findViewById(R.id.spinner_UserType);
        etFullName = view.findViewById(R.id.editText_FullName);
        etPhoneNumber = view.findViewById(R.id.editText_PhoneNumber);
        etWebsite = view.findViewById(R.id.editText_Website);
        etEmail = view.findViewById(R.id.editText_Email);
        etLanguage = view.findViewById(R.id.editText_Language);
        etCountry = view.findViewById(R.id.editText_Country);
        etObjectiveDetail = view.findViewById(R.id.editText_ObjectiveDetail);
        etStrengthDetail = view.findViewById(R.id.editText_StrengthDetail);
        lnTourGuide = view.findViewById(R.id.TourGuide);

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

        // region load các loại người dùng có thể nâng cấp vào spinner
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
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayNamePrivileges);

        arrayAdapterPrivileges.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrUserType.setAdapter(arrayAdapterPrivileges);
        // endregion

        spnrUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                privilege = Integer.parseInt(arrayNumberPrivileges.get(i));

                if(privilege == 2){ //Mới thêm *******************************************************************************
                    lnTourGuide.setVisibility(View.VISIBLE);
                }else{
                    lnTourGuide.setVisibility(View.GONE);
                }
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

        return view;
    }

    void loadProfile() {
        ArrayList<String> arrayContact;
        try {
            String rs = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_CONTACT_INFO + userId).get();
            arrayContact = parseJsonNoId(new JSONObject(rs), Config.GET_KEY_JSON_CONTACT_INFO);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            arrayContact = null;
        }
        if (arrayContact != null) {
            etFullName.setText(arrayContact.get(0).equals(Config.NULL) ? "" : arrayContact.get(0));
            etPhoneNumber.setText(arrayContact.get(1).equals(Config.NULL) ? "" : arrayContact.get(1));
            etWebsite.setText(arrayContact.get(2).equals(Config.NULL) ? "" : arrayContact.get(2));
            etEmail.setText(arrayContact.get(3).equals(Config.NULL) ? "" : arrayContact.get(3));
            etLanguage.setText(arrayContact.get(4).equals(Config.NULL) ? "" : arrayContact.get(4));
            etCountry.setText(arrayContact.get(5).equals(Config.NULL) ? "" : arrayContact.get(5));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_Back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.textView_UpdateAvatar:
                PickImageFromGallery(REQUEST_CODE);
                break;
            case R.id.button_Upgrade:
                try {
                    boolean isPostContactSuccess = false, isPostPrivilegeSuccess = false, isPostImage = false;

                    // region post thông tin người dùng
                    if (etFullName.getText().toString().equals("")) {
                        etFullName.setError(getResources().getString(R.string.text_FullNameIsNotAllowedToBeEmpty));
                    } else if (etPhoneNumber.getText().toString().equals("")) {
                        etPhoneNumber.setError(getResources().getString(R.string.text_PhoneNumberIsNotAllowedToBeEmpty));
                    } else {
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
                    }
                    // endregion

                    // region post avatar
                    if (isChangeAvatar) {
                        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                        ByteArrayOutputStream ban = new ByteArrayOutputStream();
                        bitmapAvatar.compress(Bitmap.CompressFormat.JPEG, 80, ban);
                        avatar = bitmapAvatar;
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
                    } else {
                        isPostImage = true;
                    }
                    // endregion

                    // region post nâng quyền
                    if (isPostContactSuccess && isPostImage) {
                        JSONObject jsonPrivilege = new JSONObject("{\"quyen\":\"" + privilege + "\"}");
                        String sttPostPrivilege = new HttpRequestAdapter.httpPost(jsonPrivilege)
                                .execute(Config.URL_HOST + Config.URL_POST_UPGRADE_MEMBER + userId).get();
                        if (sttPostPrivilege.equals("1"))
                            isPostPrivilegeSuccess = true;
                    }
                    // endregion

                    if (isPostContactSuccess && isPostPrivilegeSuccess && isPostImage) {
                        Toast.makeText(getContext(), getResources().getString(R.string.Toast_UpgradeSuccessfuly), Toast.LENGTH_SHORT).show();
                        reload(); //Mới thêm *****************************************************************************
                    } else {
                        if (!isPostContactSuccess) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.text_AddProfileFailed), Toast.LENGTH_SHORT).show();
                        } else if (!isPostPrivilegeSuccess) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.text_UpgradePrivilegeFailed), Toast.LENGTH_SHORT).show();
                        } else if (!isPostImage) {
                            Toast.makeText(getContext(),
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
                bitmapAvatar = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Cavatar.setImageBitmap(bitmapAvatar);
                if (avatar != bitmapAvatar) {
                    isChangeAvatar = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void reload() { //Mới thêm **************************************************************************
        FragmentPersonal fragmentPersonal = new FragmentPersonal();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentPersonal);
        fragmentTransaction.commit();
    }
}