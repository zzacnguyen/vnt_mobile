package com.example.zzacn.vnt_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Home.ServiceInfo.ActivityServiceInfo;

import java.util.ArrayList;

public class ListOfServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private ArrayList<Service> services;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public ListOfServiceAdapter(RecyclerView recyclerView, ArrayList<Service> service, Context context) {
        this.context = context;
        this.services = service;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null)
                        onLoadMoreListener.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return services.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_location_list, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txtName.setText(services.get(position).getName());
            viewHolder.imgImage.setImageBitmap(services.get(position).getImage());
            viewHolder.cardView.setTag(services.get(position).getId());

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent iServiceInfo = new Intent(context, ActivityServiceInfo.class);
                    iServiceInfo.putExtra("id", (int) view.getTag());
                    iServiceInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iServiceInfo);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    //"Normal item" Viewholder
    static class ViewHolder extends RecyclerView.ViewHolder {
        //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
        TextView txtName;
        ImageView imgImage;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textview_ServiceName);
            imgImage = itemView.findViewById(R.id.image_ServicePhoto);
            cardView = itemView.findViewById(R.id.cardView_List);

        }
    }
}
