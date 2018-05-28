package com.example.zzacn.vnt_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zzacn.vnt_mobile.Model.Object.TripSchedule;
import com.example.zzacn.vnt_mobile.R;
import com.example.zzacn.vnt_mobile.View.Personal.TripSchedule.ActivityTripScheduleInfo;

import java.util.ArrayList;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private ArrayList<TripSchedule> tripSchedules;
    private Context context;

    public ScheduleAdapter(ArrayList<TripSchedule> tripSchedules, Context context) {
        this.tripSchedules = tripSchedules;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Khi gọi DiaDanhAdapter thì hàm này chạy đầu tiên
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.custom_main_tripschedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //Mỗi 1 lần chạy hàm này tương ứng với load 1 item trong recycler view
        final TripSchedule tripSchedule = tripSchedules.get(position);
        holder.txtTripName.setText(tripSchedule.getTripName());
        holder.txtStartDate.setText(tripSchedule.getTripStartDate());
        holder.txtEndDate.setText(tripSchedule.getTripEndDate());
        holder.cardView.setTag(tripSchedule.getTripID());

        holder.cardView.setOnClickListener(new View.OnClickListener() {  //Bắt sự kiện click vào 1 item cardview
            @Override
            public void onClick(View view) {
                Intent iScheduleInfo = new Intent(context, ActivityTripScheduleInfo.class);
                iScheduleInfo.putExtra("schedules", tripSchedule);
                iScheduleInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iScheduleInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripSchedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder { //ViewHolder chạy thứ 2, phần này giúp cho recycler view ko bị load lại dữ liệu khi thực hiện thao tác vuốt màn hình
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
