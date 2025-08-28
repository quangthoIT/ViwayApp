package com.example.test.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.response.TicketHistoryResponse;
import com.example.test.support.SupportDirectoryActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketViewHolder> {

    private List<TicketHistoryResponse> tiketList;
    private OnItemClickListener listener;

    public TicketHistoryAdapter(List<TicketHistoryResponse> tiketList, OnItemClickListener listener) {
        this.tiketList = tiketList;
        this.listener = listener;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder{
        public TextView maVe;
        public TextView trangThai;
        public TextView tuyenDuongDi;
        public TextView tuyenDuongDen;
        public TextView viTriGhe;
        public TextView gioXuatBen;
        public ImageButton hoTroButton;

        public TicketViewHolder(View itemView) {
            super(itemView);

            maVe = itemView.findViewById(R.id.TicketHistory_maVe);
            trangThai = itemView.findViewById(R.id.TicketHistory_trangThai);
            tuyenDuongDi = itemView.findViewById(R.id.TicketHistory_DiemDi);
            tuyenDuongDen = itemView.findViewById(R.id.TicketHistory_DiemDen);
            viTriGhe = itemView.findViewById(R.id.TicketHistory_viTriGhe);
            gioXuatBen = itemView.findViewById(R.id.TicketHistory_gioXuatBen);
            hoTroButton = itemView.findViewById(R.id.TicketHistory_hoTro);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull TicketViewHolder holder, int position){
        TicketHistoryResponse currenTicket = tiketList.get(position);

        holder.maVe.setText(currenTicket.getMaVe());

        String status = currenTicket.getTrangThai();
        if ("confirmed".equalsIgnoreCase(status)) {
            holder.trangThai.setText("Đã thanh toán");
        } else if ("cancelled".equalsIgnoreCase(status)) {
            holder.trangThai.setText("Đã hủy");
        }

        holder.tuyenDuongDi.setText(currenTicket.getTuyenDuongDi());
        holder.tuyenDuongDen.setText(currenTicket.getTuyenDuongDen());
        holder.viTriGhe.setText(currenTicket.getViTriGhe());
        holder.gioXuatBen.setText(currenTicket.getGioXuatBen());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currenTicket);
            }
        });

        holder.hoTroButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
                showDialogSupportBottom(v, currenTicket.getMaVe());
           }
        });
    }
    @Override
    public int getItemCount() {
        return tiketList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TicketHistoryResponse ticket);
    }

    public void showDialogSupportBottom (View v ,String maVe) {
        Context context = v.getContext();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        View bottomSheetView = LayoutInflater.from(v.getContext())
                .inflate(R.layout.bottom_dialog_support, null);

        TextView code_historyTicket = bottomSheetView.findViewById(R.id.DialogSupport_tvMaVe);
        LinearLayout call = bottomSheetView.findViewById(R.id.DialogSupport_phone);
        LinearLayout swap = bottomSheetView.findViewById(R.id.DialogSupport_swap);
        LinearLayout support = bottomSheetView.findViewById(R.id.DialogSupport_support);

        code_historyTicket.setText(maVe);

        call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:0969069605"));

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                context.startActivity(intent);
            }
            bottomSheetDialog.dismiss();
        });

        swap.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        support.setOnClickListener(view ->{
            Intent intent = new Intent(context, SupportDirectoryActivity.class);
            context.startActivity(intent);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}
