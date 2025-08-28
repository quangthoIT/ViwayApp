package com.example.test.login_logout_forgotpass;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.config.Config;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassword_CreatePass extends AppCompatActivity {

    private TextView chuThuong, chuHoa, kiTuDacBiet, so;
    private ImageButton btnBack;
    private TextInputEditText passWord, rePassWord, thongTin;

    private Button btnXacNhan;
    private String contact, username, email, phone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_create_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fogot_createpass), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chuThuong = findViewById(R.id.Forgot_tvLowercase);
        chuHoa = findViewById(R.id.Forgot_tvUppercase);
        kiTuDacBiet = findViewById(R.id.Forgot_tvSpecialChar);
        so = findViewById(R.id.Forgot_tvNumber);
        passWord = findViewById(R.id.Forgot_inputPassWord);
        rePassWord = findViewById(R.id.Forgot_reInputPassWord);
        btnXacNhan = findViewById(R.id.Forgot_btnXacNhan);
        btnBack = findViewById(R.id.Forgot_btnBack);

        Intent it = getIntent();
        contact = it.getStringExtra("contact");
        username = it.getStringExtra("username");


        // ------------------------ Kiểm tra nhập mật khẩu có đủ kí tự yêu cầu không ----------------------
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                if (password.matches(".*[a-z].*")) {
                    chuThuong.setTextColor(Color.BLUE);
                } else {
                    chuThuong.setTextColor(Color.RED);
                }

                if (password.matches(".*[A-Z].*")) {
                    chuHoa.setTextColor(Color.BLUE);
                } else {
                    chuHoa.setTextColor(Color.RED);
                }

                if (password.matches(".*\\d.*")) {
                    so.setTextColor(Color.BLUE);
                } else {
                    so.setTextColor(Color.RED);
                }

                if (password.matches(".*[@$!%*?&^#()._+=\\-].*")) {
                    kiTuDacBiet.setTextColor(Color.BLUE);
                } else {
                    kiTuDacBiet.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // ------------------------ Nút xác nhận --------------------------------
        btnXacNhan.setOnClickListener(v -> {
            String pass = passWord.getText().toString().trim();
            String repass = rePassWord.getText().toString().trim();


            // Kiểm tra xem còn ô nào trống không
            if (pass.isEmpty() || repass.isEmpty()) {
                NotifyDialogHelper.showNotifyDialog(
                        ForgotPassword_CreatePass.this,
                        "Vui lòng nhập đầy đủ thông tin!"
                );
                return;
            }

            // Kiểm tra độ mạnh của mật khẩu
            if (!isStrongPassword(pass)) {
                NotifyDialogHelper.showNotifyDialog(
                        ForgotPassword_CreatePass.this,
                        "Mật khẩu yếu! Phải có chữ hoa, chữ thường, số và ký tự đặc biệt."
                );
                return;
            }

            // Kiểm tra khớp mật khẩu
            if (!pass.equals(repass)) {
                rePassWord.setError("Mật khẩu nhập lại không khớp");
                rePassWord.requestFocus();
                return;
            }

            // OK hết, gửi dữ liệu
            sendDataToServer(contact, pass);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }


//---------- Hàm gửi dữ mật khẩu mới lên server -----------------------------------
    private void sendDataToServer (String contact, String newPassword) {

        String url = Config.BASE_URL + "/users/auth/reset-password";
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();

        try{
            data.put("contact", contact);
            data.put("newPassword", newPassword);

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
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ForgotPassword_CreatePass.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> showNotifyCreateSuccessfull());
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPassword_CreatePass.this, "Tạo tài khoản thất bại", Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }


    // ------------------------- Hàm kiểm tra mật khẩu mạnh ----------------------------
    private boolean isStrongPassword(String password) {
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&^#()._+=\\-].*");
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

    // ------------------------ Hiển thị thông báo tạo mật khẩu mới thành công -----------------------
    public void showNotifyCreateSuccessfull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword_CreatePass.this);
        View view = LayoutInflater.from(ForgotPassword_CreatePass.this).inflate(R.layout.dialog_create_account_success, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.CreateAccountSuccess_btnDong);
        TextView title = view.findViewById(R.id.CreateAccountSuccess_title);

        title.setText("Tạo mật khẩu mới thành công");

        btnNo.setOnClickListener(v -> {
            Intent it = new Intent(ForgotPassword_CreatePass.this, InputPhoneActivity.class);
            startActivity(it);
            dialog.dismiss();
            finish();

        });

    }
}
