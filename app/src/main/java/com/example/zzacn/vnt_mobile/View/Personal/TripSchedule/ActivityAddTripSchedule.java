package com.example.zzacn.vnt_mobile.View.Personal.TripSchedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class ActivityAddTripSchedule extends AppCompatActivity {

    ImageButton btnBack;
    Button btnCreate;
    EditText etTripName, etEndDate, etStartDate;
    Boolean checkInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtripschedule);

        btnBack = findViewById(R.id.button_Back);
        etTripName = findViewById(R.id.editText_TripName);
        etEndDate = findViewById(R.id.editText_EndDatePicker);
        btnCreate = findViewById(R.id.button_CreateTripSchedule);
        etStartDate = findViewById(R.id.editText_StartDatePicker);

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
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                String stt = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = null, endDate = null;
                try {
                    startDate = new SimpleDateFormat("dd/MM/yyyy").parse(etStartDate.getText().toString());
                    endDate = new SimpleDateFormat("dd/MM/yyyy").parse(etEndDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (etTripName.getText() != null && etStartDate.getText() != null && etEndDate.getText() != null) {
                    if (endDate.before(startDate)) {
                        Toast.makeText(ActivityAddTripSchedule.this, getResources().getString(R.string.text_CheckDate), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jsonPost = new JSONObject("{"
                                    + Config.POST_KEY_JSON_TRIP_SCHEDULE.get(0) + ":\"" + etTripName.getText().toString() + "\","
                                    + Config.POST_KEY_JSON_TRIP_SCHEDULE.get(1) + ":\"" + sdf.format(startDate) + "\","
                                    + Config.POST_KEY_JSON_TRIP_SCHEDULE.get(2) + ":\"" + sdf.format(endDate) + "\"}");
                            stt = new HttpRequestAdapter.httpPost(jsonPost)
                                    .execute(Config.URL_HOST + Config.URL_POST_TRIP_SCHEDULE + userId).get();

                            Toast.makeText(ActivityAddTripSchedule.this, getResources().getString(R.string.text_AddNewSuccess), Toast.LENGTH_SHORT).show();
                        } catch (JSONException | InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(ActivityAddTripSchedule.this, getResources().getString(R.string.Toast_TheInformationIsNotEnpty), Toast.LENGTH_SHORT).show();
                }

                // nếu status != null và = OK
                if (Objects.equals(stt, "\"status:200\"")) {
                    finish();
                } else {
                    Toast.makeText(ActivityAddTripSchedule.this
                            , getResources().getString(R.string.text_AddFailed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("mess", getResources().getString(R.string.text_AddNewSuccess));
        setResult(RESULT_OK, data);
        finishActivity(1);
        super.finish();
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

}

