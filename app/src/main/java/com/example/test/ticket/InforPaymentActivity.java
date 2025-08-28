package com.example.test.ticket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.SheetPickUpPointBottom;
import com.example.test.utils.Validator;
import com.example.test.config.Config;
import com.example.test.response.ScheduleResponse;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import org.json.JSONArray;
import org.json.JSONException;
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

public class InforPaymentActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioBenXeVp, radioTrungChuyen;
    private EditText edtTrungChuyen;
    private LinearLayout showAddress;
    private ImageButton btnBack, btnEdit;
    private Button btnNext;
    private TextView diemDiHeader, diemDenHeader, ngayDiHeader,
            fullName, phone, email, gheDaChon,giaVe, loaiGhe, gioDi,
            gioDen, diemLenXe,khoangCach, diemXuongXe, benXeMacDinh, diaChiBenXe;

    private ImageView imgDiemDi, imgDiemDen, imgLineDown;
    private String ngaydi, viTriDonKhach, token;
    private Integer ticketId, userId;
    private Boolean trungChuyen = false;
    private TicketResponse ticket;
    private ArrayList<String> selectedSeats;
    private List<ScheduleResponse> pickUpPoinList;

    private UserInfoResponse userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.infor_payment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_thongTin_back);
        btnEdit = findViewById(R.id.btn_thonTinEdit);

        btnNext = findViewById(R.id.thongTin_btnNext);
        diemDenHeader = findViewById(R.id.thongTin_diemDen);
        diemDiHeader = findViewById(R.id.thongTin_diemDi);
        ngayDiHeader = findViewById(R.id.thongTin_thoiGianDi);
        fullName = findViewById(R.id.thongTin_tvHoVaTen);
        phone = findViewById(R.id.thongTin_tvSdt);
        email = findViewById(R.id.thongTin_tvEmail);
        gheDaChon = findViewById(R.id.thongTin_viTriGhe);
        giaVe = findViewById(R.id.thongTin_GiaVe);
        loaiGhe = findViewById(R.id.thongTin_LoaiGhe);
        gioDi = findViewById(R.id.thongTin_GioXuatPhat);
        gioDen = findViewById(R.id.thongTin_GioDen);
        diemLenXe = findViewById(R.id.thongTin_diemLenXe);
        khoangCach = findViewById(R.id.thongTin_khoangCach);
        diemXuongXe = findViewById(R.id.thongTin_diemXuongXe);
        diaChiBenXe = findViewById(R.id.thongTin_tvDiaChiBenXe);
        showAddress = findViewById(R.id.thongTin_showAddress);
        imgDiemDi = findViewById(R.id.img_diemDi);
        imgDiemDen = findViewById(R.id.img_diemDen);
        imgLineDown = findViewById(R.id.img_lineDown);

        radioGroup = findViewById(R.id.thongTin_radioGroup);
        radioBenXeVp = findViewById(R.id.thongTin_radioBenXe);
        radioTrungChuyen = findViewById(R.id.thongTin_radioTrungChuyen);
        edtTrungChuyen = findViewById(R.id.thongtin_edtTrungChuyen);
        benXeMacDinh = findViewById(R.id.thongTin_tvBenXeMacDinh);

        Intent intent = getIntent();

        if (intent != null) {
            ticket = (TicketResponse) intent.getSerializableExtra("ticket");
            ngaydi = intent.getStringExtra("ngayDiHeader");
            selectedSeats = intent.getStringArrayListExtra("selectedSeats");

            ticketId = ticket.getTicketId();

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String giaVeFormatted = formatter.format(ticket.getGiaVe()) + " VNĐ";
            String khoangCachFormatted = "Khoảng cách " + formatter.format(ticket.getKhoangCach()) + " km - " + ticket.getThoiGianDi();

            diemDiHeader.setText(ticket.getDiemDi());
            diemDenHeader.setText(ticket.getDiemDen());
            ngayDiHeader.setText(ngaydi);
            gioDi.setText(ticket.getGioDi());
            giaVe.setText(giaVeFormatted);
            loaiGhe.setText(ticket.getLoaiGhe());
            gioDen.setText(ticket.getGioDen());
            gioDi.setText(ticket.getGioDi());
            diemLenXe.setText(ticket.getDiemDi());
            khoangCach.setText(khoangCachFormatted);
            diemXuongXe.setText(ticket.getDiemDen());
            gheDaChon.setText(TextUtils.join(", ", selectedSeats));

            imgDiemDi.setBackgroundResource(R.drawable.ic_diemdi);
            imgDiemDen.setBackgroundResource(R.drawable.ic_diemden);
            imgLineDown.setBackgroundResource(R.drawable.ic_linedown);

        }

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");
        GetUserInfo(userId, token);

//  ------------------------ Hiển thị các điểm đón -------------------------------------
        getSupportFragmentManager().setFragmentResultListener("pickup_point_result", this, (requestKey, bundle) -> {
            if (requestKey.equals("pickup_point_result")) {
                ScheduleResponse selectedPoint = (ScheduleResponse) bundle.getSerializable("selected_pickup_point");
                benXeMacDinh.setText(selectedPoint.getBenXe());
                diaChiBenXe.setText(selectedPoint.getDiaChi());
            }
        });

        pickUpPoinList = new ArrayList<>();
        requestPickUpPoint(token, ticketId);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.thongTin_radioBenXe) {
                    edtTrungChuyen.setVisibility(View.GONE);
                    benXeMacDinh.setVisibility(View.VISIBLE);
                    diaChiBenXe.setVisibility(View.VISIBLE);
                } else if (radioTrungChuyen.isChecked()) {
                    edtTrungChuyen.setVisibility(View.VISIBLE);
                    benXeMacDinh.setVisibility(View.GONE);
                    diaChiBenXe.setVisibility(View.GONE);
                }
            }
        });
//  -------------- Hiển thị địa chỉ ----------------------------------------------------
        showAddress.setOnClickListener(v -> {
            if (radioBenXeVp.isChecked()) {
                SheetPickUpPointBottom sheet = SheetPickUpPointBottom.newInstance(new ArrayList<>(pickUpPoinList));
                sheet.setOnPickupPointSelectedListener(selectedPoint -> {
                    benXeMacDinh.setText(selectedPoint.getBenXe());
                    diaChiBenXe.setText(selectedPoint.getDiaChi());
                });
                sheet.show(getSupportFragmentManager(), "pickup_point_sheet");
            }
        });


        btnEdit.setOnClickListener(v -> {
            showEditDialog();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnNext.setOnClickListener(v -> {
            if (radioTrungChuyen.isChecked()) {
                viTriDonKhach = edtTrungChuyen.getText().toString().trim();
                trungChuyen = true;
            } else {
                viTriDonKhach = benXeMacDinh.getText().toString().trim();
                trungChuyen = false;
            }

            if (viTriDonKhach.isEmpty()) {
                NotifyDialogHelper.showNotifyDialog(
                        InforPaymentActivity.this,
                        "Vui lòng chọn vị trí đón khách trước khi tiếp tục!"
                );
                return;
            }

            Intent it = new Intent(InforPaymentActivity.this, ConfirmInfoPaymentActivity.class);
            it.putExtra("ticket", ticket);
            it.putExtra("ngayDiHeader", ngaydi);
            it.putExtra("diemDon", viTriDonKhach);
            it.putExtra("trungChuyen", trungChuyen);
            ArrayList<String> selectedList = new ArrayList<>(selectedSeats);
            it.putStringArrayListExtra("selectedSeats", selectedList);
            it.putExtra("userInfo", userInfo);

            startActivity(it);

        });

    }

//    -------------------- Lấy thông tin khách hàng ---------------------------
    private void GetUserInfo(Integer userId, String token) {
        String baseUrl = Config.BASE_URL+ "/users/get-info/" + userId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(InforPaymentActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);

                        String fullname = obj.getString("fullname");
                        String phone = obj.getString("phone_number");
                        String email = obj.getString("email");

                        userInfo = new UserInfoResponse(fullname, phone, email);

                        runOnUiThread(() -> updateDisplay(userInfo));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(InforPaymentActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
//  ---------------------- Lấy danh sách các điểm đón -------------------------
    private void requestPickUpPoint(String token, Integer tripId) {
        String baseUrl = Config.BASE_URL+"/route-point/trip/" + tripId;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(InforPaymentActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        List<ScheduleResponse> newScheduleList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            ScheduleResponse schedule = new ScheduleResponse(
                                    obj.optInt("id", 0),
                                    obj.optString("location", "Trống"),
                                    obj.optString("address", "Trống"),
                                    obj.optString("estimatedTime", "Trống")
                            );
                            newScheduleList.add(schedule);
                        }

                        runOnUiThread(() -> {
                            pickUpPoinList.clear();
                            pickUpPoinList.addAll(newScheduleList);
                            if (pickUpPoinList != null && !pickUpPoinList.isEmpty()) {
                                ScheduleResponse firstPoint = pickUpPoinList.get(0);
                                benXeMacDinh.setText(firstPoint.getBenXe());
                                diaChiBenXe.setText(firstPoint.getDiaChi());
                                viTriDonKhach = firstPoint.getBenXe();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(InforPaymentActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(InforPaymentActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
//  ---------------------- Cập nhật giao diện ---------------------------------
    private void updateDisplay(UserInfoResponse userInfo) {
        fullName.setText(userInfo.getFullname());
        phone.setText(userInfo.getPhone());
        email.setText(userInfo.getEmail());
    }
//  ---------------------- Hiển thị hộp thoại chỉnh sửa thông tin ------------
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtFullname = view.findViewById(R.id.edtFullname);
        EditText edtPhone = view.findViewById(R.id.edtPhone);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Gán sẵn thông tin cũ vào EditText
        edtFullname.setText(fullName.getText().toString());
        edtPhone.setText(phone.getText().toString());
        edtEmail.setText(email.getText().toString());

        btnSave.setOnClickListener(v -> {
            String newFullname = edtFullname.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();

            if (!newPhone.isEmpty() && !Validator.isPhoneNumber(newPhone)) {
                edtPhone.setError("Số điện thoại không hợp lệ");
                edtPhone.requestFocus();
                return;
            }

            if (!newEmail.isEmpty() && !Validator.isEmail(newEmail)) {
                edtEmail.setError("Email không hợp lệ");
                edtEmail.requestFocus();
                return;
            }

            fullName.setText(newFullname);
            phone.setText(newPhone);
            email.setText(newEmail);

            userInfo.setFullname(newFullname);
            userInfo.setPhone(newPhone);
            userInfo.setEmail(newEmail);

            dialog.dismiss();
        });
    }


}
