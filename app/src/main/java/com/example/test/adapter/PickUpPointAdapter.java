package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.response.ScheduleResponse;

import java.util.List;

public class PickUpPointAdapter extends RecyclerView.Adapter<PickUpPointAdapter.PickUpPointViewHolder> {

    private List<ScheduleResponse> pickupPoints;
    private int selectedPosition = -1;

    public interface OnPickupPointSelectedListener {
        void onPickupPointSelected(ScheduleResponse selectedPoint);
    }
    private OnPickupPointSelectedListener listener;

    public PickUpPointAdapter(List<ScheduleResponse> pickupPoints, OnPickupPointSelectedListener listener) {
        this.pickupPoints = pickupPoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PickUpPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pickup_point, parent, false);
        return new PickUpPointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickUpPointViewHolder holder, int position) {
        ScheduleResponse point = pickupPoints.get(position);

        holder.tvTime.setText(point.getThoiGian());
        holder.tvStation.setText(point.getBenXe());
        holder.tvAddress.setText(point.getDiaChi());
        holder.radioButton.setChecked(position == selectedPosition);

        holder.itemLayout.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onPickupPointSelected(point);
        });
    }

    @Override
    public int getItemCount() {
        return pickupPoints.size();
    }

    public static class PickUpPointViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvStation, tvAddress;
        RadioButton radioButton;
        LinearLayout itemLayout;

        public PickUpPointViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.itemPickupPoint_tvTime);
            tvStation = itemView.findViewById(R.id.itemPickupPoint_tvStation);
            tvAddress = itemView.findViewById(R.id.itemPickupPoint_tvAddress);
            radioButton = itemView.findViewById(R.id.itemPickupPoint_check);
            itemLayout = itemView.findViewById(R.id.itemPickupPoint_selected);
        }
    }
}
