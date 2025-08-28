package com.example.test.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import com.example.test.CustomSpinner;
import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.ticket.TicketTripActivity;
import com.example.test.item.SliderItem;
import com.example.test.adapter.SliderAdapter;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private Button btnTimVe;
    private ImageButton btnHoanDoi;
    private TextView ngayDi, ngayVe, thuDi, thuVe ;
    private Spinner spinnerDiemDi, spinnerDiemDen, spinnerSoLuongVe;
    private int selectedDiemDiIndex = 0, selectedDiemDenIndex = 0, selectedSoLuongVeIndex = 0;
    private String selectedDiemDiItem = "";
    private Calendar calendarNgayDi, calendarNgayVe;
    private CheckBox checkBoxKhuHoi;
    CustomSpinner adapterDiemDi, adapterDiemDen, adapterSoLuongVe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //-------------- Set ngày tháng mặc định tiếng Việt------------------------
        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        //-----------------------------------------------------------------------------

        spinnerDiemDi = view.findViewById(R.id.spinnerDiemDi);
        spinnerDiemDen = view.findViewById(R.id.spinnerDiemDen);
        btnHoanDoi = view.findViewById(R.id.btnDoi);
        ngayDi = view.findViewById(R.id.ngayDi);
        ngayVe = view.findViewById(R.id.ngayVe);
        thuDi = view.findViewById(R.id.thuDi);
        thuVe = view.findViewById(R.id.thuVe);
        spinnerSoLuongVe = view.findViewById(R.id.soLuongVe);
        btnTimVe = view.findViewById(R.id.btnTimVe);
        checkBoxKhuHoi = view.findViewById(R.id.checkbox_khuHoi);
        viewPager2 = view.findViewById(R.id.viewPagerImageSlider);

        calendarNgayDi = Calendar.getInstance();
        calendarNgayVe = Calendar.getInstance();
        updateDateInview(ngayDi, thuDi,calendarNgayDi);
        updateDateInview(ngayVe, thuVe,calendarNgayVe);
        ngayVe.setEnabled(false);

        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.banner1));
        sliderItems.add(new SliderItem(R.drawable.banner3));
        sliderItems.add(new SliderItem(R.drawable.banner4));

        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });


        String[] diaDiemList = {"Hồ Chí Minh", "Bình Định", "Quảng Nam", "Khánh Hòa", "Phú Yên"};
        List<String> itemDiaDiemList = Arrays.asList(diaDiemList);
        String[] SoLuongVe = {"1", "2", "3", "4", "5", "6"};
        List<String> itemSoLuongVeList = Arrays.asList(SoLuongVe);

        adapterDiemDi = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout,
                itemDiaDiemList,
                "Điểm đi",
                selectedDiemDiIndex
        );

        adapterDiemDi.setShowSelectedText(true);
        spinnerDiemDi.setAdapter(adapterDiemDi);
        spinnerDiemDi.setDropDownVerticalOffset(130);
        spinnerDiemDi.setDropDownWidth(dpToPx(150));
        spinnerDiemDi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDiemDiIndex = position;
                adapterDiemDi.setSelectedIndex(position);
                adapterDiemDi.notifyDataSetChanged();
                selectedDiemDiItem = (String) parent.getItemAtPosition(position);
                adapterDiemDen.setDisabledItemValue(selectedDiemDiItem);
                adapterDiemDen.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapterDiemDen = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout_right,
                itemDiaDiemList,
                "Điểm đến",
                selectedDiemDenIndex
        );

        adapterDiemDen.setShowSelectedText(true);
        spinnerDiemDen.setAdapter(adapterDiemDen);
        spinnerDiemDen.setDropDownVerticalOffset(130);
        spinnerDiemDen.setDropDownHorizontalOffset(dpToPx(-40));
        spinnerDiemDen.setDropDownWidth(dpToPx(150));
        spinnerDiemDen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDestinationItem = (String) parent.getItemAtPosition(position);
                if (selectedDestinationItem.equals(selectedDiemDiItem)) {
                    spinnerDiemDen.setSelection(selectedDiemDenIndex, false);
                    return;
                }
                selectedDiemDenIndex = position;
                adapterDiemDen.setSelectedIndex(position);
                adapterDiemDen.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapterSoLuongVe = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout_soluongve,
                itemSoLuongVeList,
                "Số lượng vé",
                selectedSoLuongVeIndex
        );
        adapterSoLuongVe.setShowSelectedText(true);
        spinnerSoLuongVe.setAdapter(adapterSoLuongVe);
        spinnerSoLuongVe.setDropDownVerticalOffset(130);
        spinnerSoLuongVe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSoLuongVeIndex = position;
                adapterSoLuongVe.setSelectedIndex(position);
                adapterSoLuongVe.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnHoanDoi.setOnClickListener(v -> {
            int selectedDiemDiIndex1 = spinnerDiemDi.getSelectedItemPosition();
            int selectedDiemDenIndex1 = spinnerDiemDen.getSelectedItemPosition();

            spinnerDiemDi.setSelection(selectedDiemDenIndex1);
            spinnerDiemDen.setSelection(selectedDiemDiIndex1);
            adapterDiemDi.setSelectedIndex(selectedDiemDenIndex1);
            adapterDiemDen.setSelectedIndex(selectedDiemDiIndex1);
            adapterDiemDi.notifyDataSetChanged();
            adapterDiemDen.notifyDataSetChanged();
        });


        ngayDi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(ngayDi,thuDi, calendarNgayDi);
            }

        });

        checkBoxKhuHoi.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            ngayVe.setEnabled(isChecked);
        }));

        ngayVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(ngayVe,thuVe, calendarNgayVe);
            }
        });

        btnTimVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TicketTripActivity.class);
                String valueDiemDi = spinnerDiemDi.getSelectedItem().toString();
                String valueDiemDen = spinnerDiemDen.getSelectedItem().toString();
                Long valueNgayDi = calendarNgayDi.getTimeInMillis();
                String valueSoLuongVe = spinnerSoLuongVe.getSelectedItem().toString();
                intent.putExtra("DIEM_DI",valueDiemDi);
                intent.putExtra("DIEM_DEN",valueDiemDen);
                intent.putExtra("NGAY_DI",valueNgayDi);
                intent.putExtra("SO_LUONG_VE",valueSoLuongVe);

                if(checkBoxKhuHoi.isChecked()){
                    Long valueNgayVe = calendarNgayVe.getTimeInMillis();
                    intent.putExtra("NGAY_VE",valueNgayVe);
                }

                if (valueDiemDi.equals(valueDiemDen)) {
                    NotifyDialogHelper.showNotifyDialog(
                            getContext(),
                            "Điểm đi và điểm đến không được trùng nhau!"
                    );
                    return;
                }

                startActivity(intent);
            }
        });

        return view;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void showDatePickerDialog(TextView textViewDay,TextView textViewThu, Calendar currentCalender) {
        int day = currentCalender.get(Calendar.DAY_OF_MONTH);
        int month = currentCalender.get(Calendar.MONTH);
        int year = currentCalender.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentCalender.set(Calendar.YEAR, year);
                        currentCalender.set(Calendar.MONTH, month);
                        currentCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
                        String dayOfWeek = dayOfWeekFormat.format(currentCalender.getTime());
                        textViewThu.setText(dayOfWeek);
                        updateDateInview(textViewDay,textViewThu, currentCalender);
                    }
                },
                year,
                month,
                day
        );
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.add(Calendar.DAY_OF_MONTH, 60);// Giới hạn 60 ngày
        datePicker.setMaxDate(maxDateCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateDateInview(TextView textView,TextView textViewThu, Calendar currentCalender){
        String myForm = "dd/MM";
        String dayOfWeekFormat = "EEEE";
        Locale localeVN = new Locale("vi", "VN");

        SimpleDateFormat sdf = new SimpleDateFormat(myForm, localeVN);
        SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat(dayOfWeekFormat, localeVN);
        String dayOfWeek = sdfDayOfWeek.format(currentCalender.getTime());

        textView.setText(sdf.format(currentCalender.getTime()));
        textViewThu.setText(dayOfWeek);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

}
