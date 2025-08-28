package com.example.test.ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.SeatStatusResponse;
import com.example.test.response.TicketResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectedSeatActivity extends AppCompatActivity {

    private TextView diemDiHeader, diemDenHeader, ngayDiHeader, gioDi, ngayDi, soLuongVe, giaTien;
    private Button btnNext;
    private ImageButton btnBack;
    private Button btnA1,btnA2,btnA3,btnA4,btnA5,btnA6,btnA7,btnA8,btnA9,btnA10,btnA11,btnA12,btnA13,btnA15,btnA16,btnA17;
    private Button btnB1,btnB2,btnB3,btnB4,btnB5,btnB6,btnB7,btnB8,btnB9,btnB10,btnB11,btnB12,btnB13,btnB14,btnB15,btnB16,btnB17;
    private Integer giaVe = 0, tongTien = 0, ticketId;
    private String token, ngayDiFormatted;
    private TicketResponse ticket;
    Map<String, Button> seatButtonsMap = new HashMap<>();
    Set<String> selectedSeats = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.select_seat), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        diemDiHeader = findViewById(R.id.chonGhe_diemDiHeader);
        diemDenHeader = findViewById(R.id.chonGhe_diemDenHeader);
        ngayDiHeader = findViewById(R.id.chonGhe_thoiGianHeader);
        gioDi = findViewById(R.id.chonGhe_gioLenXe);
        ngayDi = findViewById(R.id.chonGhe_ngayXuatBen);
        soLuongVe = findViewById(R.id.chonGhe_SoLuongGhe);
        giaTien = findViewById(R.id.chonGhe_TongTien);

        btnBack = findViewById(R.id.chonGhe_btnBack);
        btnNext = findViewById(R.id.chonGhe_btnTiepTuc);

        btnA1 = findViewById(R.id.btnA01);
        btnA2 = findViewById(R.id.btnA02);
        btnA3 = findViewById(R.id.btnA03);
        btnA4 = findViewById(R.id.btnA04);
        btnA5 = findViewById(R.id.btnA05);
        btnA6 = findViewById(R.id.btnA06);
        btnA7 = findViewById(R.id.btnA07);
        btnA8 = findViewById(R.id.btnA08);
        btnA9 = findViewById(R.id.btnA09);
        btnA10 = findViewById(R.id.btnA10);
        btnA11 = findViewById(R.id.btnA11);
        btnA12 = findViewById(R.id.btnA12);
        btnA13 = findViewById(R.id.btnA13);
        btnA15 = findViewById(R.id.btnA15);
        btnA16 = findViewById(R.id.btnA16);
        btnA17 = findViewById(R.id.btnA17);

        btnB1 = findViewById(R.id.btnB01);
        btnB2 = findViewById(R.id.btnB02);
        btnB3 = findViewById(R.id.btnB03);
        btnB4 = findViewById(R.id.btnB04);
        btnB5 = findViewById(R.id.btnB05);
        btnB6 = findViewById(R.id.btnB06);
        btnB7 = findViewById(R.id.btnB07);
        btnB8 = findViewById(R.id.btnB08);
        btnB9 = findViewById(R.id.btnB09);
        btnB10 = findViewById(R.id.btnB10);
        btnB11 = findViewById(R.id.btnB11);
        btnB12 = findViewById(R.id.btnB12);
        btnB13 = findViewById(R.id.btnB13);
        btnB14 = findViewById(R.id.btnB14);
        btnB15 = findViewById(R.id.btnB15);
        btnB16 = findViewById(R.id.btnB16);
        btnB17 = findViewById(R.id.btnB17);

        Intent intent = getIntent();
        if (intent != null) {
            ngayDiFormatted = intent.getStringExtra("ngayDiHeader");
            ticket = (TicketResponse) intent.getSerializableExtra("ticket");

            diemDiHeader.setText(ticket.getDiemDi());
            diemDenHeader.setText(ticket.getDiemDen());
            ngayDiHeader.setText(ngayDiFormatted);
            gioDi.setText(ticket.getGioDi());
            ngayDi.setText(ngayDiFormatted);
            giaVe = ticket.getGiaVe();
            ticketId = ticket.getTicketId();

        }

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        initSeatButtonsMap();
        fetchSeatStatus(ticketId,token);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnNext.setOnClickListener(v -> {

            if (selectedSeats == null || selectedSeats.isEmpty()) {
                NotifyDialogHelper.showNotifyDialog(
                        SelectedSeatActivity.this,
                        "Vui lòng chọn ít nhất một ghế trước khi tiếp tục!"
                );
                return;
            }

            Intent it = new Intent(SelectedSeatActivity.this, InforPaymentActivity.class);
            it.putExtra("ticket", ticket);
            it.putExtra("ngayDiHeader", ngayDiFormatted);
            ArrayList<String> selectedList = new ArrayList<>(selectedSeats);
            it.putStringArrayListExtra("selectedSeats", selectedList);
            startActivity(it);
        });

    }
//  --------------- Hiển thị danh sách trạng thái các ghế -----------------------------------
    private void fetchSeatStatus (Integer ticketId, String token) {
        String url = Config.BASE_URL+"/trip/seat-trip/"+ ticketId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(SelectedSeatActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        Set<String> bookedSeats = new HashSet<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            bookedSeats.add(jsonArray.getString(i));
                        }

                        List<SeatStatusResponse> seatStatusList = new ArrayList<>();
                        for (String seatId : seatButtonsMap.keySet()) {
                            String status = bookedSeats.contains(seatId) ? "booked" : "available";
                            seatStatusList.add(new SeatStatusResponse(seatId, status));
                        }

                        runOnUiThread(() -> updateSeatButtons(seatStatusList));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(SelectedSeatActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }


        });
    }
//  --------------- Cập nhật trạng thái ghế -------------------------------------------------
    private void updateSeatButtons(List<SeatStatusResponse> seats) {
        for (SeatStatusResponse seat : seats) {
            String id = seat.getSeatId();
            String status = seat.getStatus();

            Button seatButton = seatButtonsMap.get(id);

            if (seatButton != null) {
                if (status.equals("available")) {
                    seatButton.setBackgroundColor(Color.parseColor("#99FFFF"));
                    seatButton.setEnabled(true);

                    seatButton.setOnClickListener(v -> {
                        if (selectedSeats.contains(id)) {
                            seatButton.setBackgroundColor(Color.parseColor("#99FFFF"));
                            selectedSeats.remove(id);
                        } else {
                            seatButton.setBackgroundColor(Color.parseColor("#FFD700"));
                            selectedSeats.add(id);
                        }

                        int soLuong = selectedSeats.size();
                        tongTien = soLuong * giaVe;
                        soLuongVe.setText(TextUtils.join(", ", selectedSeats));
                        giaTien.setText(String.format("%,d VNĐ", tongTien));
                    });
                } else if (status.equals("booked")) {
                    seatButton.setBackgroundColor(Color.parseColor("#888888"));
                    seatButton.setTextColor(Color.WHITE);
                    seatButton.setEnabled(false);
                }
            }
        }
    }
//  --------------- Tạo danh sách ghế -------------------------------------------------------
    private void initSeatButtonsMap() {
        seatButtonsMap.put("A1", btnA1);
        seatButtonsMap.put("A2", btnA2);
        seatButtonsMap.put("A3", btnA3);
        seatButtonsMap.put("A4", btnA4);
        seatButtonsMap.put("A5", btnA5);
        seatButtonsMap.put("A6", btnA6);
        seatButtonsMap.put("A7", btnA7);
        seatButtonsMap.put("A8", btnA8);
        seatButtonsMap.put("A9", btnA9);
        seatButtonsMap.put("A10", btnA10);
        seatButtonsMap.put("A11", btnA11);
        seatButtonsMap.put("A12", btnA12);
        seatButtonsMap.put("A13", btnA13);
        seatButtonsMap.put("A15", btnA15);
        seatButtonsMap.put("A16", btnA16);
        seatButtonsMap.put("A17", btnA17);

        seatButtonsMap.put("B1", btnB1);
        seatButtonsMap.put("B2", btnB2);
        seatButtonsMap.put("B3", btnB3);
        seatButtonsMap.put("B4", btnB4);
        seatButtonsMap.put("B5", btnB5);
        seatButtonsMap.put("B6", btnB6);
        seatButtonsMap.put("B7", btnB7);
        seatButtonsMap.put("B8", btnB8);
        seatButtonsMap.put("B9", btnB9);
        seatButtonsMap.put("B10", btnB10);
        seatButtonsMap.put("B11", btnB11);
        seatButtonsMap.put("B12", btnB12);
        seatButtonsMap.put("B13", btnB13);
        seatButtonsMap.put("B14", btnB14);
        seatButtonsMap.put("B15", btnB15);
        seatButtonsMap.put("B16", btnB16);
        seatButtonsMap.put("B17", btnB17);
    }

}
