package com.example.test.ticket;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.DetailHistoryTicketResponse;
import com.example.test.response.TicketHistoryResponse;
import com.example.test.support.SupportDirectoryActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailHistoryActivity extends AppCompatActivity {

    private DetailHistoryTicketResponse detailHistoryTicketResponse;
    private ImageButton btnBack, btnHelp;
    private TextView fullname, phone, email, trangThai, tuyenXe,
                        ThoiGianKhoiHanh, SoLuongVe, ViTriGhe,
                        DiemLenXe, DiemXuongXe, giaVe, phiThanhToan, TongTien;
    private Integer tongtien, userId;
    private String token,ticketHistoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_historyticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Detail_hitoryTicket), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnHelp = findViewById(R.id.Detail_btnSetting);
        btnBack = findViewById(R.id.Detail_btnBack);
        phone = findViewById(R.id.Detail_tvSdt);
        fullname = findViewById(R.id.Detail_tvHoVaTen);
        email = findViewById(R.id.Detail_tvEmail);
        trangThai = findViewById(R.id.Detail_tvTrangThai);
        tuyenXe = findViewById(R.id.Detail_tvTuyenXe);
        ThoiGianKhoiHanh = findViewById(R.id.Detail_tvThoiGianDi);
        SoLuongVe = findViewById(R.id.Detail_tvSoLuongVe);
        ViTriGhe = findViewById(R.id.Detail_tvViTriGhe);
        DiemLenXe = findViewById(R.id.Detail_tvDiemLenXe);
        DiemXuongXe = findViewById(R.id.Detail_tvDiemXuongXe);
        giaVe = findViewById(R.id.Detail_tvGiaTien);
        phiThanhToan = findViewById(R.id.Detail_tvPhiThanhToan);
        TongTien = findViewById(R.id.Detail_tvTongTien);

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        userId = sharedPreferences.getInt("userId", -1);

        Intent intent = getIntent();
        if (intent != null){
            TicketHistoryResponse ticket = (TicketHistoryResponse) intent.getSerializableExtra("ticketHistory");
            ticketHistoryId = ticket.getMaVe();
        }

        getDetailHistoryTicket(token, ticketHistoryId);

        btnHelp.setOnClickListener(v -> {
            showDialogSupportBottom(v,ticketHistoryId);
        });
        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

//    ------------ Hiển thị chi tiết lịch sử vé ----------------------------------
    private void getDetailHistoryTicket (String token, String maVe) {
        String baseUrl = Config.BASE_URL + "/ticket/details/" + maVe;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(DetailHistoryActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);
                        JSONArray seatArray = obj.getJSONArray("seat_code");
                        List<String> seatList = new ArrayList<>();
                        for (int j = 0; j < seatArray.length(); j++) {
                            seatList.add(seatArray.getString(j));
                        }
                        String seatCodeStr = String.join(", ", seatList);


                        detailHistoryTicketResponse = new DetailHistoryTicketResponse(
                        obj.optString("fullname", "Trống"),
                        obj.optString("phone_number", "Trống"),
                        obj.optString("email", "Trống"),
                        obj.optString("status", "Trống"),
                        obj.optString("route", "Trống"),
                        obj.optInt("total_seat", 0),
                        seatCodeStr,
                        obj.optString("full_time", "Trống"),
                        obj.optString("pickUpPoint", "Trống"),
                        obj.optString("dropOffPoint", "Trống"),
                        obj.optInt("price", 0)
                        );

                        runOnUiThread(() -> updateDisplay(detailHistoryTicketResponse));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(DetailHistoryActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
//---------------- Cập nhật giao diện --------------------------------------------
    private void updateDisplay(DetailHistoryTicketResponse ticketResponse) {
        fullname.setText(ticketResponse.getFullname());
        phone.setText(ticketResponse.getPhone());
        email.setText(ticketResponse.getEmail());

        String status = ticketResponse.getStatus();
        if ("confirmed".equalsIgnoreCase(status)) {
            trangThai.setText("Đã thanh toán");
        } else if ("cancelled".equalsIgnoreCase(status)) {
            trangThai.setText("Đã hủy");
        } else {
            trangThai.setText("Không xác định");
        }

        tuyenXe.setText(ticketResponse.getRoute());
        SoLuongVe.setText(ticketResponse.getTotal_seat().toString());
        ViTriGhe.setText(ticketResponse.getSeat_code());
        ThoiGianKhoiHanh.setText(ticketResponse.getFull_time());
        DiemLenXe.setText(ticketResponse.getPickUpPoint());
        DiemXuongXe.setText(ticketResponse.getDropOffPoint());

        tongtien = ticketResponse.getTotal_seat() * ticketResponse.getPrice();
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String giaVeFormatted = formatter.format(tongtien) + " VNĐ";
        giaVe.setText(giaVeFormatted);
        phiThanhToan.setText("0đ");
        TongTien.setText(giaVeFormatted);

    }
//----------------- Hiển thị hộp thoại hỗ trợ ------------------------------------
    public void showDialogSupportBottom (View v , String maVe) {
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
