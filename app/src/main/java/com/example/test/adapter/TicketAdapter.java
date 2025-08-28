package com.example.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.test.ScheduleActivity;
import com.example.test.response.TicketHistoryResponse;
import com.example.test.response.TicketResponse;
import com.example.test.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketTripHolder> {

    private List<TicketResponse> ticketResponseList;
    private OnTicketClickListener listener;

    public TicketAdapter(List<TicketResponse> ticketResponseList,  OnTicketClickListener listener){
        this.ticketResponseList = ticketResponseList;
        this.listener = listener;
    }
    public static class TicketTripHolder extends RecyclerView.ViewHolder{
        public TextView gioDi;
        public TextView gioDen;
        public TextView giaVe;
        public TextView loaiGhe;
        public TextView soLuongGhe;
        public TextView ticket_Diemdi;
        public TextView khoangCach;
        public TextView ticket_Diemden;
        public TextView lichTrinh;

        public TicketTripHolder(View itemView){
            super(itemView);

            gioDi = itemView.findViewById(R.id.ticket_gioDi);
            gioDen = itemView.findViewById(R.id.ticket_gioDen);
            giaVe = itemView.findViewById(R.id.ticket_giaVe);
            loaiGhe = itemView.findViewById(R.id.ticket_loaiGhe);
            soLuongGhe = itemView.findViewById(R.id.ticket_soLuongGhe);
            ticket_Diemdi = itemView.findViewById(R.id.ticket_diemDi);
            khoangCach = itemView.findViewById(R.id.ticket_khoangCach);
            ticket_Diemden = itemView.findViewById(R.id.ticket_diemDen);
            lichTrinh = itemView.findViewById(R.id.ticket_tvLichTrinh);
        }
    }

    @NonNull
    @Override
    public TicketAdapter.TicketTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketTripHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull TicketAdapter.TicketTripHolder holder, int position){
        TicketResponse currenTicket = ticketResponseList.get(position);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        Integer ticketId;
        String diemDi, diemDen;
        String giaVeFormatted = formatter.format(currenTicket.getGiaVe()) + " VNĐ";
        String soLuongGheFormatted = "Còn " + formatter.format(currenTicket.getSoLuongGhe()) + " chỗ";
        String khoangCachFormatted = "Khoảng cách " + formatter.format(currenTicket.getKhoangCach()) + " km - " + currenTicket.getThoiGianDi();

        ticketId = currenTicket.getTicketId();
        diemDi = currenTicket.getDiemDi();
        diemDen = currenTicket.getDiemDen();
        holder.gioDi.setText(currenTicket.getGioDi());
        holder.gioDen.setText(currenTicket.getGioDen());
        holder.giaVe.setText(giaVeFormatted);
        holder.loaiGhe.setText(currenTicket.getLoaiGhe());
        holder.soLuongGhe.setText(soLuongGheFormatted);
        holder.ticket_Diemdi.setText(currenTicket.getDiemDi());
        holder.khoangCach.setText(khoangCachFormatted);
        holder.ticket_Diemden.setText(currenTicket.getDiemDen());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTicketClick(currenTicket);
            }
        });

        holder.lichTrinh.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ScheduleActivity.class);
            intent.putExtra("ticketId", ticketId);
            intent.putExtra("diemDi", diemDi);
            intent.putExtra("diemDen", diemDen);
            context.startActivity(intent);
        });

    }
    @Override
    public int getItemCount() {
        return ticketResponseList.size();
    }

    public void updateList(List<TicketResponse> newList) {
        this.ticketResponseList = newList;
        notifyDataSetChanged();
    }

    public interface OnTicketClickListener {
        void onTicketClick(TicketResponse ticket);
    }

}
