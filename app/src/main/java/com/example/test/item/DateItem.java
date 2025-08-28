package com.example.test.item;

import java.util.Calendar;
public class DateItem {

    private Calendar date;
    private boolean isSelected;

    public DateItem(Calendar date) {
        this.date = date;
        this.isSelected = false;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
