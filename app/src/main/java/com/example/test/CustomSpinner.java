package com.example.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomSpinner extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;
    private String displayText;
    private int selectedIndex = 0;
    private boolean showSelectedText = false;
    private int layoutResourceId;
    private String disabledItemValue = "";

    public CustomSpinner(@NonNull Context context, int resource, @NonNull List<String> items, String displayText, int selectedIndex) {
        super(context, resource, items);
        this.context = context;
        this.layoutResourceId = resource;
        this.items = items;
        this.displayText = displayText;
        this.selectedIndex = selectedIndex;
    }

    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    public void setShowSelectedText(boolean show) {
        this.showSelectedText = show;
    }

    public void setDisabledItemValue(String value) {
        this.disabledItemValue = value;
    }
    private View createCustomerView(int position,@Nullable View convertView, ViewGroup parent, boolean isDropDown){
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);
        }
        TextView textView = view.findViewById(R.id.spinnerText);
        ImageView icon = view.findViewById(R.id.icon_dropdown);
        String currentItem = items.get(position);

        if(isDropDown) {
            textView.setText(items.get(position));
            icon.setVisibility(View.GONE);
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(null, Typeface.NORMAL);
            view.setBackgroundColor(Color.WHITE);
            view.setEnabled(true);
            view.setAlpha(1.0f);

            if (currentItem.equals(disabledItemValue)) {
                textView.setTextColor(Color.GRAY);
                textView.setTypeface(null, Typeface.ITALIC);
                view.setBackgroundColor(Color.LTGRAY);
                view.setEnabled(false);
                view.setAlpha(0.5f);
            }
            else if(position == selectedIndex){
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(null, Typeface.BOLD);
                view.setBackgroundColor(Color.parseColor("#ee4d2d"));
            }
        } else  {
            if (showSelectedText) {
                textView.setText(items.get(selectedIndex));
                icon.setVisibility(View.GONE);
            } else {
                textView.setText(displayText);
                icon.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        return createCustomerView(position,convertView,  parent, false);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        return createCustomerView(position,convertView, parent, true);
    }
}
