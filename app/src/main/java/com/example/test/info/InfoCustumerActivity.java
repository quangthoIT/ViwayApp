package com.example.test.info;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.login_logout_forgotpass.InputPhoneActivity;
import com.example.test.response.InfoCustomerResponse;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoCustumerActivity extends AppCompatActivity {

    private InfoCustomerResponse infoCustomerResponse;
    private ImageButton btnBack;
    private ImageView imgUser;
    private Button btnUpdateInfo, btnLogout;
    private Integer userId;
    private TextView phone, fullname, email, sex, job, dateOfBirth;
    private String token, imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.info), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnUpdateInfo = findViewById(R.id.Info_btnCapNhatThongTin);
        btnLogout = findViewById(R.id.Info_btnDangXuat);
        btnBack = findViewById(R.id.Info_btnBack);
        imgUser = findViewById(R.id.Info_imgInfo);
        phone = findViewById(R.id.Info_tvPhone);
        fullname = findViewById(R.id.Info_tvFullName);
        email = findViewById(R.id.Info_tvEmail);
        sex = findViewById(R.id.Info_tvGioiTinh);
        job = findViewById(R.id.Info_tvNgheNghiep);
        dateOfBirth = findViewById(R.id.Info_tvNgaySinh);

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        userId = sharedPreferences.getInt("userId", -1);
        imgUrl = sharedPreferences.getString("imageUrl", "");

        if (imgUrl != null && !imgUrl.isEmpty()) {
            String fullImageUrl = Config.BASE_URL_IMAGE + imgUrl;

            GlideUrl glideUrl = new GlideUrl(fullImageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUser);
        }

//       -------- Lấy thông tin khách hàng ----------------------------
        getInfo(userId, token);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });

        btnUpdateInfo.setOnClickListener(v -> {
            Intent intent = new Intent(InfoCustumerActivity.this, EditInfoCustomerActivity.class);
            intent.putExtra("infoUser", infoCustomerResponse);
            startActivity(intent);
        });

    }

//    --------- Hàm lấy thông tin khách hàng ----------------------
    private void getInfo (Integer userId, String token) {
        String baseUrl = Config.BASE_URL + "/users/get-user/" + userId;

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
                        Toast.makeText(InfoCustumerActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);

                        int id = obj.optInt("id", -1);
                        String fullName = getSafeString(obj, "fullName");
                        String phone = getSafeString(obj, "phoneNumber");
                        String email = getSafeString(obj, "email");
                        String address = getSafeString(obj, "address");
                        String sex = getSafeString(obj, "sex");
                        String job = getSafeString(obj, "job");
                        String dateOfBirth = getSafeString(obj, "dateOfBirth");

                        infoCustomerResponse = new InfoCustomerResponse(id,fullName, phone, email, address, sex, job, dateOfBirth);

                        runOnUiThread(() -> updateDisplay(infoCustomerResponse));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(InfoCustumerActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

//    --------- Cập nhật giao diện khi có dữ liệu ---------------
    private void updateDisplay(InfoCustomerResponse infoCustomer) {
        fullname.setText(infoCustomer.getFullName());
        phone.setText(infoCustomer.getPhone());
        email.setText(infoCustomer.getEmail());
        sex.setText(infoCustomer.getSex());
        job.setText(infoCustomer.getJob());
        dateOfBirth.setText(infoCustomer.getDateOfBirth());
    }

//    ---------- Yêu cầu đăng xuất ------------------------------
    private void requestLogout (String token) {
        String baseUrl = Config.BASE_URL+ "/users/logout";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .post(RequestBody.create(new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(InfoCustumerActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(InfoCustumerActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        getSharedPreferences("VIWAY", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Intent it = new Intent(InfoCustumerActivity.this, InputPhoneActivity.class);
                        startActivity(it);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(InfoCustumerActivity.this, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

//    ----------- Kiểm tra dữ liệu khi nhận ----------------------
    private String getSafeString(JSONObject obj, String key) {
        if (obj.isNull(key)) return "Chưa cập nhật";
        String value = obj.optString(key, "Chưa cập nhật");
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return "Chưa cập nhật";
        }
        return value;
    }

//    ----------- Cảnh báo khi đăng xuất ------------------------
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.Logout_btnNo);
        Button btnYes = view.findViewById(R.id.Logout_btnYes);

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            requestLogout(token);
            dialog.dismiss();
        });
    }


}
