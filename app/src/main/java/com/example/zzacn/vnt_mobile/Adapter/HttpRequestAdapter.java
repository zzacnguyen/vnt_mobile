package com.example.zzacn.vnt_mobile.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestAdapter {
    private static HttpURLConnection urlConnection;

    public static class httpGet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            try {
                URL obj = new URL(strings[0]);
                urlConnection = (HttpURLConnection) obj.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }
    }

    public static class httpGetImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                input.close();
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    public static class httpPostImage extends AsyncTask<String, Void, String> {
        MultipartEntity reqEntity;

        public httpPostImage(MultipartEntity reqEntity) {
            this.reqEntity = reqEntity;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.addRequestProperty("Content-length", reqEntity.getContentLength() + "");
                urlConnection.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
                urlConnection.connect();

                OutputStream os = urlConnection.getOutputStream();
                reqEntity.writeTo(urlConnection.getOutputStream());
                os.flush();
                os.close();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    in.close();

                    return result.toString();
                } else {
                    return "failure";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    public static class httpPost extends AsyncTask<String, Void, String> {
        JSONObject json;

        public httpPost(JSONObject json) {
            this.json = json;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL obj = new URL(strings[0]);
                urlConnection = (HttpURLConnection) obj.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();
                BufferedWriter bw =
                        new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                bw.write(json.toString());
                bw.flush();
                bw.close();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) { //success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    in.close();

                    return result.toString();
                } else {
                    return "failure";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    public static class httpPut extends AsyncTask<String, Void, String> {
        JSONObject json;

        public httpPut(JSONObject json) {
            this.json = json;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL obj = new URL(strings[0]);
                urlConnection = (HttpURLConnection) obj.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("PUT");
                OutputStreamWriter streamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                streamWriter.write(json.toString());
                streamWriter.flush();
                streamWriter.close();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return "success";
                } else {
                    return "failure";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    public static class httpDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL obj = new URL(strings[0]);
                urlConnection = (HttpURLConnection) obj.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("DELETE");
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return "success";
                } else {
                    return "failure";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}
