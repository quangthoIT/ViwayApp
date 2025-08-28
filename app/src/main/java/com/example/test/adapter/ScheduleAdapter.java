package com.example.test.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.response.ScheduleResponse;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleResponse> scheduleList;
    private String origin;
    private String destination;


    public ScheduleAdapter(List<ScheduleResponse> scheduleList, String origin, String destination) {
        this.scheduleList = scheduleList;
        this.origin = origin;
        this.destination = destination;
    }


    public static class ScheduleViewHolder extends RecyclerView.ViewHolder{
        public TextView thoiGian;
        public TextView benXe;
        public TextView diaChi;
        public ImageView icon, icDiemDi;

        public ScheduleViewHolder(View view) {
            super(view);

            thoiGian = view.findViewById(R.id.itemSchedule_tvTime);
            benXe = view.findViewById(R.id.itemSchedule_tvDiaDiem);
            diaChi = view.findViewById(R.id.itemSchedule_tvDiaChi);
            icDiemDi = view.findViewById(R.id.itemSchedule_icDiemDi);
            icon = view.findViewById(R.id.itemSchedule_icDot);
        }

    }

    @NonNull
    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull ScheduleAdapter.ScheduleViewHolder holder, int position){
        ScheduleResponse item = scheduleList.get(position);
        String tenBenXe = item.getBenXe();

        holder.thoiGian.setText(item.getThoiGian());
        holder.benXe.setText(item.getBenXe());
        holder.diaChi.setText(item.getDiaChi());

        // RESET trạng thái trước khi xử lý logic
        holder.icon.setVisibility(View.VISIBLE);
        holder.icDiemDi.setColorFilter(null);
        holder.icDiemDi.setImageResource(0);

        if (tenBenXe.equalsIgnoreCase(origin)) {
            holder.icDiemDi.setImageResource(R.drawable.ic_diemdi);
            holder.icDiemDi.setColorFilter(Color.RED);
        } else if (tenBenXe.equalsIgnoreCase(destination)) {
            holder.icDiemDi.setImageResource(R.drawable.ic_diemden);
            holder.icDiemDi.setColorFilter(Color.RED);
            holder.icon.setVisibility(View.INVISIBLE);
        } else {
            holder.icDiemDi.setImageResource(R.drawable.ic_diemden);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

}
