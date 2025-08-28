package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.response.NotifyResponse;
import com.example.test.R;

import java.util.List;
public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private List<NotifyResponse> notifyList;

    public NotifyAdapter(List<NotifyResponse> notifyList){
        this.notifyList = notifyList;
    }

    public static class NotifyViewHolder extends RecyclerView.ViewHolder{
        public TextView tieuDeThongBao;
        public TextView thoiGianThongBao;
        public TextView noiDungThongBao;

        public NotifyViewHolder(View itemView){
            super(itemView);
            tieuDeThongBao = itemView.findViewById(R.id.Notify_tieuDe);
            thoiGianThongBao = itemView.findViewById(R.id.Notify_thoiGian);
            noiDungThongBao = itemView.findViewById(R.id.Notify_noiDung);
        }
    }

    @NonNull
    @Override
    public NotifyAdapter.NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull NotifyAdapter.NotifyViewHolder holder, int position){
        NotifyResponse currentNotify = notifyList.get(position);

        holder.tieuDeThongBao.setText(currentNotify.getTieuDeThongBao());
        holder.thoiGianThongBao.setText(currentNotify.getThoiGianThongBao());
        holder.noiDungThongBao.setText(currentNotify.getNoiDungThongBao());

    }
    @Override
    public int getItemCount() {
        return notifyList.size();
    }

}
