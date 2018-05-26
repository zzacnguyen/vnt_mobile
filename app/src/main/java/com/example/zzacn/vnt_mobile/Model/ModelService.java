package com.example.zzacn.vnt_mobile.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Model.Object.Review;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.Model.Object.ServiceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJson;
import static com.example.zzacn.vnt_mobile.Helper.JsonHelper.parseJsonNoId;
import static com.example.zzacn.vnt_mobile.View.MainActivity.isStoragePermissionGranted;

public class ModelService {

    public static Bitmap setImage(String url, String folderName, String fileName) {
        Bitmap bitmap = null;
        if (isStoragePermissionGranted) {
            // nếu có trả về tên hình + id hình để đặt tên cho file + folder
            if (!folderName.equals(Config.NULL) && !fileName.equals(Config.NULL)) {
                File path = new File(Environment.getExternalStorageDirectory().toString() + Config.FOLDER + "/" + folderName);
                path.mkdirs();
                File file = new File(path, fileName);
                if (file.exists()) {
                    // nếu file đã tồn tại thì load lên
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(file.toString(), options);
                    if (bitmap == null) {
                        if (file.delete()) {
                            try {
                                bitmap = new HttpRequestAdapter.httpGetImage().execute(url).get();
                                FileOutputStream out = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            } catch (InterruptedException | ExecutionException | FileNotFoundException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    // nếu file không tồn tại thì tải hình về và lưu hình vào bộ nhớ
                    try {
                        bitmap = new HttpRequestAdapter.httpGetImage().execute(url).get();
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (InterruptedException | ExecutionException | IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                bitmap = new HttpRequestAdapter.httpGetImage().execute(url).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public ServiceInfo getServiceInfo(String url, String lang) {

        ArrayList<String> arrayServiceInfo;
        String stringNameOfTheEventType;
        ServiceInfo serviceInfo = new ServiceInfo();
        Boolean isLike, isRating;

        try {
            // get thông tin dịch vụ chuyển về dạng jsonarray
            String data = new HttpRequestAdapter.httpGet().execute(url).get();
            JSONObject jsonResult = new JSONArray(data).getJSONObject(0);

            // lây thông tin người dùng đã thích dịch vụ hay chưa
            isLike = jsonResult.getString(Config.KEY_SERVICE_INFO.get(0)).equals("1");

            // lây thông tin người dùng đã đánh giá dịch vụ hay chưa
            isRating = jsonResult.getString(Config.KEY_SERVICE_INFO.get(3)).equals("1");

            // lấy thông tin chi tiết dịch vụ chuyển vào array
            JSONArray jsonService = new JSONArray(jsonResult.getString(Config.KEY_SERVICE_INFO.get(6)));
            arrayServiceInfo = parseJson(jsonService.getJSONObject(0), Config.GET_KEY_JSON_SERVICE_INFO);
            if (lang.equals("en")) {
                arrayServiceInfo.set(7, yandexApiTranslate(arrayServiceInfo.get(7), lang));
            }

            // nếu type_event != null thì lấy tên loại hình sự kiện ngược lại cho = null
            if (jsonResult.getString(Config.KEY_SERVICE_INFO.get(7)).equals(Config.NULL)) {
                stringNameOfTheEventType = Config.NULL;
            } else {
                stringNameOfTheEventType = new JSONObject(jsonResult.get(Config.KEY_SERVICE_INFO.get(7)).toString())
                        .getString(Config.KEY_SERVICE_INFO.get(8));
            }

            // set id dịch vụ
            serviceInfo.setId(Integer.parseInt(arrayServiceInfo.get(0)));
            // set tên dịch vụ
            if (!arrayServiceInfo.get(1).equals(Config.NULL)) {
                serviceInfo.setHotelName(arrayServiceInfo.get(1));
            } else if (!arrayServiceInfo.get(2).equals(Config.NULL)) {
                serviceInfo.setEntertainName(arrayServiceInfo.get(2));
            } else if (!arrayServiceInfo.get(3).equals(Config.NULL)) {
                serviceInfo.setVehicleName(arrayServiceInfo.get(3));
            } else if (!arrayServiceInfo.get(4).equals(Config.NULL)) {
                serviceInfo.setPlaceName(arrayServiceInfo.get(4));
            } else if (!arrayServiceInfo.get(5).equals(Config.NULL)) {
                serviceInfo.setEatName(arrayServiceInfo.get(5));
            }
            // set website
            serviceInfo.setWebsite(arrayServiceInfo.get(6));
            // set giới thiệu
            serviceInfo.setServiceAbout(arrayServiceInfo.get(7));
            // set giờ mở cửa
            serviceInfo.setTimeOpen(arrayServiceInfo.get(8));
            // set giờ đóng cửa
            serviceInfo.setTimeClose(arrayServiceInfo.get(9));
            // set giá thấp nhất
            serviceInfo.setLowestPrice(arrayServiceInfo.get(10));
            // set giá cao nhất
            serviceInfo.setHighestPrice(arrayServiceInfo.get(11));
            // set địa chỉ
            serviceInfo.setAddress(arrayServiceInfo.get(12));
            // set số điện thoại
            serviceInfo.setPhoneNumber(arrayServiceInfo.get(13));
            // set đánh giá
            // nếu rating == null thì set số sao = 0;
            if (arrayServiceInfo.get(14).equals(Config.NULL)) {
                serviceInfo.setReviewMark((float) 0);
                serviceInfo.setStars(0);
            } else {
                serviceInfo.setReviewMark(Float.parseFloat(arrayServiceInfo.get(14)));
                serviceInfo.setStars(Float.parseFloat(arrayServiceInfo.get(14)));
            }
            // set kinh độ
            serviceInfo.setLongitude(arrayServiceInfo.get(15));
            // set vĩ độ
            serviceInfo.setLatitude(arrayServiceInfo.get(16));
            // set loại hình sự kiện
            serviceInfo.setEventType(stringNameOfTheEventType);

            if (isLike) {
                serviceInfo.setIsLike(true);
                // lấy id like
                JSONObject jsonIdLike = new JSONObject(jsonResult.getString(Config.KEY_SERVICE_INFO.get(1)));
                // getString like_id
                serviceInfo.setIdLike(jsonIdLike.getString(Config.KEY_SERVICE_INFO.get(2)));
            } else {
                serviceInfo.setIsLike(false);
                serviceInfo.setIdLike("0");
            }

            if (isRating) {
                serviceInfo.setIsRating(true);
                JSONObject jsonIdRating = new JSONObject(jsonResult.getString(Config.KEY_SERVICE_INFO.get(4)));
                serviceInfo.setIdRating(jsonIdRating.getString(Config.KEY_SERVICE_INFO.get(5)));
            } else {
                serviceInfo.setIsRating(false);
                serviceInfo.setIdRating("0");
            }

            // set số lượt like
            serviceInfo.setCountLike(Integer.parseInt(jsonResult.getString(Config.KEY_SERVICE_INFO.get(9))));

            // lấy thông tin hình gồm : "url + id + tên hình"
            // xóa dấu " bằng replaceAll
            // tách theo dấu + ra 3 phân tử truyền vào hàm setImage
            String[] urlImageThumb1 = null, urlImageThumb2 = null, urlImageBanner = null;
            try {
                urlImageThumb1 = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_LINK_THUMB_1 + serviceInfo.getId())
                        .get().replaceAll("\"", "").split("\\+");
                urlImageThumb2 = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_LINK_THUMB_2 + serviceInfo.getId())
                        .get().replaceAll("\"", "").split("\\+");
                urlImageBanner = new HttpRequestAdapter.httpGet().execute(Config.URL_HOST + Config.URL_GET_LINK_BANNER + serviceInfo.getId())
                        .get().replaceAll("\"", "").split("\\+");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // set hình 1
            if (urlImageThumb1 != null) {
                serviceInfo.setThumbInfo1(setImage(Config.URL_HOST + urlImageThumb1[0],
                        Config.FOLDER_THUMB1, urlImageThumb1[2]));
            }
            // set hình 2
            if (urlImageThumb2 != null) {
                serviceInfo.setThumbInfo2(setImage(Config.URL_HOST + urlImageThumb2[0],
                        Config.FOLDER_THUMB2, urlImageThumb2[2]));
            }
            // set hình banner
            if (urlImageBanner != null) {
                serviceInfo.setBanner(setImage(Config.URL_HOST + urlImageBanner[0], Config.FOLDER_BANNER, urlImageBanner[2]));
            }

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            serviceInfo = null;
        }
        return serviceInfo;
    }

    public ArrayList<Service> getServiceInMain(String url, ArrayList<String> formatJson) {

        ArrayList<String> arrayList;
        ArrayList<Service> services = new ArrayList<>();

        try {
            String rs = new HttpRequestAdapter.httpGet().execute(url).get();
            arrayList = parseJsonNoId(new JSONObject(rs), Config.GET_KEY_JSON_LOAD);
            JSONArray jsonArray = new JSONArray(arrayList.get(0));

            int limit = jsonArray.length() > 5 ? 5 : jsonArray.length();

            for (int i = 0; i < limit; i++) {

                Service service = new Service();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList.clear();
                arrayList = parseJson(jsonObject, formatJson);

                // nếu load dịch vụ của doanh nghiệp
                if (arrayList.size() > 4) {
                    //Set mã dịch vụ
                    service.setId(Integer.parseInt(arrayList.get(0)));
                    //Set tên dịch vụ tìm kiếm
                    service.setName(!arrayList.get(1).equals(Config.NULL) ? arrayList.get(1)
                            : !arrayList.get(2).equals(Config.NULL) ? arrayList.get(2)
                            : !arrayList.get(3).equals(Config.NULL) ? arrayList.get(3)
                            : !arrayList.get(4).equals(Config.NULL) ? arrayList.get(4)
                            : arrayList.get(5));
                    //Set số sao trung bình
                    if (arrayList.get(6).equals(Config.NULL)) {
                        service.setAverageScore(0);
                    } else {
                        service.setAverageScore(Float.parseFloat(arrayList.get(6)));
                    }
                    //Set tổng lượt like
                    if (arrayList.get(7).equals(Config.NULL)) {
                        service.setTotalLike(0);
                    } else {
                        service.setTotalLike(Integer.parseInt(arrayList.get(7)));
                    }
                    //Set tổng lượt rate
                    if (arrayList.get(8).equals(Config.NULL)) {
                        service.setTotalRate(0);
                    } else {
                        service.setTotalRate(Integer.parseInt(arrayList.get(8)));
                    }
                    //Set hình ảnh
                    service.setImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(10),
                            Config.FOLDER_THUMB1, arrayList.get(10)));

                    services.add(service);
                } else {
                    // nếu load dịch vụ thường
                    service.setImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(3),
                            Config.FOLDER_THUMB1, arrayList.get(3)));
                    service.setId(Integer.parseInt(arrayList.get(0)));
                    service.setName(arrayList.get(1));

                    services.add(service);
                }
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return services;
    }

    public ArrayList<Service> getFullServiceList(String getJson, ArrayList<String> formatJson) {

        ArrayList<String> arrayList;
        ArrayList<Service> services = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                Service service = new Service();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList = parseJson(jsonObject, formatJson);
                service.setImage(setImage(Config.URL_HOST + Config.URL_GET_THUMB + arrayList.get(3),
                        Config.FOLDER_THUMB1, arrayList.get(3)));
                service.setId(Integer.parseInt(arrayList.get(0)));
                service.setName(arrayList.get(1));

                services.add(service);
                arrayList.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return services;
    }

    public ArrayList<Review> getReviewList(String getJson, ArrayList<String> formatJson) {

        ArrayList<String> arrayList;
        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(getJson);

            for (int i = 0; i < jsonArray.length(); i++) {

                Review review = new Review();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList = parseJsonNoId(jsonObject, formatJson);
                review.setUserName(arrayList.get(0));
                review.setStars(Float.parseFloat(arrayList.get(1)));
                review.setTitle(arrayList.get(2));
                review.setReview(arrayList.get(3));
                review.setDateReview(arrayList.get(4));

                reviews.add(review);
                arrayList.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private String yandexApiTranslate(String key, String lang) {
        String stringTranslated = null;
        if (Objects.equals(key, "")) {
            return null;
        } else {
            try {
                stringTranslated = new HttpRequestAdapter.httpGet().execute("https://translate.yandex.net/api/v1.5/tr.json/translate?text=" + key + "&lang="
                        + lang + "&key=trnsl.1.1.20170313T183850Z.f990548f90f1dae0.2f5d740510b7b7d9942413100d30b61aa74dfbfa").get();
                JSONObject obj = new JSONObject(stringTranslated);
                JSONArray result = (JSONArray) obj.get("text");

                stringTranslated = result.get(0).toString();
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
            return stringTranslated;
        }
    }
}
