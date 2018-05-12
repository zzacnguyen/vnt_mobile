package com.example.zzacn.vnt_mobile.View.Personal.Event;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;


public class ActivityAddEvent extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    EditText etEventName, etEventStart, etEventEnd, etServiceVenue;
    Spinner spEventType;
    int eventTypeId = 0;

    ArrayAdapter<String> arrayAdapterEventType;
    ArrayList<String> arrayIdEventType = new ArrayList<>();
    ArrayList<String> arrayListEventType = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);

        btnBack = findViewById(R.id.button_Back);
        etEventName = findViewById(R.id.editText_EventName);
        etEventStart = findViewById(R.id.editText_EventStart);
        etEventEnd = findViewById(R.id.editText_EventEnd);
        etServiceVenue = findViewById(R.id.editText_ServiceVenue);
        spEventType = findViewById(R.id.spinner_EventType);

        btnBack.setOnClickListener(this);
        etEventStart.setOnClickListener(this);
        etEventEnd.setOnClickListener(this);

        //Load loại hình dịch vụ vào spinner
        try {
            JSONArray jsonArrayEventType = new JSONArray(new HttpRequestAdapter.httpGet()
                    .execute(Config.URL_HOST + Config.URL_GET_EVENT_TYPE).get());

            arrayListEventType = parseJsonNoId(jsonArrayEventType, Config.GET_KEY_JSON_EVENT_TYPE);
            arrayIdEventType = parseJson(jsonArrayEventType, new ArrayList<String>());
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        arrayAdapterEventType = new ArrayAdapter<String>(getApplication(),
                android.R.layout.simple_list_item_1, arrayListEventType);
        arrayAdapterEventType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spEventType.setAdapter(arrayAdapterEventType);

        spEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eventTypeId = Integer.parseInt(arrayIdEventType.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_Back:
                finish();
                break;

            case R.id.editText_EventStart:
                datePicker(etEventStart);
                break;

            case R.id.editText_EventEnd:
                datePicker(etEventEnd);
                break;

            case R.id.button_CreateEvent:
                String stt = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date eventStart = null, eventEnd = null;

                try {
                    eventStart = new SimpleDateFormat("dd/MM/yyyy").parse(etEventStart.getText().toString());
                    eventEnd = new SimpleDateFormat("dd/MM/yyyy").parse(etEventEnd.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonPost = new JSONObject("{"
                            + Config.POST_KEY_EVENT.get(0) + ":\"" + etEventName.getText().toString() + "\","
                            + Config.POST_KEY_EVENT.get(1) + ":\"" + sdf.format(eventStart) + "\","
                            + Config.POST_KEY_EVENT.get(2) + ":\"" + sdf.format(eventEnd) + "\","
                            + Config.POST_KEY_EVENT.get(3) + ":\"" + eventTypeId + "\","
                            + Config.POST_KEY_EVENT.get(4) + ":\"" + etServiceVenue.getText().toString() + "\"}");
                    stt = new HttpRequestAdapter.httpPost(jsonPost)
                            .execute(Config.URL_HOST + Config.URL_POST_EVENT).get();

                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                //Nếu status != null và = OK
                if(Objects.equals(stt, "\"status:200\"")){
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.text_SuccessfullyAdded), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ActivityAddEvent.this,
                            getResources().getString(R.string.text_AddFailed), Toast.LENGTH_SHORT).show();
                }

                break;
        }
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
