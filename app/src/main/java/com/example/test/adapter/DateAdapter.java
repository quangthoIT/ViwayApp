package com.example.test.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;

import com.example.test.R;
import com.example.test.item.DateItem;
import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private String token;
    private List<DateItem> dateItemList;
    private SimpleDateFormat dayOfWeekFormat;
    private SimpleDateFormat dateFormat;
    private OnDateClickListener onDateClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public DateAdapter(List<DateItem> dateItemList, String token) {
        this.dateItemList = dateItemList;
        this.token = token;
        this.dayOfWeekFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN"));
        this.dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
    }


    public static class DateViewHolder extends RecyclerView.ViewHolder {
        LinearLayout dateItemContainer;
        TextView dayOfWeekTextView;
        TextView dateNumberTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateItemContainer = itemView.findViewById(R.id.dateItemContainer);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeekTextView);
            dateNumberTextView = itemView.findViewById(R.id.dateNumberTextView);
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateItem dateItem = dateItemList.get(position);
        Calendar calendar = dateItem.getDate();

        String dayOfWeek = dayOfWeekFormat.format(calendar.getTime());
        switch (dayOfWeek.toLowerCase(Locale.ROOT)) {
            case "t.hai":
            case "mon":
                dayOfWeek = "Th 2";
                break;
            case "t.ba":
            case "tue":
                dayOfWeek = "Th 3";
                break;
            case "t.tư":
            case "wed":
                dayOfWeek = "Th 4";
                break;
            case "t.năm":
            case "thu":
                dayOfWeek = "Th 5";
                break;
            case "t.sáu":
            case "fri":
                dayOfWeek = "Th 6";
                break;
            case "t.bảy":
            case "sat":
                dayOfWeek = "Th 7";
                break;
            case "cn":
            case "sun":
                dayOfWeek = "CN";
                break;
            default:
                break;
        }


        holder.dayOfWeekTextView.setText(dayOfWeek);
        holder.dateNumberTextView.setText(dateFormat.format(calendar.getTime()));

        holder.dateItemContainer.setSelected(dateItem.isSelected());
        if (dateItem.isSelected()) {
           // holder.dateItemContainer.setBackgroundResource(R.drawable.date_item_isselected);
            holder.dayOfWeekTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.dateNumberTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        } else {
            //holder.dateItemContainer.setBackgroundResource(R.drawable.date_item_background);
            holder.dayOfWeekTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.date_text_default));
            holder.dateNumberTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.date_text_default));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onDateClickListener != null) {
                int oldSelectedPosition = selectedPosition;
                selectedPosition = position;

                if (oldSelectedPosition != RecyclerView.NO_POSITION && oldSelectedPosition != selectedPosition) {
                    if (oldSelectedPosition < dateItemList.size()) {
                        dateItemList.get(oldSelectedPosition).setSelected(false);
                    }
                    notifyItemChanged(oldSelectedPosition);
                }

                if (selectedPosition < dateItemList.size()) {
                    dateItemList.get(selectedPosition).setSelected(true);
                }
                notifyItemChanged(selectedPosition);

                if (selectedPosition < dateItemList.size()) {
                    onDateClickListener.onDateClick(dateItemList.get(selectedPosition), token);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateItemList.size();
    }

    public interface OnDateClickListener {
        void onDateClick(DateItem dateItem, String token);
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < dateItemList.size()) {
            if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition != position) {
                dateItemList.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = position;
            dateItemList.get(selectedPosition).setSelected(true);
            notifyItemChanged(selectedPosition);
        } else {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                dateItemList.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = RecyclerView.NO_POSITION;
        }
    }

}
