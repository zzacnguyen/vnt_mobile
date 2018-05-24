package com.example.zzacn.vnt_mobile.View.Home.ServiceInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.ServiceInfo;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.Login_Register.ActivityLogin;
import com.example.zzacn.vnt_mobile.View.Search.ActivityNearLocation;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class ActivityServiceInfo extends AppCompatActivity implements View.OnClickListener {
    String[] imgDetail;
    Button btnShare, btnLike, btnNear, btnReview, btnShowReview;
    ImageButton btnBack;
    TextView txtServiceName, txtServiceAbout, txtPrice, txtTime, txtAddress, txtPhoneNumber, txtWebsite,
            toolbarTitle, fbEvent, txtMark, txtCountLike, txtTranslate, txtHotelStar;
    ImageView imgThumbInfo1, imgThumbInfo2, imgBanner;
    Toolbar toolbar;
    LinearLayout info, hotelStar;
    RatingBar rbStar;
    int idService, serviceType;
    String idLike, idRating, longitude, latitude;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        Intent iDetail = new Intent(ActivityServiceInfo.this, ActivityFullImage.class);

        switch (view.getId()) {
            case R.id.image_Info1:
                try {
                    imgDetail = new HttpRequestAdapter.httpGet()
                            .execute(Config.URL_HOST + Config.URL_GET_LINK_DETAIL_1 + idService).get()
                            .replaceAll("\"", "")
                            .split("\\+");
                    iDetail.putExtra("img", imgDetail);
                    startActivity(iDetail);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.image_Info2:
                try {
                    imgDetail = new HttpRequestAdapter.httpGet()
                            .execute(Config.URL_HOST + Config.URL_GET_LINK_DETAIL_2 + idService).get()
                            .replaceAll("\"", "")
                            .split("\\+");
                    iDetail.putExtra("img", imgDetail);
                    startActivity(iDetail);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button_NearLocation:
                Intent intent = new Intent(ActivityServiceInfo.this, ActivityNearLocation.class);
                // truyền kinh độ vĩ độ loại dịch vụ qua cho form tìm kiếm lân cận
                intent.putExtra(Config.KEY_NEAR_LOCATION.get(0), longitude);
                intent.putExtra(Config.KEY_NEAR_LOCATION.get(1), latitude);
                intent.putExtra(Config.KEY_NEAR_LOCATION.get(2), serviceType);
                startActivity(intent);
                break;

            case R.id.button_Back:
                finish();
                break;

            case R.id.button_ShareService:
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(ActivityServiceInfo.this, getResources().getString(R.string.toast_ShareSuccessful), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(ActivityServiceInfo.this, getResources().getString(R.string.toast_ShareCancel), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(ActivityServiceInfo.this, getResources().getString(R.string.toast_ShareError), Toast.LENGTH_SHORT).show();
                    }
                });

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://vietnamtour.com/"))
                        .build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(linkContent);
                }
                break;

            case R.id.btnReview:
                if (userId == 0) {
                    Intent intentReview = new Intent(ActivityServiceInfo.this, ActivityLogin.class);
                    startActivityForResult(intentReview, 1);
                } else {
                    Intent intentReview = new Intent(ActivityServiceInfo.this, ActivityReview.class);
                    intentReview.putExtra("id", idService);
                    intentReview.putExtra("idRating", idRating);
                    startActivityForResult(intentReview, 1);
                }
                break;
            case R.id.btnOpenListReview:
                Intent intentListReview = new Intent(ActivityServiceInfo.this, ActivityReviewList.class);
                intentListReview.putExtra("id", idService);
                startActivity(intentListReview);
                break;
            case R.id.btnLike:
                if (userId == 0) {
                    Intent data = new Intent(ActivityServiceInfo.this, ActivityLogin.class);
                    startActivity(data);
                } else {
                    if (btnLike.getText().equals(getResources().getString(R.string.text_Like))) {
                        Toast.makeText(ActivityServiceInfo.this, getResources().getString(R.string.text_Liked),
                                Toast.LENGTH_SHORT).show();
                        btnLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_36dp, 0, 0);
                        btnLike.setText(getResources().getString(R.string.text_UnLike));
                        txtCountLike.setText(Integer.parseInt(txtCountLike.getText().toString()) + 1 + "");

                        try {
                            JSONObject jsonObject = new JSONObject("{"
                                    + Config.POST_KEY_JSON_LIKE.get(0) + ":\"" + idService + "\","
                                    + Config.POST_KEY_JSON_LIKE.get(1) + ":\"" + userId + "\"}");
                            idLike = new HttpRequestAdapter.httpPost(jsonObject)
                                    .execute(Config.URL_HOST + Config.URL_GET_ALL_FAVORITE).get();
                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityServiceInfo.this, getResources().getString(R.string.text_UnLiked), Toast.LENGTH_SHORT).show();
                        txtCountLike.setText(Integer.parseInt(txtCountLike.getText().toString()) - 1 + "");
                        btnLike.setText(getResources().getString(R.string.text_Like));
                        btnLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_border_36dp, 0, 0);

                        new HttpRequestAdapter.httpDelete().execute(Config.URL_HOST + Config.URL_GET_ALL_FAVORITE + "/" + idLike);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_service_info);

        btnShare = findViewById(R.id.button_ShareService);
        btnLike = findViewById(R.id.btnLike);
        btnNear = findViewById(R.id.button_NearLocation);
        btnReview = findViewById(R.id.btnReview);
        btnShowReview = findViewById(R.id.btnOpenListReview);
        btnBack = findViewById(R.id.button_Back);
        txtTranslate = findViewById(R.id.tvTranslate);

        idService = getIntent().getIntExtra("id", 0);

        //Init FB share content
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Gọi sự kiện click
        btnBack.setOnClickListener(this);
        btnNear.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnShowReview.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnReview.setOnClickListener(this);
        txtTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtTranslate.getText().equals(getResources().getString(R.string.text_TranslateToEnglish))) {
                    getServiceInfo("en");
                    txtTranslate.setText(getResources().getString(R.string.text_TranslateToVietnamese));
                } else {
                    getServiceInfo("vi");
                    txtTranslate.setText(getResources().getString(R.string.text_TranslateToEnglish));
                }
            }
        });

        getServiceInfo("vi");
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void getServiceInfo(String lang) {
        String url = Config.URL_GET_SERVICE_INFO.get(0) + idService + Config.URL_GET_SERVICE_INFO.get(1) + userId;
        txtServiceName = findViewById(R.id.textViewServiceName);
        txtServiceAbout = findViewById(R.id.textViewServiceAbout);
        txtPrice = findViewById(R.id.textViewCost);
        txtTime = findViewById(R.id.textViewTime);
        txtAddress = findViewById(R.id.textViewServiceAddress);
        txtPhoneNumber = findViewById(R.id.textViewServicePhone);
        txtWebsite = findViewById(R.id.textViewWebsite);
        imgThumbInfo1 = findViewById(R.id.image_Info1);
        imgThumbInfo2 = findViewById(R.id.image_Info2);
        imgBanner = findViewById(R.id.imgBanner);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        fbEvent = findViewById(R.id.fb_event);
        info = findViewById(R.id.info);
        txtMark = findViewById(R.id.textViewRatingMark);
        txtCountLike = findViewById(R.id.textViewLikeCount);
        rbStar = findViewById(R.id.ratingBarStars);
        txtHotelStar = findViewById(R.id.textViewHotelStar);
        hotelStar = findViewById(R.id.hotelStar);

        imgThumbInfo1.setOnClickListener(this);
        imgThumbInfo2.setOnClickListener(this);

        ServiceInfo serviceInfo = new ModelService().getServiceInfo(Config.URL_HOST + url, lang);

        if (serviceInfo != null) {
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

        } else {
            Toast.makeText(this, getResources().getString(R.string.text_Error), Toast.LENGTH_SHORT).show();
        }
    }

//    private void printKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.example.zzacn.vnt_mobile",
//                    PackageManager.GET_SIGNATURES);
//            ;
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            startActivity(getIntent());
            if (data.hasExtra("mess"))
                Toast.makeText(this, data.getStringExtra("mess"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnLike.setOnClickListener(null);
        btnNear.setOnClickListener(null);
        btnShare.setOnClickListener(null);
        btnReview.setOnClickListener(null);
        btnShowReview.setOnClickListener(null);
    }
}
