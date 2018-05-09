package com.example.zzacn.vnt_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.zzacn.vnt_mobile.Model.Object.NearLocation;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Home.ActivityServiceInfo;

import java.util.ArrayList;


public class NearLocationAdapter extends RecyclerView.Adapter<NearLocationAdapter.ViewHolder> {
    ArrayList<NearLocation> nearLocations;
    Context context;

    public NearLocationAdapter(ArrayList<NearLocation> nearLocations, Context context) {
        this.nearLocations = nearLocations;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //Khi gọi DiaDanhAdapter thì hàm này chạy đầu tiên
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.custom_nearlocation, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) { //Mỗi 1 lần chạy hàm này tương ứng với load 1 item trong recycler view
        NearLocation nearLocation = nearLocations.get(position);
        holder.txtTen.setText("Tên dịch vụ: " + nearLocation.getNearLocationName());
        holder.imgHinh.setImageBitmap(nearLocation.getNearLocationImage());
        holder.txtKhoangCach.setText("Khoảng cách: " + nearLocation.getNearLocationDistance() + "m");
        holder.cardView.setTag(nearLocation.getNearLocationId());

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

    public class ViewHolder extends RecyclerView.ViewHolder { //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
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
