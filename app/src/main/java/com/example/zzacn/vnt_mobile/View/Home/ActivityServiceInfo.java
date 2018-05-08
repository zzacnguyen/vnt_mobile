package com.example.zzacn.vnt_mobile.View.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.ServiceInfo;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.MainActivity.userId;

public class ActivityServiceInfo extends AppCompatActivity implements View.OnClickListener {
    public static String[] imgDetail = null;
    Button btnShare, btnLike, btnNear, btnReview, btnShowReview;
    ImageButton btnBack;
    TextView txtServiceName, txtServiceAbout, txtPrice, txtTime, txtAddress, txtPhoneNumber, txtWebsite,
            toolbarTitle, fbEvent, txtMark, txtCountLike;
    ImageView imgThumbInfo1, imgThumbInfo2, imgBanner;
    Toolbar toolbar;
    LinearLayout info;
    RatingBar rbStar;
    int idService, serviceType, REQUEST_CODE = 2;
    String idLike, idRating, longitude, latitude;
    JSONObject saveJson;

    @Override
    public void onClick(View view) {
        Intent iDetail = new Intent(ActivityServiceInfo.this, ActivityFullImage.class);

        switch (view.getId()) {
            case R.id.imgInfo1:
                try {
                    imgDetail = new HttpRequestAdapter.httpGet()
                            .execute(Config.URL_HOST + Config.URL_GET_LINK_DETAIL_1 + idService).get()
                            .replaceAll("\"", "")
                            .split("\\+");
                    startActivity(iDetail);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imgInfo2:
                try {
                    imgDetail = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_LINK_DETAIL_2 + idService).get()
                            .replaceAll("\"", "")
                            .split("\\+");
                    startActivity(iDetail);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info);

        btnShare = findViewById(R.id.btnShareService);
        btnLike = findViewById(R.id.btnLike);
        btnNear = findViewById(R.id.btnNearLocation);
        btnReview = findViewById(R.id.btnReview);
        btnShowReview = findViewById(R.id.btnOpenListReview);
        btnBack = findViewById(R.id.btnBack);

        idService = getIntent().getIntExtra("id", 0);

        // click trở lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finishActivity(1);
            }
        });

        getServiceInfo(Config.URL_GET_SERVICE_INFO.get(0) + idService + Config.URL_GET_SERVICE_INFO.get(1) + userId);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void getServiceInfo(String url) {
        txtServiceName = findViewById(R.id.textViewServiceName);
        txtServiceAbout = findViewById(R.id.textViewServiceAbout);
        txtPrice = findViewById(R.id.textViewCost);
        txtTime = findViewById(R.id.textViewTime);
        txtAddress = findViewById(R.id.textViewServiceAddress);
        txtPhoneNumber = findViewById(R.id.textViewServicePhone);
        txtWebsite = findViewById(R.id.textViewWebsite);
        imgThumbInfo1 = findViewById(R.id.imgInfo1);
        imgThumbInfo2 = findViewById(R.id.imgInfo2);
        imgBanner = findViewById(R.id.imgBanner);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        fbEvent = findViewById(R.id.fb_event);
        info = findViewById(R.id.info);
        txtMark = findViewById(R.id.textViewRatingMark);
        txtCountLike = findViewById(R.id.textViewLikeCount);
        rbStar = findViewById(R.id.ratingBarStars);

        imgThumbInfo1.setOnClickListener(this);
        imgThumbInfo2.setOnClickListener(this);

        ServiceInfo serviceInfo = new ModelService().getServiceInfo(Config.URL_HOST + url);

        idLike = serviceInfo.getIdLike();
        idRating = serviceInfo.getIdRating();
        longitude = serviceInfo.getLongitude();
        latitude = serviceInfo.getLatitude();

        fbEvent.setSelected(true);

        // region get tên và set màu cho từng dịch vụ
        if (serviceInfo.getEatName() != null) {
            txtServiceName.setText(serviceInfo.getEatName());
            toolbar.setBackgroundColor(getResources().getColor(R.color.tbEat));
            info.setBackgroundColor(getResources().getColor(R.color.tbEat));
            serviceType = 1;
            toolbarTitle.setText(getResources().getString(R.string.title_RestaurantDetails));
        } else if (serviceInfo.getHotelName() != null) {
            txtServiceName.setText(serviceInfo.getHotelName());
            toolbar.setBackgroundColor(getResources().getColor(R.color.tbHotel));
            info.setBackgroundColor(getResources().getColor(R.color.tbHotel));
            serviceType = 2;
            toolbarTitle.setText(getResources().getString(R.string.title_HotelDetails));
        } else if (serviceInfo.getPlaceName() != null) {
            txtServiceName.setText(serviceInfo.getPlaceName());
            toolbar.setBackgroundColor(getResources().getColor(R.color.tbPlace));
            info.setBackgroundColor(getResources().getColor(R.color.tbPlace));
            serviceType = 4;
            toolbarTitle.setText(getResources().getString(R.string.title_PlaceDetails));
        } else if (serviceInfo.getVehicleName() != null) {
            txtServiceName.setText(serviceInfo.getVehicleName());
            toolbar.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
            info.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
            serviceType = 3;
            toolbarTitle.setText(getResources().getString(R.string.title_TransportDetails));
        } else {
            txtServiceName.setText(serviceInfo.getEntertainName());
            toolbar.setBackgroundColor(getResources().getColor(R.color.tbEntertain));
            info.setBackgroundColor(getResources().getColor(R.color.tbEntertain));
            serviceType = 5;
            toolbarTitle.setText(getResources().getString(R.string.title_EntertainmentDetails));
        }
        // endregion

        // nếu không có sự kiện thì ẩn thẻ sự kiện
        if (serviceInfo.getEventType().equals(Config.NULL)) {
            fbEvent.setVisibility(TextView.GONE);
        } else {
            fbEvent.setVisibility(TextView.VISIBLE);
            fbEvent.setText(serviceInfo.getEventType());
        }

        // nếu đã thích thì đổi text thành unlike và đổi icon
        if (serviceInfo.getIsLike()) {
            btnLike.setText(getResources().getString(R.string.text_UnLike));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_36dp, 0, 0);
        } else {
            btnLike.setText(getResources().getString(R.string.text_Like));
            btnLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_border_36dp, 0, 0);
        }

        // nếu đã đánh giá thì đổi text thành reviewed
        if (serviceInfo.getIsRating()) {
            btnReview.setText(getResources().getString(R.string.text_Reviewed));
        } else {
            btnReview.setText(getResources().getString(R.string.text_Review));
        }

        // set text mô tả dịch vụ
        txtServiceAbout.setText(serviceInfo.getServiceAbout());

        // nếu giá thấp nhất và giá cao nhất = 0 thì settext updating
        if (serviceInfo.getLowestPrice().equals("0") && serviceInfo.getHighestPrice().equals("0")) {
            txtPrice.setText(getResources().getString(R.string.text_Updating));
        } else {
            txtPrice.setText(getResources().getString(R.string.text_From) + " " + serviceInfo.getLowestPrice() + " " + getResources().getString(R.string.text_To) + " " + serviceInfo.getHighestPrice());
        }

        // nếu giờ mở cửa và giờ đóng cửa = đang cập nhật thì settext updating
        if (serviceInfo.getTimeOpen().equals("Đang cập nhật") && serviceInfo.getTimeClose().equals("Đang cập nhật")) {
            txtTime.setText(getResources().getString(R.string.text_Updating));
        } else {
            txtTime.setText(getResources().getString(R.string.text_From) + " " + serviceInfo.getTimeOpen()
                    + " " + getResources().getString(R.string.text_To) + " " + serviceInfo.getTimeClose());
        }
        // set số lượt like
        txtCountLike.setText(serviceInfo.getCountLike() + "");
        // set địa chỉ
        txtAddress.setText(serviceInfo.getAddress());
        // set số điện thoại
        txtPhoneNumber.setText(serviceInfo.getPhoneNumber());
        // set website
        txtWebsite.setText(serviceInfo.getWebsite());
        // set hình
        imgBanner.setImageBitmap(serviceInfo.getBanner());
        imgThumbInfo1.setImageBitmap(serviceInfo.getThumbInfo1());
        imgThumbInfo2.setImageBitmap(serviceInfo.getThumbInfo2());
        // set số sao
        txtMark.setText(String.format("%.1f", serviceInfo.getReviewMark()));
        // set số ngôi sao
        rbStar.setRating(serviceInfo.getStars());

        // json yêu thích lưu vào thẻ nhớ
        try {
            saveJson = new JSONObject("{" + Config.POST_KEY_JSON_LIKE_SERVICE.get(0) + ":\"" + idService + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(1) + ":\"" + serviceInfo.getHotelName() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(2) + ":\"" + serviceInfo.getEntertainName() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(3) + ":\"" + serviceInfo.getVehicleName() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(4) + ":\"" + serviceInfo.getPlaceName() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(5) + ":\"" + serviceInfo.getEatName() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(6) + ":\"" + serviceInfo.getIdImage() + "\","
                    + Config.POST_KEY_JSON_LIKE_SERVICE.get(7) + ":\"" + serviceInfo.getImageName() + "\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
