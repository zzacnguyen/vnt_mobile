package com.example.zzacn.vnt_mobile.View.Favorite;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Adapter.HttpRequestAdapter;
import com.example.zzacn.vnt_mobile.Adapter.ListOfServiceAdapter;
import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Helper.JsonHelper;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.ModelFavorite;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;
import static com.facebook.FacebookSdk.getApplicationContext;


public class FavoriteFragment extends Fragment {
    ArrayList<String> finalArr = new ArrayList<>();
    TextView txtServiceName;
    ImageView imgServiceImage;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        txtServiceName = view.findViewById(R.id.textViewFavorite);
        imgServiceImage = view.findViewById(R.id.imageViewFavorite);

        File path = new File(Environment.getExternalStorageDirectory() + Config.FOLDER);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, Config.FILE_LIKE);

        recyclerView = view.findViewById(R.id.RecyclerView_FavoriteList);
        recyclerView.setHasFixedSize(true); //Tối ưu hóa dữ liệu, k bị ảnh hưởng bởi nội dung trong adapter

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getFavoriteList(file, userId);
        if (file.exists()) {
            try {
                JSONArray jsonFile = new JSONArray(JsonHelper.readJson(file));
                for (int i = 0; i < jsonFile.length(); i++) {
                    JSONObject jsonObject = new JSONObject("{" +
                            Config.POST_KEY_JSON_LIKE.get(0) + ":\"" + jsonFile.getJSONObject(i).getString("id") + "\"," +
                            Config.POST_KEY_JSON_LIKE.get(1) + ":\"" + userId + "\"}");
                    new HttpRequestAdapter.httpPost(jsonObject).execute(Config.URL_HOST + Config.URL_GET_ALL_FAVORITE);
                }
                file.delete();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    private void getFavoriteList(final File file, int id) {

        ArrayList<Service> favoriteList = new ModelFavorite().getFavoriteList(file, Config.URL_HOST +
                Config.URL_GET_ALL_FAVORITE + "/" + id);

        final ListOfServiceAdapter listOfServiceAdapter =
                new ListOfServiceAdapter(recyclerView, favoriteList, getApplicationContext());
        recyclerView.setAdapter(listOfServiceAdapter);
        listOfServiceAdapter.notifyDataSetChanged();

        //set load more listener for the RecyclerView adapter
        final ArrayList<Service> finalListService = favoriteList;
        try {
            finalArr = JsonHelper.parseJsonNoId(new JSONObject(new HttpRequestAdapter.httpGet().execute(Config.URL_HOST +
                    Config.URL_GET_ALL_FAVORITE + "/" + id).get()), Config.GET_KEY_JSON_LOAD);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        listOfServiceAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (finalListService.size() < Integer.parseInt(finalArr.get(2))) {
                    finalListService.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            listOfServiceAdapter.notifyItemInserted(finalListService.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalListService.remove(finalListService.size() - 1);
                            listOfServiceAdapter.notifyItemRemoved(finalListService.size());

                            ArrayList<Service> serviceArrayList = new ModelFavorite().
                                    getFavoriteList(file, finalArr.get(1));
                            finalListService.addAll(serviceArrayList);
                            try {
                                finalArr = JsonHelper.parseJsonNoId(new JSONObject
                                        (new HttpRequestAdapter.httpGet().execute(finalArr.get(1)).get()), Config.GET_KEY_JSON_LOAD);
                            } catch (JSONException | InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            listOfServiceAdapter.notifyDataSetChanged();
                            listOfServiceAdapter.setLoaded();
                        }
                    }, 1000);
                }
            }
        });
    }
}
