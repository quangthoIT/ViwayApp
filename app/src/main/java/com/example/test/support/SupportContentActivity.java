package com.example.test.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.utils.FileUtils;
import com.example.test.utils.ImagePickerHelper;
import com.example.test.InputStreamRequestBody;
import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.config.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupportContentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private File selectedImageFile;
    private TextView tieuDe;
    private String text, token;
    private Integer userId;
    private ImageButton btnBack;
    private ImageView previewImage;
    private Button btnSend;
    private EditText codeTicket, content;
    private LinearLayout loadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.evaluation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tieuDe = findViewById(R.id.Evaluation_tvHeader);
        codeTicket = findViewById(R.id.Evaluation_edNhapMaVe);
        content = findViewById(R.id.Evaluation_edNhapMoTa);
        loadImage = findViewById(R.id.Evaluation_LoadImage);
        previewImage = findViewById(R.id.Evaluation_imagePreview);
        btnBack = findViewById(R.id.Evaluation_btnBack);
        btnSend = findViewById(R.id.Evaluation_btnGuiYeuCau);

        Intent intent = getIntent();
        text = intent.getStringExtra("tieuDe");
        tieuDe.setText(text);

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        loadImage.setOnClickListener(v -> {
            ImagePickerHelper.pickImageFromGallery(this, (file, uri) -> {
                selectedImageFile = file;
                selectedImageUri = uri;
                previewImage.setVisibility(View.VISIBLE);
                previewImage.setImageURI(uri);
            });
        });


        btnSend.setOnClickListener(v -> {
            String maVe = codeTicket.getText().toString().trim();
            String noiDung = content.getText().toString().trim();
            String idUser = userId.toString();

            if (maVe.isEmpty() || noiDung.isEmpty()) {
                NotifyDialogHelper.showNotifyDialog(this, "Vui lòng nhập đầy đủ mã vé và nội dung!");
                return;
            }

            sendSupportToServer(token, idUser, maVe, noiDung, selectedImageUri);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePickerHelper.handleActivityResult(this, requestCode, resultCode, data);
    }

//------------- Gửi thông tin hỗ trợ đi -----------------------------------------
    private void sendSupportToServer(String token, String userId, String ticketCode, String description, Uri imageUri) {
        String baseUrl = Config.BASE_URL + "/feedbacks";

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("ticketCode", ticketCode)
                .addFormDataPart("description", description);

        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                String fileName = FileUtils.getFileNameFromUri(this, imageUri);

                RequestBody imageBody = new InputStreamRequestBody("image/*", inputStream);
                builder.addFormDataPart("files", fileName, imageBody);
            } catch (Exception e) {

            }
        }


        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SupportContentActivity.this, "Lỗi kết nối server.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> showSendFeedbackSuccess());
                } else {
                    runOnUiThread(() -> Toast.makeText(SupportContentActivity.this, "Gửi hỗ trợ thất bại", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

//------------- Thông báo gửi hỗ trợ thành công ---------------------------------
    public void showSendFeedbackSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SupportContentActivity.this);
        View view = LayoutInflater.from(SupportContentActivity.this).inflate(R.layout.dialog_create_account_success, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnDong = view.findViewById(R.id.CreateAccountSuccess_btnDong);
        TextView title = view.findViewById(R.id.CreateAccountSuccess_title);
        TextView content = view.findViewById(R.id.content);

        title.setText("Gửi hỗ trợ thành công");
        content.setText("Chuyển hướng về trang danh sách hỗ trợ.");
        btnDong.setOnClickListener(v -> {
            Intent it = new Intent(SupportContentActivity.this, SupportListActivity.class);
            startActivity(it);
            dialog.dismiss();
            finish();

        });

    }



}
