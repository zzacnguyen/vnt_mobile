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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.avatar;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userType;

public class FragmentProfile extends Fragment implements View.OnClickListener {

    public Bitmap bitmapAvatar;
    boolean isChangeAvatar = false;
    EditText etFullName, etPhoneNumber, etWebsite, etEmail,
            etLanguage, etCountry, etObjectiveDetail, etStrengthDetail;
    CircleImageView imgAvatar;
    TextView btnChangeAvatar, btnCancel, btnDone, toolbar;
    Button btnEditProfile;
    LinearLayout linearButtonEdit, linearObjectiveDetail, linearStrengthDetail;
    private int REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnCancel = view.findViewById(R.id.textView_Cancel);
        btnDone = view.findViewById(R.id.textView_Done);
        btnChangeAvatar = view.findViewById(R.id.textView_ChangeAvatar);

        imgAvatar = view.findViewById(R.id.avatar);
        etFullName = view.findViewById(R.id.editText_FullName);
        etPhoneNumber = view.findViewById(R.id.editText_PhoneNumber);
        etWebsite = view.findViewById(R.id.editText_Website);
        etEmail = view.findViewById(R.id.editText_Email);
        etLanguage = view.findViewById(R.id.editText_Language);
        etCountry = view.findViewById(R.id.editText_Country);
        toolbar = view.findViewById(R.id.toolbarTitle);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        linearButtonEdit = view.findViewById(R.id.linearButtonEdit);
        etObjectiveDetail = view.findViewById(R.id.editText_ObjectiveDetail);
        etStrengthDetail = view.findViewById(R.id.editText_StrengthDetail);
        linearObjectiveDetail = view.findViewById(R.id.ObjectiveDetail);
        linearStrengthDetail = view.findViewById(R.id.StrengthDetail);

        if (userType.contains("3")) {
            linearStrengthDetail.setVisibility(View.VISIBLE);
            linearObjectiveDetail.setVisibility(View.VISIBLE);
        }

        loadProfile();

        btnCancel.setOnClickListener(this);
        btnChangeAvatar.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFullName.setInputType(InputType.TYPE_CLASS_TEXT);
                etPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
                etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etWebsite.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                etCountry.setInputType(InputType.TYPE_CLASS_TEXT);
                etLanguage.setInputType(InputType.TYPE_CLASS_TEXT);
                toolbar.setText(getResources().getString(R.string.text_EditProfile));
                btnDone.setVisibility(View.VISIBLE);
                btnDone.setEnabled(true);
                btnChangeAvatar.setVisibility(View.VISIBLE);
                linearButtonEdit.setVisibility(View.GONE);
            }
        });

        return view;
    }

    void loadProfile() {
        etFullName.setInputType(InputType.TYPE_NULL);
        etPhoneNumber.setInputType(InputType.TYPE_NULL);
        etEmail.setInputType(InputType.TYPE_NULL);
        etWebsite.setInputType(InputType.TYPE_NULL);
        etCountry.setInputType(InputType.TYPE_NULL);
        etLanguage.setInputType(InputType.TYPE_NULL);
        etObjectiveDetail.setInputType(InputType.TYPE_NULL);
        etStrengthDetail.setInputType(InputType.TYPE_NULL);

        ArrayList<String> arrayContact, arrayTourguideInfo = null;
        try {
            String rs = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_CONTACT_INFO + userId).get();
            JSONObject jsonUserInfo = new JSONObject(rs);
            arrayContact = parseJsonNoId(jsonUserInfo.getJSONObject(Config.GET_KEY_JSON_USER_INFO.get(0)),
                    Config.GET_KEY_JSON_CONTACT_INFO);
            if (userType.contains("3")) {
                arrayTourguideInfo = parseJsonNoId(jsonUserInfo.getJSONObject(Config.GET_KEY_JSON_USER_INFO.get(1)),
                        Config.GET_KEY_JSON_TOURGUIDE_INFO);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            arrayContact = null;
            arrayTourguideInfo = null;
        }
        if (arrayContact != null) {
            etFullName.setText(arrayContact.get(0).equals(Config.NULL) ? "" : arrayContact.get(0));
            etPhoneNumber.setText(arrayContact.get(1).equals(Config.NULL) ? "" : arrayContact.get(1));
            etWebsite.setText(arrayContact.get(2).equals(Config.NULL) ? "" : arrayContact.get(2));
            etEmail.setText(arrayContact.get(3).equals(Config.NULL) ? "" : arrayContact.get(3));
            etLanguage.setText(arrayContact.get(4).equals(Config.NULL) ? "" : arrayContact.get(4));
            etCountry.setText(arrayContact.get(5).equals(Config.NULL) ? "" : arrayContact.get(5));
        }
        if (arrayTourguideInfo != null) {
            etObjectiveDetail.setText(arrayTourguideInfo.get(0).equals(Config.NULL) ? "" : arrayTourguideInfo.get(0));
            etStrengthDetail.setText(arrayTourguideInfo.get(1).equals(Config.NULL) ? "" : arrayTourguideInfo.get(1));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_Cancel:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.textView_ChangeAvatar:
                PickImageFromGallery(REQUEST_CODE);
                break;

            case R.id.textView_Done:

                boolean isPostText = false, isPostImage = false;

                // region Post Text
                if (etFullName.getText().toString().equals("")) {
                    etFullName.setError(getResources().getString(R.string.text_FullNameIsNotAllowedToBeEmpty));
                } else {
                    String tourGuide;
                    if (userType.contains("3")) {
                        tourGuide = "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(6) + ":\"" + "1" + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(7) + ":\"" + etObjectiveDetail.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(8) + ":\"" + etStrengthDetail.getText();
                    } else {
                        tourGuide = "";
                    }
                    try {
                        JSONObject jsonEditProfile = new JSONObject("{"
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(0) + ":\"" + etFullName.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(1) + ":\"" + etPhoneNumber.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(2) + ":\"" + etWebsite.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(3) + ":\"" + etEmail.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(4) + ":\"" + etLanguage.getText() + "\","
                                + Config.POST_KEY_JSON_CONTACT_INFO.get(5) + ":\"" + etCountry.getText()
                                + tourGuide + "\"}");

                        String sttPostProfile = new HttpRequestAdapter.httpPost(jsonEditProfile)
                                .execute(Config.URL_HOST + Config.URL_POST_CONTACT_INFO + userId).get();

                        isPostText = sttPostProfile.equals("1");

                    } catch (JSONException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                // endregion

                // region post avatar
                if (isChangeAvatar) {
                    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    ByteArrayOutputStream de2 = new ByteArrayOutputStream();
                    bitmapAvatar.compress(Bitmap.CompressFormat.JPEG, 80, de2);
                    avatar = bitmapAvatar;
                    ContentBody contentAvatar = new ByteArrayBody(de2.toByteArray(), "c.jpg");

                    reqEntity.addPart("avatar", contentAvatar);
                    try {
                        // post hình lên
                        String response = new HttpRequestAdapter.httpPostImage(reqEntity).execute(Config.URL_HOST
                                + Config.URL_POST_AVATAR + userId).get();
                        // nếu post thành công trả về "status:200"
                        isPostImage = response.equals("\"status:200\"");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    isPostImage = true;
                }
                // endregion

                if (isPostText && isPostImage) {
                    Toast.makeText(getContext(), getResources().getString(R.string.text_EditSuccessfully),
                            Toast.LENGTH_SHORT).show();
                    reload();
                } else {
                    if (!isPostText) {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.text_AddProfileFailed), Toast.LENGTH_SHORT).show();
                    } else if (!isPostImage) {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.text_ChangeAvatarFailed), Toast.LENGTH_SHORT).show();
                    }
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
                imgAvatar.setImageBitmap(bitmapAvatar);
                if (avatar != bitmapAvatar) {
                    isChangeAvatar = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void reload() {
        FragmentPersonal fragmentPersonal = new FragmentPersonal();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentPersonal);
        fragmentTransaction.commit();
    }
}
