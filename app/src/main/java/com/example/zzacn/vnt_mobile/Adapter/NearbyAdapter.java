package com.example.zzacn.vnt_mobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Model.Object.NearLocation;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Home.ServiceInfo.ActivityServiceInfo;

import java.util.ArrayList;


public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {
    private ArrayList<NearLocation> nearLocations;
    private Context context;

    public NearbyAdapter(ArrayList<NearLocation> nearLocations, Context context) {
        this.nearLocations = nearLocations;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Khi gọi DiaDanhAdapter thì hàm này chạy đầu tiên
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.custom_nearby, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //Mỗi 1 lần chạy hàm này tương ứng với load 1 item trong recycler view

        holder.txtTen.setText("Tên dịch vụ: " + nearLocations.get(position).getNearLocationName());
        holder.imgHinh.setImageBitmap(nearLocations.get(position).getNearLocationImage());
        holder.txtKhoangCach.setText("Khoảng cách: " + nearLocations.get(position).getNearLocationDistance() + "m");
        holder.cardView.setTag(nearLocations.get(position).getNearLocationId());

        holder.cardView.setOnClickListener(new View.OnClickListener() {  //Bắt sự kiện click vào 1 item cardview
            @Override
            public void onClick(View view) {
                Intent iPlaceInfo = new Intent(context, ActivityServiceInfo.class);
                iPlaceInfo.putExtra("id", (int) view.getTag());
                iPlaceInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iPlaceInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearLocations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder { //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
        TextView txtTen, txtKhoangCach;
        ImageView imgHinh;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTen = itemView.findViewById(R.id.textView_NearName);
            txtKhoangCach = itemView.findViewById(R.id.textView_Radius);
            imgHinh = itemView.findViewById(R.id.imageview_ViewNear);
            cardView = itemView.findViewById(R.id.cardView_Near);
        }
    }
}
