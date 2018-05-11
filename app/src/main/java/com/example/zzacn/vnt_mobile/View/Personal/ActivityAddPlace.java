package com.example.zzacn.vnt_mobile.View.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class ActivityAddPlace extends AppCompatActivity {

    private final int REQUEST_CODE_PLACEPICKER = 1;
    TextView txtLat, txtLong, btnCancel;
    EditText etAddress, etPlaceName, etPlacePhone, etPlaceAbout;
    Button btnPlacePicker;
    LinearLayout linearPlace, linearEat, linearHotel, linearEntertaiment, linearVehicle;
    String stringIdPlace;

    Spinner spinnerDistrict, spinnerProvince, spinnerWard;
    ArrayAdapter<String> arrayListProvince, arrayListDistrict, arrayListWard;
    int ID = 0;
    ArrayList<String> arrayIdProvince = new ArrayList<>(),
            arrayIdDistrict = new ArrayList<>(),
            arrayIdWard = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlocation);

        txtLat = findViewById(R.id.txtLatitude);
        txtLong = findViewById(R.id.txtLongitude);
        etAddress = findViewById(R.id.etAddress);
        etPlaceName = findViewById(R.id.etPlaceName);
        etPlacePhone = findViewById(R.id.etPlacePhone);
        etPlaceAbout = findViewById(R.id.etPlaceAbout);
        btnPlacePicker = findViewById(R.id.btnPlacePicker);
        btnCancel = findViewById(R.id.btnCancelLocation);
        linearPlace = findViewById(R.id.linearPlace);
        linearEat = findViewById(R.id.linearEat);
        linearHotel = findViewById(R.id.linearHotel);
        linearEntertaiment = findViewById(R.id.linearEntertainment);
        linearVehicle = findViewById(R.id.linearVehicle);

        //region SPINNER Province
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);

        // load tỉnh thành vào spinner
        ArrayList<String> arrayProvince = new ArrayList<>();
        try {
            JSONArray jsonArrayProvince = new JSONArray(new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_PROVINCE).get());
            arrayProvince = parseJsonNoId(jsonArrayProvince, Config.GET_KEY_JSON_PROVINCE);
            arrayIdProvince = parseJson(jsonArrayProvince, new ArrayList<String>());
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        arrayListProvince = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayProvince);
        arrayListProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(arrayListProvince);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // load quận huyện vào spinner
                ArrayList<String> arrayDistrict = new ArrayList<>();
                try {
                    JSONArray jsonArrayDistrict =
                            new JSONArray(new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_DISTRICT + arrayIdProvince.get(i)).get());
                    arrayDistrict = parseJsonNoId(jsonArrayDistrict, Config.GET_KEY_JSON_DISTRICT);
                    arrayIdDistrict = parseJson(jsonArrayDistrict, new ArrayList<String>());
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }
                arrayListDistrict = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayDistrict);
                arrayListDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDistrict.setAdapter(arrayListDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // load xã phường vào spinner
                ArrayList<String> arrayWard = new ArrayList<>();
                try {
                    JSONArray jsonArrayWard =
                            new JSONArray(new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_WARD + arrayIdDistrict.get(i)).get());
                    arrayWard = parseJsonNoId(jsonArrayWard, Config.GET_KEY_JSON_WARD);
                    arrayIdWard = parseJson(jsonArrayWard, new ArrayList<String>());
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }
                arrayListWard = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayWard);
                arrayListWard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWard.setAdapter(arrayListWard);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ID = Integer.parseInt(arrayIdWard.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion P  Provice

        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlacePickerActivity();
            }
        });

        linearPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postPlace())
                    openActivityAddService(4);
            }
        });

        linearEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postPlace())
                    openActivityAddService(1);
            }
        });

        linearHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postPlace())
                    openActivityAddService(2);
            }
        });

        linearEntertaiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postPlace())
                    openActivityAddService(5);
            }
        });

        linearVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postPlace())
                    openActivityAddService(3);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean postPlace() {
        if (etPlaceName.getText().toString().equals("")) {
            etPlaceName.setError(getResources().getString(R.string.text_WhatIsYourPlaceName));
        } else if (etAddress.getText().toString().equals("")) {
            etAddress.setError(getResources().getString(R.string.text_EnterYourAddress));
        } else if (etPlacePhone.getText().toString().equals("")) {
            etPlacePhone.setError(getResources().getString(R.string.text_EnterYourPhoneNumber));
        } else if (etPlaceAbout.getText().toString().equals("")) {
            etPlaceAbout.setError(getResources().getString(R.string.text_TypeYouDescription));
        } else if (ID == 0) {
            Toast.makeText(ActivityAddPlace.this, getResources().getString(R.string.text_ChooseAddress), Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonPost = new JSONObject("{"
                        // tên địa điểm
                        + Config.POST_KEY_JSON_PLACE.get(0) + ":\"" + etPlaceName.getText().toString() + "\","
                        // giới thiệu địa điểm
                        + Config.POST_KEY_JSON_PLACE.get(1) + ":\"" + etPlaceAbout.getText().toString() + "\","
                        // địa chỉ
                        + Config.POST_KEY_JSON_PLACE.get(2) + ":\"" + etAddress.getText().toString() + "\","
                        // sdt
                        + Config.POST_KEY_JSON_PLACE.get(3) + ":\"" + etPlacePhone.getText().toString() + "\","
                        // vĩ độ
                        + Config.POST_KEY_JSON_PLACE.get(4) + ":\"" + txtLat.getText().toString() + "\","
                        // kinh độ
                        + Config.POST_KEY_JSON_PLACE.get(5) + ":\"" + txtLong.getText().toString() + "\","
                        // mã xã phường
                        + Config.POST_KEY_JSON_PLACE.get(6) + ":\"" + ID + "\","
                        // id người dùng ctv
                        + Config.POST_KEY_JSON_PLACE.get(7) + ":\"" + userId + "\","
                        // id người dùng hdv
                        + Config.POST_KEY_JSON_PLACE.get(8) + ":\"" + "" + "\"" + "}");
                stringIdPlace = new HttpRequestAdapter.httpPost(jsonPost).execute(Config.URL_HOST + Config.URL_POST_PLACE).get();
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }

            if (stringIdPlace.equals("\"status:500\"")) {
                Toast.makeText(ActivityAddPlace.this, getResources()
                        .getString(R.string.text_Error), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                stringIdPlace = stringIdPlace.contains(":")
                        ? stringIdPlace.replaceAll("\"", "").split(":")[1] : "0";
                return true;
            }
        }
        return true;
    }

    private void openActivityAddService(int type) {
        Intent intent = new Intent(ActivityAddPlace.this, ActivityAddService.class);
        intent.putExtra("type", type);
        intent.putExtra("id", Integer.parseInt(stringIdPlace));
        startActivity(intent);
        finish();
    }

    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACEPICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data) {
        Place placeSelected = PlacePicker.getPlace(data, this);

        Double latitude = placeSelected.getLatLng().latitude;
        Double longitude = placeSelected.getLatLng().longitude;
        String placeName = placeSelected.getName().toString();

        txtLat.setText(String.valueOf(latitude).substring(0, 9));
        txtLong.setText(String.valueOf(longitude).substring(0, 10));
        etAddress.setText(placeSelected.getAddress().toString());
        if (!placeName.contains("\'")) {
            etPlaceName.setText(placeSelected.getName().toString());
        }
    }
}
