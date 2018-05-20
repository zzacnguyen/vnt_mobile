package com.example.zzacn.vnt_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Config;
import com.example.zzacn.vnt_mobile.Interface.OnLoadMoreListener;
import com.example.zzacn.vnt_mobile.Model.Object.Event;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Home.ServiceInfo.ActivityServiceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.zzacn.vnt_mobile.View.Personal.FragmentPersonal.userId;


public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Context context;
    private ArrayList<Event> events;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public EventAdapter(RecyclerView recyclerView, ArrayList<Event> events, Context context) {
        this.context = context;
        this.events = events;

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
        return events.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_notify, parent, false);
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
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txtTenSk.setText(events.get(position).getEventName());
            viewHolder.txtNgaySk.setText(events.get(position).getEventDate());
            viewHolder.imgHinhSk.setImageBitmap(events.get(position).getEventImage());
            viewHolder.cardView.setTag(events.get(position).getEventId());
            if (events.get(position).isSeen())
                viewHolder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent iEventInfo = new Intent(context, ActivityServiceInfo.class);
                    iEventInfo.putExtra("id", (int) view.getTag());
                    try {
                        new HttpRequestAdapter.httpPost(new JSONObject("{"
                                + Config.POST_KEY_JSON_SEEN.get(0) + view.getTag()
                                + Config.POST_KEY_JSON_SEEN.get(0) + userId + "}"))
                                .execute(Config.URL_HOST + Config.URL_POST_SEEN_EVENT);
                        viewHolder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    iEventInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iEventInfo);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    //"Normal item" Viewholder
    static class ViewHolder extends RecyclerView.ViewHolder {
        //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
        TextView txtTenSk, txtNgaySk;
        ImageView imgHinhSk;
        View cardView;

        ViewHolder(View itemView) {
            super(itemView);

            txtTenSk = itemView.findViewById(R.id.textView_EventName);
            imgHinhSk = itemView.findViewById(R.id.image_ViewSuKien);
            txtNgaySk = itemView.findViewById(R.id.textView_EventDate);
            cardView = itemView.findViewById(R.id.cardView_SuKien);
        }
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}
