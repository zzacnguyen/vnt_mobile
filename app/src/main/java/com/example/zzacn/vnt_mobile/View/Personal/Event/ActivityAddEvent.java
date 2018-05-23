package com.example.zzacn.vnt_mobile.View.Personal.Event;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class ActivityAddEvent extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    Button btnCreate;
    EditText etEventName, etEventStart, etEventEnd;
    Spinner spEventType, spServiceVenue;
    int eventTypeId, serviceId;
    String start, end;

    // event type
    ArrayAdapter<String> arrayAdapterEventType;
    ArrayList<String> arrayIdEventType = new ArrayList<>();
    ArrayList<String> arrayListEventType = new ArrayList<>();

    // service venue
    ArrayAdapter<String> arrayAdapterServiceVenue;
    ArrayList<String> arrayIdServiceVenue = new ArrayList<>();
    ArrayList<String> arrayListServiceVenue = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);

        btnBack = findViewById(R.id.button_Back);
        btnCreate = findViewById(R.id.button_CreateEvent);
        etEventName = findViewById(R.id.editText_EventName);
        etEventStart = findViewById(R.id.editText_EventStart);
        etEventEnd = findViewById(R.id.editText_EventEnd);
        spEventType = findViewById(R.id.spinner_EventType);
        spServiceVenue = findViewById(R.id.spinner_ServiceVenue);

        btnBack.setOnClickListener(this);
        etEventStart.setOnClickListener(this);
        etEventEnd.setOnClickListener(this);
        btnCreate.setOnClickListener(this);

        //Load loại hình sự kiện vào spinner
        try {
            JSONArray jsonArrayEventType = new JSONArray(new HttpRequestAdapter.httpGet()
                    .execute(Config.URL_HOST + Config.URL_GET_EVENT_TYPE).get());
            arrayListEventType = parseJsonNoId(jsonArrayEventType, Config.GET_KEY_JSON_EVENT_TYPE);
            arrayIdEventType = parseJson(jsonArrayEventType, new ArrayList<String>());
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        arrayAdapterEventType = new ArrayAdapter<>(getApplication(),
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

        // load dịch vụ vào spinner
        try {
            JSONArray jsonArrayServiceVenue = new JSONArray(new HttpRequestAdapter.httpGet()
                    .execute(Config.URL_HOST + Config.URL_GET_ALL_SERVICE_VENUE + userId).get());
            ArrayList<String> arrayList = parseJsonNoId(jsonArrayServiceVenue, Config.GET_KEY_JSON_SERVICE_VENUE);
            for (int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).equals(Config.NULL)) {
                    arrayListServiceVenue.add(arrayList.get(i));
                }
            }
            arrayIdServiceVenue = parseJson(jsonArrayServiceVenue, new ArrayList<String>());
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        arrayAdapterServiceVenue = new ArrayAdapter<>(getApplication(),
                android.R.layout.simple_list_item_1, arrayListServiceVenue);
        arrayAdapterServiceVenue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spServiceVenue.setAdapter(arrayAdapterServiceVenue);

        spServiceVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceId = Integer.parseInt(arrayIdServiceVenue.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                // get date
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-đd");
                Date eventStart = null, eventEnd = null;
                try {
                    eventStart = new SimpleDateFormat("dd/MM/yyyy").parse(etEventStart.getText().toString());
                    eventEnd = new SimpleDateFormat("dd/MM/yyyy").parse(etEventEnd.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                start = sdf.format(eventStart);
                end = sdf.format(eventEnd);

                try {
                    JSONObject jsonPost = new JSONObject("{"
                            // event name
                            + Config.POST_KEY_JSON_EVENT.get(0) + ":\"" + etEventName.getText() + "\","
                            // event start
                            + Config.POST_KEY_JSON_EVENT.get(1) + ":\"" + start + "\","
                            // event end
                            + Config.POST_KEY_JSON_EVENT.get(2) + ":\"" + end + "\","
                            //  type id
                            + Config.POST_KEY_JSON_EVENT.get(3) + ":\"" + eventTypeId + "\","
                            // service id
                            + Config.POST_KEY_JSON_EVENT.get(4) + ":\"" + serviceId + "\"}");
                    stt = new HttpRequestAdapter.httpPost(jsonPost)
                            .execute(Config.URL_HOST + Config.URL_POST_EVENT).get();
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                //Nếu status != null và = OK
                if (Objects.equals(stt, "\"status:200\"")) {
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.text_AddNewSuccess), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
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
