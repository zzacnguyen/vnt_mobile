package com.example.zzacn.vnt_mobile.View.Personal.TripSchedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.zzacn.vnt_mobile.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ActivityAddTripSchedule extends AppCompatActivity implements View.OnFocusChangeListener {

    ImageButton btnBack;
    Button btnCreate;
    EditText etTripName, etEndDate, etStartDate;
    Boolean checkInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtripschedule);

        btnBack = findViewById(R.id.btnBack);
        etTripName = findViewById(R.id.etTripName);
        etEndDate = findViewById(R.id.etEndDatePicker);
        btnCreate = findViewById(R.id.btnCreateTripSchedule);
        etStartDate = findViewById(R.id.etStartDatePicker);

        etTripName.setOnFocusChangeListener(this);
        etStartDate.setOnFocusChangeListener(this);
        etEndDate.setOnFocusChangeListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity(1);
                finish();
            }
        });


        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(etStartDate);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(etEndDate);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String stt = null;
//
//                if (checkInfo) {
//                    try {
//                        JSONObject jsonPost = new JSONObject("{"
//                                + Config.POST_KEY_TRIP_SCHEDULE.get(0) + ":\"" + etTripName.getText().toString() + "\","
//                                + Config.POST_KEY_TRIP_SCHEDULE.get(1) + ":\"" + etStartDate.getText().toString() + "\","
//                                + Config.POST_KEY_TRIP_SCHEDULE.get(2) + ":\"" + etEndDate.getText().toString() + "\","
//                                + Config.POST_KEY_TRIP_SCHEDULE.get(3) + ":\"" + 1 + "\","
//                                + Config.POST_KEY_TRIP_SCHEDULE.get(4) + ":\"" + userId + "\"}");
//                        stt = new HttpRequestAdapter.httpPost(jsonPost)
//                                .execute(Config.URL_HOST + Config.URL_POST_TRIP_SCHEDULE).get();
//                    } catch (JSONException | InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // nếu status != null và = OK
//                if (stt.equals("\'status:200\"")) {
//                    finish();
//                } else {
//                    Toast.makeText(ActivityAddTripSchedule.this, getResources().getString(R.string.text_AddFailed), Toast.LENGTH_SHORT).show();
//                }
            }
        });


    }

    private void datePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //i: năm, i1: tháng, i2: ngày
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                editText.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, date);
        datePickerDialog.show();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.etTripName:
                if (!b) {
                    String str = ((EditText) view).getText().toString();
                    if (str.trim().equals("")) {
                        etTripName.setError(getResources().getString(R.string.text_FieldIsEmpty));
                        checkInfo = false;
                    } else {
                        checkInfo = true;
                    }
                }
                break;

            case R.id.etStartDatePicker:
                if (!b) {
                    String str = ((EditText) view).getText().toString();
                    if (str.trim().equals("")) {
                        etStartDate.setError(getResources().getString(R.string.text_FieldIsEmpty));
                        checkInfo = false;
                    } else {
                        checkInfo = true;
                    }
                }
                break;

            case R.id.etEndDatePicker:
                if (!b) {
                    String str = ((EditText) view).getText().toString();
                    if (str.trim().equals("")) {
                        etEndDate.setError(getResources().getString(R.string.text_FieldIsEmpty));
                        checkInfo = false;
                    } else {

                        checkInfo = true;
                    }
                }
                break;
        }
    }
}
