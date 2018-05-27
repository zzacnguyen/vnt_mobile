package com.example.zzacn.vnt_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Model.Object.Service;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Home.ServiceInfo.ActivityEnterpriseServiceInfo;

import java.util.ArrayList;


public class EnterpriseServiceAdapter extends RecyclerView.Adapter<EnterpriseServiceAdapter.ViewHolder> {
    private ArrayList<Service> services;
    private Context context;

    public EnterpriseServiceAdapter(ArrayList<Service> service, Context context) {
        this.services = service;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Khi gọi DiaDanhAdapter thì hàm này chạy đầu tiên
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.custom_enterprise_service_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //Mỗi 1 lần chạy hàm này tương ứng với load 1 item trong recycler view
        holder.txtName.setText(services.get(position).getName());
        holder.imgImageService.setImageBitmap(services.get(position).getImage());
        holder.ratingBar.setRating(services.get(position).getAverageScore());
        holder.tvTotalLike.setText(String.valueOf(services.get(position).getTotalLike()));
        holder.tvTotalRate.setText(String.valueOf(services.get(position).getTotalRate()));
        holder.cardView.setTag(services.get(position).getId());

        holder.cardView.setOnClickListener(new View.OnClickListener() {  //Bắt sự kiện click vào 1 item cardview
            @Override
            public void onClick(View view) {
                Intent iServiceInfo = new Intent(context, ActivityEnterpriseServiceInfo.class);
                iServiceInfo.putExtra("id", (int) view.getTag());
                iServiceInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iServiceInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder { //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
        TextView txtName, tvTotalLike, tvTotalRate;
        ImageView imgImageService;
        RatingBar ratingBar;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textview_ServiceName);
            imgImageService = itemView.findViewById(R.id.imageview_ServicePhoto);
            tvTotalLike = itemView.findViewById(R.id.tvTotalLike);
            tvTotalRate = itemView.findViewById(R.id.tvTotalRate);
            ratingBar = itemView.findViewById(R.id.ratingBar_Stars);
            cardView = itemView.findViewById(R.id.cardViewEnterprise);
        }
    }
}
