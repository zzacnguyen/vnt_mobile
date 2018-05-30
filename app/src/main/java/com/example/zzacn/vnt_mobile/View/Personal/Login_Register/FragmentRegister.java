package com.example.zzacn.vnt_mobile.View.Personal.Login_Register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FragmentRegister extends Fragment {

    EditText etUserName, etPassword, etConfirmPassword, etCountry, etLanguage;
    Button btnReg;
    ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etUserName = view.findViewById(R.id.editText_UserName);
        etPassword = view.findViewById(R.id.editText_Password);
        etConfirmPassword = view.findViewById(R.id.editText_PasswordConfirm);
        btnReg = view.findViewById(R.id.button_Register);
        btnBack = view.findViewById(R.id.button_Back);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json;
                String stt = null, error = null;
                int lenUserName = etUserName.getText().toString().length(), lenPassword = etPassword.getText().toString().length();
                if ((0 <= lenUserName && lenUserName < 5)
                        || etUserName.getText().toString().length() > 25) {
                    etUserName.setError(getResources().getString(R.string.text_YourUsernameMustBeBetween5And25));

                } else if ((0 <= lenPassword && lenPassword < 6)
                        || etPassword.getText().toString().length() > 26) {
                    etPassword.setError(getResources().getString(R.string.text_YourPasswordMustBeBetween6And26));

                } else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    etConfirmPassword.setError(getResources().getString(R.string.text_ThisDoesNotMatchThePasswordEnteredAbove));

                } else if (etUserName.getText().toString().contains(" ")) {
                    etUserName.setError(getResources().getString(R.string.text_TheUsernameCannotContainSpaces));
                } else {
                    try {
                        JSONObject jsonPost = new JSONObject("{"
                                + Config.POST_KEY_JSON_LOGIN_REGISTER.get(0) + ":\"" + etUserName.getText().toString() + "\","
                                + Config.POST_KEY_JSON_LOGIN_REGISTER.get(1) + ":\"" + etPassword.getText().toString() + "\"}");
                        json = new JSONObject(new HttpRequestAdapter.httpPost(jsonPost).execute(Config.URL_HOST + Config.URL_REGISTER).get());
                        // lấy status trả về
                        stt = json.getString(Config.GET_KEY_JSON_LOGIN.get(2));
                        // lấy lỗi trả về
                        error = json.getString(Config.GET_KEY_JSON_LOGIN.get(1));
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                // nếu status != null và = OK
                if (stt != null && stt.equals(Config.GET_KEY_JSON_LOGIN.get(4))) {
                    Toast.makeText(getContext(), getResources().getString(R.string.text_RegisterSuccess), Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    // nếu lỗi != null và = 3 => tài khoản trùng
                    if (error != null && error.equals("3")) {
                        etUserName.setError(getResources().getString(R.string.text_ThatUsernameIsAlreadyInUse));
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
