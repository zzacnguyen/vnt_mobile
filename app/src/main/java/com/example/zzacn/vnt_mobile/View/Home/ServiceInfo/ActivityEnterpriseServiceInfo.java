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

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.ModelService;
import com.example.zzacn.vnt_mobile.Model.Object.ServiceInfo;
import com.example.zzacn.vnt_mobile.R;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;

public class ActivityEnterpriseServiceInfo extends AppCompatActivity implements View.OnClickListener {
    final int RESULT_BANNER = 111,
            RESULT_INFO1 = 112,
            RESULT_INFO2 = 113;
    boolean isChangeBanner = false, isChangeThumb1 = false, isChangeThumb2 = false;
    Button btnShowReview;
    TextView tvServiceName, tvServiceAbout, toolbarTitle, fbEvent, txtMark, btnCancel, btnDone;
    EditText etAddress, etPhoneNumber, etWebsite, etLowestPrice, etHighestPrice, etTimeOpen, etTimeClose, etHotelStar;
    ImageView imgThumbInfo1, imgThumbInfo2, imgBanner;
    Toolbar toolbar;
    LinearLayout info, linearHotelStar;
    RatingBar rbStar;
    int idService, serviceType;
    String idLike, idRating, longitude, latitude;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private Bitmap banner, thumb1, thumb2;

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageView_Banner:
                PickImageFromGallery(RESULT_BANNER);
                break;
            case R.id.imageView_Detail1:
                PickImageFromGallery(RESULT_INFO1);
                break;

            case R.id.imageView_Detail2:
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
                    // region bắt lỗi nhập
                    if (!etLowestPrice.getText().toString().trim().matches("^[0-9]*$")) {
                        etLowestPrice.setError(getResources().getString(R.string.text_LowestPriceMustBeANumber));
                    } else if (!etHighestPrice.getText().toString().trim().matches("^[0-9]*$")) {
                        etHighestPrice.setError(getResources().getString(R.string.text_HighestPriceMustBeANumber));
                    } else if (Integer.parseInt(etLowestPrice.getText().toString())
                            > Integer.parseInt(etHighestPrice.getText().toString())) {
                        etLowestPrice.setError(getResources().getString(R.string.text_LowestPriceIsNotHigherThanHighestPrice));
                    } else if (tvServiceName.getText().toString().equals("")) {
                        tvServiceName.setError(getResources().getString(R.string.text_ServiceNameIsNotAllowedToBeEmpty));
                    } else if (etPhoneNumber.getText().toString().equals("")) {
                        etPhoneNumber.setError(getResources().getString(R.string.text_PhoneNumberIsNotAllowedToBeEmpty));
                    } else if (!etPhoneNumber.getText().toString().trim().matches("^\\+[0-9]{10,13}$")) {
                        etPhoneNumber.setError(getResources().getString(R.string.text_InvalidPhoneNumber));
                    } else if (etWebsite.getText().toString().equals("")) {
                        etWebsite.setError(getResources().getString(R.string.text_WebsiteIsNotAllowedToBeEmpty));
                    } else if (etHotelStar.getText().toString().equals("")
                            && etHotelStar.getVisibility() != View.GONE) {
                        etHotelStar.setError(getResources().getString(R.string.text_NumberStarIsNotAllowedToBeEmpty));
                    } else if (!etHotelStar.getText().toString().trim().matches("^[0-9]*$")) {
                        etHotelStar.setError(getResources().getString(R.string.text_NumberStarMustBeANumber));
                    } else if (tvServiceAbout.getText().toString().equals("")) {
                        tvServiceAbout.setError(getResources().getString(R.string.text_DescriptionIsNotAllowedToBeEmpty));
                    } // endregion
                    else {

                        boolean isPostTextSuccessfully = false, isPostImageSuccessfully = false;

                        // region post text
                        String name = "";
                        switch (serviceType) {
                            case 1: // loại hình ăn uống
                                name = Config.POST_KEY_JSON_SERVICE_EAT.get(0) + ":\"" + tvServiceName.getText().toString() + "\"";
                                break;
                            case 2: // khách sạn
                                name = Config.POST_KEY_JSON_SERVICE_HOTEL.get(0) + ":\"" + tvServiceName.getText().toString() + "\","
                                        + Config.POST_KEY_JSON_SERVICE_HOTEL.get(1) + ":\"" + etHotelStar.getText().toString() + "\"";
                                break;
                            case 3: // phương tiện di chuyển
                                name = Config.POST_KEY_JSON_SERVICE_TRANSPORT.get(0) + ":\"" + tvServiceName.getText().toString() + "\"";
                                break;
                            case 4: // tham quan
                                name = Config.POST_KEY_JSON_SERVICE_SIGHTSEEING.get(0) + ":\"" + tvServiceName.getText().toString() + "\"";
                                break;
                            case 5: // vui chơi giải trí
                                name = Config.POST_KEY_JSON_SERVICE_ENTERTAINMENTS.get(0) + ":\"" + tvServiceName.getText().toString() + "\"";
                                break;
                            default:
                                break;
                        }
                        try {
                            JSONObject jsonPost = new JSONObject("{"
                                    // mô tả
                                    + Config.POST_KEY_JSON_SERVICE.get(0) + ":\"" + tvServiceAbout.getText() + "\","
                                    // giờ mở cửa
                                    + Config.POST_KEY_JSON_SERVICE.get(1) + ":\"" + etTimeOpen.getText() + "\","
                                    // giờ đóng cửa
                                    + Config.POST_KEY_JSON_SERVICE.get(2) + ":\"" + etTimeClose.getText() + "\","
                                    // giá cao nhất
                                    + Config.POST_KEY_JSON_SERVICE.get(3) + ":\"" + etHighestPrice.getText() + "\","
                                    // giá thấp nhất
                                    + Config.POST_KEY_JSON_SERVICE.get(4) + ":\"" + etLowestPrice.getText() + "\","
                                    // sdt
                                    + Config.POST_KEY_JSON_SERVICE.get(5) + ":\"" + etPhoneNumber.getText() + "\","
                                    // loại hình
                                    + Config.POST_KEY_JSON_SERVICE.get(6) + ":\"" + serviceType + "\","
                                    // user id
                                    + Config.POST_KEY_JSON_SERVICE.get(7) + ":\"" + userId + "\","
                                    // website
                                    + Config.POST_KEY_JSON_SERVICE.get(8) + ":\"" + etWebsite.getText().toString() + "\","
                                    // tên dịch vụ
                                    + name + "}");

                            // url : localhost/doan3_canthotour/edit-services/services-id={id_service}&user-id={id_user}
                            String rs = new HttpRequestAdapter.httpPut(jsonPost).execute(Config.URL_HOST
                                    + Config.URL_PUT_SERVICE_INFO.get(0) + idService + Config.URL_PUT_SERVICE_INFO.get(1) + userId).get();

                            if (rs.equals("\"status:200\"")) {
                                isPostTextSuccessfully = true;
                            }
                        } catch (InterruptedException | ExecutionException | JSONException e) {
                            e.printStackTrace();
                        }
                        // endregion

                        // region post image
                        if (isChangeBanner && isChangeThumb1 && isChangeThumb2) {
                            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                            ByteArrayOutputStream ban = new ByteArrayOutputStream();
                            bitmapArrayList.get(0).compress(Bitmap.CompressFormat.JPEG, 80, ban);
                            ContentBody contentBanner = new ByteArrayBody(ban.toByteArray(), "a.jpg");

                            ByteArrayOutputStream de1 = new ByteArrayOutputStream();
                            bitmapArrayList.get(1).compress(Bitmap.CompressFormat.JPEG, 80, de1);
                            ContentBody contentDetails1 = new ByteArrayBody(de1.toByteArray(), "b.jpg");

                            ByteArrayOutputStream de2 = new ByteArrayOutputStream();
                            bitmapArrayList.get(2).compress(Bitmap.CompressFormat.JPEG, 80, de2);
                            ContentBody contentDetails2 = new ByteArrayBody(de2.toByteArray(), "c.jpg");

                            reqEntity.addPart("banner", contentBanner);
                            reqEntity.addPart("details1", contentDetails1);
                            reqEntity.addPart("details2", contentDetails2);
                            try {
                                // post hình lên
                                String response = new HttpRequestAdapter.httpPostImage(reqEntity).execute(Config.URL_HOST
                                        + Config.URL_POST_IMAGE + idService).get();
                                // nếu post thành công trả về "status:200"
                                if (response.equals("\"status:200\"")) {
                                    isPostImageSuccessfully = true;
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        // endregion

                        if (isPostTextSuccessfully && isPostImageSuccessfully) {
                            Toast.makeText(this, getResources().getString(R.string.text_EditSuccessfully), Toast.LENGTH_SHORT).show();
                            disbleWidget();
                            btnDone.setText(getResources().getString(R.string.text_Edit));
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.text_EditFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
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
                        if (banner != bitmap) {
                            isChangeBanner = true;
                        }
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
                        if (thumb1 != bitmap) {
                            isChangeThumb1 = true;
                        }
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
                        if (thumb2 != bitmap) {
                            isChangeThumb2 = true;
                        }
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
        tvServiceName = findViewById(R.id.textView_ServiceName);
        tvServiceAbout = findViewById(R.id.textView_ServiceAbout);
        etLowestPrice = findViewById(R.id.editText_ServiceLowestPrice);
        etHighestPrice = findViewById(R.id.editText_ServiceHighestPrice);
        etTimeOpen = findViewById(R.id.editText_TimeOpen);
        etTimeClose = findViewById(R.id.editText_TimeClose);
        etAddress = findViewById(R.id.editText_ServiceAddress);
        etPhoneNumber = findViewById(R.id.editText_ServicePhone);
        etWebsite = findViewById(R.id.editText_ServiceWebsite);
        etHotelStar = findViewById(R.id.editText_HotelStar);
        imgThumbInfo1 = findViewById(R.id.imageView_Detail1);
        imgThumbInfo2 = findViewById(R.id.imageView_Detail2);
        imgBanner = findViewById(R.id.imageView_Banner);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        fbEvent = findViewById(R.id.FloatingBar_Event);
        info = findViewById(R.id.Linear_Info);
        linearHotelStar = findViewById(R.id.hotelStar);
        txtMark = findViewById(R.id.textView_RatingMark);
        rbStar = findViewById(R.id.ratingBar_Stars);
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
                tvServiceName.setText(serviceInfo.getEatName());
                toolbar.setBackgroundColor(getResources().getColor(R.color.tbEat));
                info.setBackgroundColor(getResources().getColor(R.color.tbEat));
                serviceType = 1;
                toolbarTitle.setText(getResources().getString(R.string.title_RestaurantDetails));
            } else if (serviceInfo.getHotelName() != null) {
                tvServiceName.setText(serviceInfo.getHotelName());
                linearHotelStar.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tbHotel));
                info.setBackgroundColor(getResources().getColor(R.color.tbHotel));
                serviceType = 2;
                toolbarTitle.setText(getResources().getString(R.string.title_HotelDetails));
            } else if (serviceInfo.getPlaceName() != null) {
                tvServiceName.setText(serviceInfo.getPlaceName());
                toolbar.setBackgroundColor(getResources().getColor(R.color.tbPlace));
                info.setBackgroundColor(getResources().getColor(R.color.tbPlace));
                serviceType = 4;
                toolbarTitle.setText(getResources().getString(R.string.title_PlaceDetails));
            } else if (serviceInfo.getVehicleName() != null) {
                tvServiceName.setText(serviceInfo.getVehicleName());
                toolbar.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
                info.setBackgroundColor(getResources().getColor(R.color.tbVehicle));
                serviceType = 3;
                toolbarTitle.setText(getResources().getString(R.string.title_TransportDetails));
            } else {
                tvServiceName.setText(serviceInfo.getEntertainName());
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
            tvServiceAbout.setText(serviceInfo.getServiceAbout());
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
            imgBanner.setImageBitmap(banner = serviceInfo.getBanner());
            imgThumbInfo1.setImageBitmap(thumb1 = serviceInfo.getThumbInfo1());
            imgThumbInfo2.setImageBitmap(thumb2 = serviceInfo.getThumbInfo2());
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
        tvServiceName.setInputType(InputType.TYPE_CLASS_TEXT);
        tvServiceAbout.setInputType(InputType.TYPE_CLASS_TEXT);
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
        tvServiceName.setInputType(InputType.TYPE_NULL);
        tvServiceAbout.setInputType(InputType.TYPE_NULL);
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
