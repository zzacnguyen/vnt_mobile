package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class FragmentEditProfile extends Fragment implements View.OnClickListener {

    EditText etFullName, etPhoneNumber, etWebsite, etEmail,
            etLanguage, etCountry;
    CircleImageView imgAvatar;
    TextView btnChangeAvatar, btnCancel, btnDone;

    private int REQUEST_CODE = 1;
    public Bitmap bitmap;
    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

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

        btnCancel.setOnClickListener(this);
        btnChangeAvatar.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        return view;
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

                try {
                    JSONObject jsonEditProfile = new JSONObject("{"
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(0) + ":\"" + etFullName.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(1) + ":\"" + etPhoneNumber.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(2) + ":\"" + etWebsite.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(3) + ":\"" + etEmail.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(4) + ":\"" + etLanguage.getText() + "\","
                            + Config.POST_KEY_JSON_CONTACT_INFO.get(5) + ":\"" + etCountry.getText() + "\"}");

                    String sttPostProfile = new HttpRequestAdapter.httpPost(jsonEditProfile)
                            .execute(Config.URL_HOST + Config.URL_POST_CONTACT_INFO + userId).get();

                    if (sttPostProfile.equals("1")){
                        Toast.makeText(getContext(), getResources().getString(R.string.text_ChangeSuccessful),
                                Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }else{
                        Toast.makeText(getContext(), getResources().getString(R.string.text_AddFailed), Toast.LENGTH_SHORT).show();
                    }
                    
                } catch (JSONException | InterruptedException | ExecutionException e) {
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

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imgAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
