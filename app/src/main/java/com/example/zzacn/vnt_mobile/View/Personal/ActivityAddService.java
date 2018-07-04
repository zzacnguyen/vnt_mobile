package com.example.zzacn.vnt_mobile.View.Personal;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.BuildConfig;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.MainActivity;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class ActivityAddService extends AppCompatActivity implements View.OnClickListener {

    //REQUEST Code
    static final int RESULT_BANNER = 111,
            RESULT_INFO1 = 112,
            RESULT_INFO2 = 113,
            REQUEST_TAKE_PHOTO = 114;
    Bitmap bmBanner, bmInfo1, bmInfo2;
    TextView txtOpenTime, txtCloseTime, btnSend, btnCancel;
    EditText etServiceName, etWebsite, etServicePhone, etServiceAbout, etLowestPrice, etHighestPrice, etNumberStar;
    ImageView imgBanner, imgInfo1, imgInfo2;
    ImageButton ibCamera;
    String mCurrentPhotoPath, idService;
    int type, idPlace;
    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addservice);

        txtOpenTime = findViewById(R.id.txtOpenTime);
        txtCloseTime = findViewById(R.id.txtCloseTime);
        imgBanner = findViewById(R.id.imgPickBanner);
        imgInfo1 = findViewById(R.id.imgPickInfo1);
        imgInfo2 = findViewById(R.id.imgPickInfo2);
        ibCamera = findViewById(R.id.ibCamera);
        btnSend = findViewById(R.id.btnConfirmService);
        btnCancel = findViewById(R.id.btnCancelService);
        etServiceName = findViewById(R.id.etServiceName);
        etWebsite = findViewById(R.id.editText_Website);
        etServicePhone = findViewById(R.id.etServicePhone);
        etServiceAbout = findViewById(R.id.etServiceAbout);
        etLowestPrice = findViewById(R.id.etLowestPrice);
        etHighestPrice = findViewById(R.id.etHighestPrice);
        etNumberStar = findViewById(R.id.etNumberOfStar);

        imgBanner.setOnClickListener(this);
        imgInfo1.setOnClickListener(this);
        imgInfo2.setOnClickListener(this);
        txtOpenTime.setOnClickListener(this);
        txtCloseTime.setOnClickListener(this);

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dispatchTakePictureIntent();
            }
        });

        type = getIntent().getIntExtra("type", 0);
        idPlace = getIntent().getIntExtra("id", 0);
        if (type == 2) {
            etNumberStar.setVisibility(View.VISIBLE);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // region bắt lỗi nhập
                if (etLowestPrice.getText().toString().equals("")) {
                    etLowestPrice.setError(getResources().getString(R.string.text_LowestPriceIsNotAllowedToBeEmpty));
                } else if (etHighestPrice.getText().toString().equals("")) {
                    etHighestPrice.setError(getResources().getString(R.string.text_HighestPriceIsNotAllowedToBeEmpty));
                } else if (Integer.parseInt(etLowestPrice.getText().toString())
                        > Integer.parseInt(etHighestPrice.getText().toString())) {
                    etLowestPrice.setError(getResources().getString(R.string.text_LowestPriceIsNotHigherThanHighestPrice));
                } else if (etServiceName.getText().toString().equals("")) {
                    etServiceName.setError(getResources().getString(R.string.text_ServiceNameIsNotAllowedToBeEmpty));
                } else if (etServicePhone.getText().toString().equals("")) {
                    etServicePhone.setError(getResources().getString(R.string.text_PhoneNumberIsNotAllowedToBeEmpty));
                } else if (etWebsite.getText().toString().equals("")) {
                    etWebsite.setError(getResources().getString(R.string.text_WebsiteIsNotAllowedToBeEmpty));
                } else if (etNumberStar.getText().toString().equals("") && etNumberStar.getVisibility() != View.GONE) {
                    etNumberStar.setError(getResources().getString(R.string.text_NumberStarIsNotAllowedToBeEmpty));
                } else if (etServiceAbout.getText().toString().equals("")) {
                    etServiceAbout.setError(getResources().getString(R.string.text_DescriptionIsNotAllowedToBeEmpty));
                } // endregion
                else {
                    // region post text
                    String name = "";
                    switch (type) {
                        case 1: // loại hình ăn uống
                            name = Config.POST_KEY_JSON_SERVICE_EAT.get(0) + ":\"" + etServiceName.getText().toString() + "\"";
                            break;
                        case 2: // khách sạn
                            name = Config.POST_KEY_JSON_SERVICE_HOTEL.get(0) + ":\"" + etServiceName.getText().toString() + "\","
                                    + Config.POST_KEY_JSON_SERVICE_HOTEL.get(1) + ":\"" + etNumberStar.getText().toString() + "\"";
                            break;
                        case 3: // phương tiện di chuyển
                            name = Config.POST_KEY_JSON_SERVICE_TRANSPORT.get(0) + ":\"" + etServiceName.getText().toString() + "\"";
                            break;
                        case 4: // tham quan
                            name = Config.POST_KEY_JSON_SERVICE_SIGHTSEEING.get(0) + ":\"" + etServiceName.getText().toString() + "\"";
                            break;
                        case 5: // vui chơi giải trí
                            name = Config.POST_KEY_JSON_SERVICE_ENTERTAINMENTS.get(0) + ":\"" + etServiceName.getText().toString() + "\"";
                            break;
                        default:
                            break;
                    }

                    Boolean isPostService = false, isPostImage = false;

                    try {
                        JSONObject jsonPost = new JSONObject("{"
                                // mô tả
                                + Config.POST_KEY_JSON_SERVICE.get(0) + ":\"" + etServiceAbout.getText().toString() + "\","
                                // giờ mở cửa
                                + Config.POST_KEY_JSON_SERVICE.get(1) + ":\"" + txtOpenTime.getText().toString() + "\","
                                // giờ đóng cửa
                                + Config.POST_KEY_JSON_SERVICE.get(2) + ":\"" + txtCloseTime.getText().toString() + "\","
                                // giá cao nhất
                                + Config.POST_KEY_JSON_SERVICE.get(3) + ":\"" + etHighestPrice.getText().toString() + "\","
                                // giá thấp nhất
                                + Config.POST_KEY_JSON_SERVICE.get(4) + ":\"" + etLowestPrice.getText().toString() + "\","
                                // sdt
                                + Config.POST_KEY_JSON_SERVICE.get(5) + ":\"" + etServicePhone.getText().toString() + "\","
                                // loại hình
                                + Config.POST_KEY_JSON_SERVICE.get(6) + ":\"" + type + "\","
                                // user id
                                + Config.POST_KEY_JSON_SERVICE.get(7) + ":\"" + userId + "\","
                                // website
                                + Config.POST_KEY_JSON_SERVICE.get(8) + ":\"" + etWebsite.getText().toString() + "\","
                                // tên dịch vụ
                                + name + "}");
                        // post dịch vụ lên trả về id dịch vụ đó
                        idService = new HttpRequestAdapter.httpPost(jsonPost)
                                .execute(Config.URL_HOST + Config.URL_POST_SERVICE + idPlace).get();

                        idService = idService.contains(":") ? idService.replaceAll("\"", "").split(":")[1] : ""; // lấy id dịch vụ trả về có dạng "id_service:..." bỏ dấu " và cắt chuỗi theo dấu : lấy số id phía sau

                        isPostService = !idService.equals("");

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                    // endregion

                    // region post image
                    bmBanner = ((BitmapDrawable) imgBanner.getDrawable()).getBitmap();
                    bmInfo1 = ((BitmapDrawable) imgInfo1.getDrawable()).getBitmap();
                    bmInfo2 = ((BitmapDrawable) imgInfo2.getDrawable()).getBitmap();
                    if (isPostService) { // nếu post thành công có id dịch vụ trả về
                        ByteArrayOutputStream ban = new ByteArrayOutputStream();
                        bmBanner.compress(Bitmap.CompressFormat.JPEG, 80, ban);
                        ContentBody contentBanner = new ByteArrayBody(ban.toByteArray(), "a.jpg");

                        ByteArrayOutputStream de1 = new ByteArrayOutputStream();
                        bmInfo1.compress(Bitmap.CompressFormat.JPEG, 80, de1);
                        ContentBody contentDetails1 = new ByteArrayBody(de1.toByteArray(), "b.jpg");

                        ByteArrayOutputStream de2 = new ByteArrayOutputStream();
                        bmInfo2.compress(Bitmap.CompressFormat.JPEG, 80, de2);
                        ContentBody contentDetails2 = new ByteArrayBody(de2.toByteArray(), "c.jpg");

                        reqEntity.addPart("banner", contentBanner);
                        reqEntity.addPart("details1", contentDetails1);
                        reqEntity.addPart("details2", contentDetails2);
                        try {
                            // post hình lên
                            String response = new HttpRequestAdapter.httpPostImage(reqEntity).execute(Config.URL_HOST
                                    + Config.URL_POST_IMAGE + idService).get();
                            // nếu post thành công trả về "status:200"

                            isPostImage = response.equals("\"status:200\"");

                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    // endregion

                    if (isPostService && isPostImage){
                        finish();
                        startActivity(new Intent(ActivityAddService.this, MainActivity.class));
                    } else {
                        if(!isPostService){
                            Toast.makeText(ActivityAddService.this, getResources()
                                        .getString(R.string.toast_AddServiceFailed), Toast.LENGTH_SHORT).show();
                        }else if (!isPostImage){
                            Toast.makeText(ActivityAddService.this, getResources()
                                    .getString(R.string.toast_UploadImageFailed), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) { //Custom sự kiện click

        final Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        switch (view.getId()) { //Bắt sự kiện click dựa trên id của giao diện, ko phải id của biến

            case R.id.imgPickBanner:
                PickImageFromGallery(RESULT_BANNER);
                break;

            case R.id.imgPickInfo1:
                PickImageFromGallery(RESULT_INFO1);
                break;

            case R.id.imgPickInfo2:
                PickImageFromGallery(RESULT_INFO2);
                break;

            case R.id.txtOpenTime: //Set sự kiện click cho textview
                TimePickerDialog openTimePickerDialog = new TimePickerDialog(ActivityAddService.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtOpenTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, true);

                openTimePickerDialog.show();
                break;

            case R.id.txtCloseTime:
                TimePickerDialog closeTimePickerDialog = new TimePickerDialog(ActivityAddService.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtCloseTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, true);

                closeTimePickerDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Lấy hình ảnh và đưa lên màn hình
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_BANNER:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgBanner.setImageBitmap(bitmap);
//                        bitmapArrayList.add(bitmap);
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
                        imgInfo1.setImageBitmap(bitmap);
//                        bitmapArrayList.add(bitmap);
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
                        imgInfo2.setImageBitmap(bitmap);
//                        bitmapArrayList.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);

                    // ScanFile so it will be appeared on Gallery
                    MediaScannerConnection.scanFile(ActivityAddService.this,
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                }
        }
    }

    private void PickImageFromGallery(int requestCode) { //Chọn 1 tấm hình từ thư viện
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình..."), requestCode);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}
