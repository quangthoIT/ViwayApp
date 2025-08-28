package com.example.test.ticket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.CustomSpinner;
import com.example.test.R;
import com.example.test.adapter.DateAdapter;
import com.example.test.adapter.TicketAdapter;
import com.example.test.config.Config;
import com.example.test.item.DateItem;
import com.example.test.response.TicketResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketTripActivity extends AppCompatActivity implements DateAdapter.OnDateClickListener{

    private RecyclerView dateRecyclerView;
    private RecyclerView ticketRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateItem> dateList;
    private TicketAdapter ticketAdapter;
    private Integer selectedGiaVeIndex= 0, selectedLoaiGheIndex = 0, selectedGioDiIndex = 0;
    private String DiemDi, DiemDen, SoLuongVe, token, ngayDiFormatted, ngayVeFormatted;
    private Calendar calendarNgayDi,calendarNgayVe;
    private Long ngayDiMillis,ngayVeMillis;
    private TextView topDiemDi, topDiemDen,currentDateHeader;
    private LinearLayout textNoData;
    private Boolean isKhuHoi;
    private ImageButton btnBack;
    private Spinner spinnerGiaVe, spinnerLoaiGhe, spinnerGioDi;
    List<TicketResponse> orginalTicketList = new ArrayList<>();
    List<TicketResponse> filterTicketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_trip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ticket_Result), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        btnBack = findViewById(R.id.TicketResult_btnBack);
        currentDateHeader = findViewById(R.id.TicketResult_ThoiGianHeader);
        dateRecyclerView = findViewById(R.id.dateRecyclerView);
        topDiemDi = findViewById(R.id.TicketResult_DiemDiHeader);
        topDiemDen = findViewById(R.id.TicketResult_DiemDenHeader);
        textNoData = findViewById(R.id.TicketResult_noData);
        ticketRecyclerView = findViewById(R.id.item_Ticket);
        spinnerGiaVe = findViewById(R.id.spinnerGiaVe);
        spinnerLoaiGhe = findViewById(R.id.spinnerLoaiGhe);
        spinnerGioDi = findViewById(R.id.spinnerGioDi);

        Intent intent = getIntent();

        if(intent != null){
            DiemDi = intent.getStringExtra("DIEM_DI");
            DiemDen = intent.getStringExtra("DIEM_DEN");
            ngayDiMillis = intent.getLongExtra("NGAY_DI", -1L);
            ngayVeMillis = intent.getLongExtra("NGAY_VE", -1L);
            SoLuongVe = intent.getStringExtra("SO_LUONG_VE");

            if (ngayVeMillis != -1L) {
                isKhuHoi = true;
                calendarNgayVe = Calendar.getInstance();
                calendarNgayVe.setTimeInMillis(ngayVeMillis);
            } else {
                isKhuHoi = false;
            }

            if (ngayDiMillis != -1L) {
                calendarNgayDi = Calendar.getInstance();
                calendarNgayDi.setTimeInMillis(ngayDiMillis);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
                ngayDiFormatted = sdf.format(calendarNgayDi.getTime());
            }

            if (isKhuHoi && calendarNgayVe != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
                ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
            }

            topDiemDi.setText(DiemDi);
            topDiemDen.setText(DiemDen);
            currentDateHeader.setText(ngayDiFormatted);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        // ------------------ Hiển thị thanh ngày đi----------------------//
        dateList = new ArrayList<>();
        dateAdapter = new DateAdapter(dateList, token);
        dateAdapter.setOnDateClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TicketTripActivity.this, LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);

//-------------------------- Hiển thị vé xe tìm được ----------------------------
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(TicketTripActivity.this));

        List<TicketResponse> ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketList, ticket -> {
            Intent it = new Intent(TicketTripActivity.this, SelectedSeatActivity.class);
            it.putExtra("ticket", ticket);
            it.putExtra("ngayDiHeader", ngayDiFormatted);
            startActivity(it);
        });
        orginalTicketList = ticketList;
        filterTicketList = new ArrayList<>(ticketList);
        ticketRecyclerView.setAdapter(ticketAdapter);


        List<String> itemGiaVeList = Arrays.asList("Tất cả", "Dưới 200K", "200K - 500K", "Trên 500K");
        List<String> itemLoaiGheList = Arrays.asList("Tất cả", "Ghế", "Limousine", "Giường");
        List<String> itemGioDiList = Arrays.asList("Tất cả", "Buổi sáng", "Buổi chiều", "Buổi tối");

        setupCustomSpinner(spinnerGiaVe, itemGiaVeList, "Giá", selectedGiaVeIndex, pos -> selectedGiaVeIndex = pos);
        setupCustomSpinner(spinnerLoaiGhe, itemLoaiGheList, "Loại ghế", selectedLoaiGheIndex, pos -> selectedLoaiGheIndex = pos);
        setupCustomSpinner(spinnerGioDi, itemGioDiList, "Giờ", selectedGioDiIndex, pos -> selectedGioDiIndex = pos);

        // ----------------- Nút quay lại -------------------------//
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // ------------Gửi yêu cầu lên server--------------//
        updateDateDisplay();
        sendDataToServer(token);

    }

    private interface OnSpinnerItemSelected {
        void onItemSelected(int position);
    }

//    ----------- Custom spinner --------------------------
    private void setupCustomSpinner(
            Spinner spinner,
            List<String> items,
            String hint,
            int selectedIndex,
            OnSpinnerItemSelected listener
    ) {
        CustomSpinner adapter = new CustomSpinner(
                TicketTripActivity.this,
                R.layout.spinner_layout,
                items,
                hint,
                selectedIndex
        );
        adapter.setShowSelectedText(false);
        spinner.setAdapter(adapter);
        spinner.setDropDownVerticalOffset(130);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelected(position);
                adapter.setSelectedIndex(position);
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

//    ----------- Chọn ngày đi ----------------------------
    @Override
    public void onDateClick(DateItem dateItem, String token) {
        calendarNgayDi = dateItem.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        currentDateHeader.setText(sdf.format(calendarNgayDi.getTime()));
        sendDataToServer(token);
    }
//  ------------- Gửi dữ liệu lên server ------------------
    private void sendDataToServer(String token) {
        String baseUrl = Config.BASE_URL+"/trip/search-trips";
        OkHttpClient client = new OkHttpClient();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));
        String ngayDiFormatted = sdf.format(calendarNgayDi.getTime());
        ngayDiMillis = calendarNgayDi.getTimeInMillis();

        JSONObject data = new JSONObject();
        try{
            data.put("origin", DiemDi);
            data.put("destination", DiemDen);
            data.put("departure_date", ngayDiFormatted);

            if (isKhuHoi && calendarNgayVe != null) {
                String ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
                data.put("return_date", ngayVeFormatted);
                data.put("is_round_trip", true);
            } else {
                data.put("return_date", JSONObject.NULL);
                data.put("is_round_trip", false);
            }

            data.put("passengers", SoLuongVe);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );

        Log.d("Url", baseUrl);
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(TicketTripActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        List<TicketResponse> newTicketList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            TicketResponse ticket = new TicketResponse(
                                    obj.getInt("id"),
                                    obj.getString("time_start"),
                                    obj.getString("time_end"),
                                    obj.getInt("price"),
                                    obj.getString("vehicle_kind"),
                                    obj.getInt("available_seats"),
                                    obj.getString("start_location"),
                                    obj.getInt("distance"),
                                    obj.getString("estimated_duration"),
                                    obj.getString("end_location")
                            );
                            newTicketList.add(ticket);
                        }

                        runOnUiThread(() -> {
                            orginalTicketList.clear();
                            orginalTicketList.addAll(newTicketList);

                            if (newTicketList.isEmpty()) {
                                textNoData.setVisibility(View.VISIBLE);
                            } else {
                                textNoData.setVisibility(View.GONE);
                                applyFilter();
                            }

                            ticketAdapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(TicketTripActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() -> {
                        if (response.code() == 400) {
                            textNoData.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(TicketTripActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

//    ----------- Cập nhật giao diện ----------------------
    @SuppressLint("NotifyDataSetChanged")
    private void updateDateDisplay() {
        Calendar today = Calendar.getInstance();
        dateList.clear();

        for (int i = 0; i <= 60; i++) {
            Calendar date = (Calendar) today.clone();
            date.add(Calendar.DATE, i);
            dateList.add(new DateItem(date));
        }

        int positionToScrollTo = 0;

        if (calendarNgayDi != null) {
            for (int i = 0; i < dateList.size(); i++) {
                DateItem item = dateList.get(i);
                if (item.getDate().get(Calendar.YEAR) == calendarNgayDi.get(Calendar.YEAR) &&
                        item.getDate().get(Calendar.MONTH) == calendarNgayDi.get(Calendar.MONTH) &&
                        item.getDate().get(Calendar.DAY_OF_MONTH) == calendarNgayDi.get(Calendar.DAY_OF_MONTH))
                {
                    positionToScrollTo = i;
                    //item.setSelected(true);
                    break;
                }
            }
        } else {
            //dateList.get(0).setSelected(true);
            positionToScrollTo = 0;
        }

        dateAdapter.notifyDataSetChanged();
        dateAdapter.setSelectedPosition(positionToScrollTo);

        int finalScrollPosition = Math.max(0, positionToScrollTo - 2);
        dateRecyclerView.scrollToPosition(finalScrollPosition);
    }

//    ----------- Lọc vé ----------------------------------
    private void applyFilter() {
        String giaVeFilter = spinnerGiaVe.getSelectedItem().toString();
        String loaiGheFilter = spinnerLoaiGhe.getSelectedItem().toString();
        String gioDiFilter = spinnerGioDi.getSelectedItem().toString();

        filterTicketList.clear();

        for (TicketResponse ticket : orginalTicketList) {
            boolean matches = true;

            // Lọc giá vé
            int giaVe = ticket.getGiaVe();
            if (giaVeFilter.equals("Dưới 200K") && giaVe >= 200000) matches = false;
            if (giaVeFilter.equals("200K - 500K") && (giaVe < 200000 || giaVe > 500000)) matches = false;
            if (giaVeFilter.equals("Trên 500K") && giaVe <= 500000) matches = false;

            // Lọc loại ghế
            if (!loaiGheFilter.equals("Tất cả") && !ticket.getLoaiGhe().equalsIgnoreCase(loaiGheFilter)) {
                matches = false;
            }

            // Lọc giờ đi
            String gio = ticket.getGioDi();
            int hour = Integer.parseInt(gio.split(":")[0]);
            if (gioDiFilter.equals("Buổi sáng") && !(hour >= 5 && hour < 12)) matches = false;
            if (gioDiFilter.equals("Buổi chiều") && !(hour >= 12 && hour < 18)) matches = false;
            if (gioDiFilter.equals("Buổi tối") && !(hour >= 18 || hour < 5)) matches = false;

            if (matches) {
                filterTicketList.add(ticket);
            }
        }
        ticketAdapter.updateList(filterTicketList);
    }


}