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
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.TripSchedule.ActivityTripScheduleInfo;

import java.util.ArrayList;


public class ListOfTripScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private ArrayList<TripSchedule> tripSchedules;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public ListOfTripScheduleAdapter(RecyclerView recyclerView, Context context, ArrayList<TripSchedule> tripSchedules) {
        this.context = context;
        this.tripSchedules = tripSchedules;

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
        return tripSchedules.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_tripschedule, parent, false);
            return new ListOfTripScheduleAdapter.ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new ListOfTripScheduleAdapter.LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            final TripSchedule tripSchedule = tripSchedules.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txtTripName.setText(tripSchedule.getTripName());
            viewHolder.txtStartDate.setText(tripSchedule.getTripStartDate());
            viewHolder.txtEndDate.setText(tripSchedule.getTripEndDate());

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {  //Bắt sự kiện click vào 1 item cardview
                @Override
                public void onClick(View view) {
                    Intent iScheduleInfo = new Intent(context, ActivityTripScheduleInfo.class);
                    iScheduleInfo.putExtra("schedules", tripSchedule);
                    iScheduleInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iScheduleInfo);
                }
            });

        }else if (holder instanceof ListOfTripScheduleAdapter.LoadingViewHolder) {
            ListOfTripScheduleAdapter.LoadingViewHolder loadingViewHolder = (ListOfTripScheduleAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return tripSchedules.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTripName, txtStartDate, txtEndDate;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTripName = itemView.findViewById(R.id.textView_TripName);
            txtStartDate = itemView.findViewById(R.id.textView_TripStartDate);
            txtEndDate = itemView.findViewById(R.id.textView_TripEndDate);
            cardView = itemView.findViewById(R.id.cardViewTripSchedule);
        }
    }
}
