package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.response.SupportResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.SupportViewHolder> {

    private List<SupportResponse> supportList;

    public SupportAdapter(List<SupportResponse> supportList) {
        this.supportList = supportList;
    }

    public static class SupportViewHolder extends RecyclerView.ViewHolder{
        public TextView tieuDeYeuCau;
        public TextView trangThai;
        public TextView maYeuCau;
        public TextView thoiGianYeuCau;

        public SupportViewHolder(View itemView){
            super(itemView);
            tieuDeYeuCau = itemView.findViewById(R.id.feedback_tieuDe);
            trangThai = itemView.findViewById(R.id.feedback_trangThai);
            maYeuCau = itemView.findViewById(R.id.feedback_maYeuCau);
            thoiGianYeuCau = itemView.findViewById(R.id.feedback_thoiGian);
        }
    }

    @NonNull
    @Override
    public SupportAdapter.SupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new SupportViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SupportAdapter.SupportViewHolder holder, int position){
        SupportResponse currentSupport = supportList.get(position);
        String tieuDe = "Hỗ trợ: " + currentSupport.getFullName() + " - " + currentSupport.getPhone();

        String status = currentSupport.getTrangThai();
        if ("resolved".equalsIgnoreCase(status)) {
            holder.trangThai.setText("Đã xử lý");
        } else if ("unresolved".equalsIgnoreCase(status)) {
            holder.trangThai.setText("Chưa xử lý");
        }

        holder.tieuDeYeuCau.setText(tieuDe);
        holder.maYeuCau.setText(currentSupport.getMaYeuCau());
        String formattedTime;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(currentSupport.getThoiGianYeuCau());
            formattedTime = outputFormat.format(date);
        } catch (Exception e) {
            formattedTime = currentSupport.getThoiGianYeuCau();
        }

        holder.thoiGianYeuCau.setText(formattedTime);

    }
    @Override
    public int getItemCount() {
        return supportList.size();
    }


}
