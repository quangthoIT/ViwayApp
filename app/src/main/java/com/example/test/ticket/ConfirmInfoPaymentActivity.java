package com.example.test.ticket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.SheetPaymentBottom;
import com.example.test.config.Config;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmInfoPaymentActivity extends AppCompatActivity {

    private TextView diemDiHeader, diemDenHeader, thoiGianHeader,
            tuyenXe, thoiGianKhoiHanh, soLuongVe, viTriGhe, diemLenXe,
            thoiGianTrungChuyen, diemXuongXe, fullname, sdt, email, thoiGianGiuCho,
            giaVe, phiThanhToan, tongThanhToan;

    private Integer tongTien = 0, tripId, userId,codeTicket;
    private String ngaydi, token, diemDon;
    private Boolean trungChuyen = false;
    private ImageButton btnBack;
    private Button btnThanhToan;
    private TicketResponse ticket;
    private UserInfoResponse userInfo;
    private ArrayList<String> selectedSeats;
    private CountDownTimer countDownTimer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_info_payment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        diemDiHeader = findViewById(R.id.confirm_DiemDiHeader);
        diemDenHeader = findViewById(R.id.confirm_DiemDenHeader);
        thoiGianHeader = findViewById(R.id.confirm_ThoiGianHeader);
        tuyenXe = findViewById(R.id.confirm_tvTuyenXe);
        thoiGianKhoiHanh = findViewById(R.id.confirm_tvThoiGianDi);
        thoiGianTrungChuyen = findViewById(R.id.confirm_tvThoiGianTrungChuyen);
        soLuongVe = findViewById(R.id.confirm_tvSoLuongVe);
        viTriGhe = findViewById(R.id.confirm_tvViTriGhe);
        diemLenXe = findViewById(R.id.confirm_tvDiemLenXe);
        diemXuongXe = findViewById(R.id.confirm_tvDiemXuongXe);
        fullname = findViewById(R.id.confirm_tvHoVaTen);
        sdt = findViewById(R.id.confirm_tvSdt);
        email = findViewById(R.id.confirm_tvEmail);
        thoiGianGiuCho = findViewById(R.id.confirm_tvThoiGianGiuVe);
        giaVe = findViewById(R.id.confirm_tvGiaTien);
        phiThanhToan = findViewById(R.id.confirm_tvPhiThanhToan);
        tongThanhToan = findViewById(R.id.confirm_tvTongTien);

        btnBack = findViewById(R.id.confirm_btn_back);
        btnThanhToan = findViewById(R.id.confirm_btnThanhToan);


        Intent intent = getIntent();

        if (intent != null) {
            ticket = (TicketResponse) intent.getSerializableExtra("ticket");
            ngaydi = intent.getStringExtra("ngayDiHeader");
            diemDon = intent.getStringExtra("diemDon");
            trungChuyen = intent.getBooleanExtra("trungChuyen", false);
            selectedSeats = intent.getStringArrayListExtra("selectedSeats");
            userInfo = (UserInfoResponse) getIntent().getSerializableExtra("userInfo");



            if (ticket != null && ngaydi != null && userInfo != null) {
                tripId = ticket.getTicketId();

                diemDiHeader.setText(ticket.getDiemDi());
                diemDenHeader.setText(ticket.getDiemDen());
                thoiGianHeader.setText(ngaydi);
                tuyenXe.setText(ticket.getDiemDi()+ " - " + ticket.getDiemDen());
                thoiGianKhoiHanh.setText(ticket.getGioDi() +" "+ ngaydi);
                soLuongVe.setText(selectedSeats.size() + " Vé");
                viTriGhe.setText(TextUtils.join(", ", selectedSeats));
                diemLenXe.setText(diemDon);
                diemXuongXe.setText(ticket.getDiemDen());

                fullname.setText(userInfo.getFullname());
                sdt.setText(userInfo.getPhone());
                email.setText(userInfo.getEmail());

                tongTien = selectedSeats.size() * ticket.getGiaVe();

                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String giaVeFormatted = formatter.format(tongTien) + " VNĐ";

                giaVe.setText(giaVeFormatted);
                phiThanhToan.setText("0đ");
                tongThanhToan.setText(giaVeFormatted);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        sendBookingInfoToServer(token);
        startCountdownTimer();


        btnThanhToan.setOnClickListener(v -> {
            SheetPaymentBottom sheetPaymentBottom = new SheetPaymentBottom(method -> {
                sendBookingToServer(method, token);
            });

            sheetPaymentBottom.show(getSupportFragmentManager(), "PaymentSheet");
        });


        btnBack.setOnClickListener(v -> {
            showCancelDialog();
        });

    }
//    ---------------- Gửi thông tin thanh toán lên server --------------------
    private void sendBookingInfoToServer(String token) {
        String baseUrl = Config.BASE_URL+ "/ticket";

        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try {
            data.put("trip_id", tripId);
            data.put("user_id", userId);
            data.put("full_name", userInfo.getFullname());
            data.put("phone_number", userInfo.getPhone());
            data.put("email", userInfo.getEmail());
            data.put("seat_code", new JSONArray(selectedSeats));
            data.put("pick_up_point", diemDon);
            data.put("require_shuttle", trungChuyen);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );


        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ConfirmInfoPaymentActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String body = response.body().string();

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject result = new JSONObject(body);
                                int idResponse = result.getInt("id");
                                codeTicket = idResponse;
                            } catch (JSONException e) {
                                Toast.makeText(ConfirmInfoPaymentActivity.this, "Lỗi JSON từ server", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ConfirmInfoPaymentActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(ConfirmInfoPaymentActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }

        });
    }
//-------------------- Gửi phương thức thanh toán lên server ------------------
    private void sendBookingToServer(String method, String token) {
        String url = Config.BASE_URL + "/ticket/payment";

        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try {
            data.put("ticket_id", codeTicket);
            data.put("payment_method", method);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ConfirmInfoPaymentActivity.this, "Gửi phương thức thất bại", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject result = new JSONObject(body);
                            String paymentUrl = result.getString("paymentUrl");
                            openPaymentGateway(paymentUrl);
                        } catch (JSONException e) {
                            Toast.makeText(ConfirmInfoPaymentActivity.this, "Lỗi JSON từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ConfirmInfoPaymentActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
//    ---------------- Mở cổng thanh toán -------------------------------------
    private void openPaymentGateway(String paymentUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(i);
    }
//-------------------- Đếm ngược thời gian ------------------------------------
    private void startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(600000, 1000) { // 600000 ms = 10 phút
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                thoiGianGiuCho.setText("Thời gian giữ vé còn lại: "+ time);
            }

            @Override
            public void onFinish() {
                thoiGianGiuCho.setText("Thời gian giu vé còn lại: 00:00");
            }
        };

        countDownTimer.start();
    }
//-------------------- Cảnh báo khi quay lại sẽ hủy vé ------------------------
    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.Logout_btnNo);
        Button btnYes = view.findViewById(R.id.Logout_btnYes);
        TextView tvTitle = view.findViewById(R.id.Logout_tvTitle);
        TextView tvContent = view.findViewById(R.id.Logout_tvContent);

        tvTitle.setText("Bạn chắc chắn hủy?");
        tvContent.setText("Nếu hủy bạn sẽ không còn được giữ chổ nữa. Bạn có chắc chắn?");

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            cancelBooking(token, codeTicket);
            dialog.dismiss();
        });
    }
//-------------------- Hàm hủy vé ---------------------------------------------
    private void cancelBooking (String token, Integer codeTicket) {
        String baseUrl = Config.BASE_URL+ "/ticket/cancel/" + codeTicket;
        Log.d("Url cancel ticket: ", baseUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ConfirmInfoPaymentActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ConfirmInfoPaymentActivity.this, "Hủy vé thành công!", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(ConfirmInfoPaymentActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ConfirmInfoPaymentActivity.this, "Hủy vé thất bại!", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


}
