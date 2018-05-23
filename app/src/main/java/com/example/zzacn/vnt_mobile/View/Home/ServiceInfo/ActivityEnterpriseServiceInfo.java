package com.example.zzacn.vnt_mobile.View.Home.ServiceInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.ServiceInfo;
import com.example.zzacn.vnt_mobile.R;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class ActivityEnterpriseServiceInfo extends AppCompatActivity implements View.OnClickListener {
    final int RESULT_BANNER = 111,
            RESULT_INFO1 = 112,
            RESULT_INFO2 = 113;
    String[] imgDetail;
    Button btnShowReview;
    TextView txtServiceName, txtServiceAbout, toolbarTitle, fbEvent, txtMark, btnCancel, btnDone;
    EditText etAddress, etPhoneNumber, etWebsite, etLowestPrice, etHighestPrice, etTimeOpen, etTimeClose;
    ImageView imgThumbInfo1, imgThumbInfo2, imgBanner;
    Toolbar toolbar;
    LinearLayout info;
    RatingBar rbStar;
    int idService, serviceType;
    String idLike, idRating, longitude, latitude;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgBanner:
                PickImageFromGallery(RESULT_BANNER);
                break;
            case R.id.image_Info1:
                PickImageFromGallery(RESULT_INFO1);
                break;

            case R.id.image_Info2:
                PickImageFromGallery(RESULT_INFO2);
                break;

            case R.id.textView_Cancel:
                finish();
                break;

            case R.id.btnOpenListReview:
                Intent intentListReview = new Intent(ActivityEnterpriseServiceInfo.this, ActivityReviewList.class);
                intentListReview.putExtra("id", idService);
                startActivity(intentListReview);
                break;

            case R.id.textView_Done:
                if (btnDone.getText().equals(getResources().getString(R.string.text_Edit))) {
                    enableWidget();
                    btnDone.setText(getResources().getString(R.string.text_Done));
                } else {
//                    try {
//                        String rs = new HttpRequestAdapter.httpPut(new JSONObject()).execute(Config.URL_HOST).get();
//                        if (rs.equals("status:200")) {
//                            finish();
//                        } else {
//                            Toast.makeText(this, getResources().getString(R.string.text_EditFailed), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_BANNER:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgBanner.setImageBitmap(bitmap);
                        bitmapArrayList.add(bitmap); //Add vào arraylist
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case RESULT_INFO1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgThumbInfo1.setImageBitmap(bitmap);
                        bitmapArrayList.add(bitmap); //Add vào arraylist
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case RESULT_INFO2:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgThumbInfo2.setImageBitmap(bitmap);
                        bitmapArrayList.add(bitmap); //Add vào arraylist
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_service_info);
        getServiceInfo();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void getServiceInfo() {
        idService = getIntent().getIntExtra("id", 0);
        String url = Config.URL_GET_SERVICE_INFO.get(0) + idService + Config.URL_GET_SERVICE_INFO.get(1) + userId;
        txtServiceName = findViewById(R.id.tvServiceName);
        txtServiceAbout = findViewById(R.id.tvServiceAbout);
        etLowestPrice = findViewById(R.id.editText_ServiceLowestPrice);
        etHighestPrice = findViewById(R.id.editText_ServiceHighestPrice);
        etTimeOpen = findViewById(R.id.editText_TimeOpen);
        etTimeClose = findViewById(R.id.editText_TimeClose);
        etAddress = findViewById(R.id.editText_ServiceAddress);
        etPhoneNumber = findViewById(R.id.editText_ServicePhone);
        etWebsite = findViewById(R.id.editText_ServiceWebsite);
        imgThumbInfo1 = findViewById(R.id.image_Info1);
        imgThumbInfo2 = findViewById(R.id.image_Info2);
        imgBanner = findViewById(R.id.imgBanner);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        fbEvent = findViewById(R.id.fb_event);
        info = findViewById(R.id.info);
        txtMark = findViewById(R.id.textViewRatingMark);
        rbStar = findViewById(R.id.ratingBarStars);
        btnShowReview = findViewById(R.id.btnOpenListReview);
        btnCancel = findViewById(R.id.textView_Cancel);
        btnDone = findViewById(R.id.textView_Done);

        imgBanner.setOnClickListener(this);
        imgThumbInfo1.setOnClickListener(this);
        imgThumbInfo2.setOnClickListener(this);
        btnShowReview.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        disbleWidget();

        ServiceInfo serviceInfo = new ModelService().getServiceInfo(Config.URL_HOST + url, "vi");

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

            // set text mô tả dịch vụ
            txtServiceAbout.setText(serviceInfo.getServiceAbout());
            // set giá thấp nhất
            etLowestPrice.setText(serviceInfo.getLowestPrice());
            // set giá cao nhất
            etHighestPrice.setText(serviceInfo.getHighestPrice());
            // set giờ mở cửa
            etTimeOpen.setText(serviceInfo.getTimeOpen());
            // set giờ đóng cửa
            etTimeClose.setText(serviceInfo.getTimeClose());
            // set địa chỉ
            etAddress.setText(serviceInfo.getAddress());
            // set số điện thoại
            etPhoneNumber.setText(serviceInfo.getPhoneNumber());
            // set website
            etWebsite.setText(serviceInfo.getWebsite());
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

    private void PickImageFromGallery(int requestCode) { //Chọn 1 tấm hình từ thư viện
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình..."), requestCode);
    }

    private void enableWidget() {
        // mở khóa nhập
        txtServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        txtServiceAbout.setInputType(InputType.TYPE_CLASS_TEXT);
        etLowestPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        etHighestPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        etTimeOpen.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        etTimeClose.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        etAddress.setInputType(InputType.TYPE_CLASS_TEXT);
        etPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        etWebsite.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        imgBanner.setClickable(true);
        imgThumbInfo1.setClickable(true);
        imgThumbInfo2.setClickable(true);
    }

    private void disbleWidget() {
        // khóa nhập
        txtServiceName.setInputType(InputType.TYPE_NULL);
        txtServiceAbout.setInputType(InputType.TYPE_NULL);
        etLowestPrice.setInputType(InputType.TYPE_NULL);
        etHighestPrice.setInputType(InputType.TYPE_NULL);
        etTimeOpen.setInputType(InputType.TYPE_NULL);
        etTimeClose.setInputType(InputType.TYPE_NULL);
        etAddress.setInputType(InputType.TYPE_NULL);
        etPhoneNumber.setInputType(InputType.TYPE_NULL);
        etWebsite.setInputType(InputType.TYPE_NULL);
        imgBanner.setClickable(false);
        imgThumbInfo1.setClickable(false);
        imgThumbInfo2.setClickable(false);
    }
}
