package com.example.test.support;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class SupportDirectoryActivity extends AppCompatActivity {

    private LinearLayout DanhGia, NapRut, ThueXe, DonTra, Khac, LichSuMuaVe;
    private Button btnXemYeuCau;
    private ImageButton btnBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.support), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DanhGia = findViewById(R.id.Support_DanhGia);
        NapRut = findViewById(R.id.Support_RutNap);
        ThueXe = findViewById(R.id.Support_ThueXe);
        DonTra = findViewById(R.id.Support_DonTra);
        Khac = findViewById(R.id.Support_HoTroKhac);
        LichSuMuaVe = findViewById(R.id.Support_LichSuMuaVe);

        btnBack = findViewById(R.id.Support_IconBack);
        btnXemYeuCau = findViewById(R.id.Support_BtnXemYeuCau);

//  ---------------------- Các danh mục cần được hỗ trợ ------------------------------------------------------
        DanhGia.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Hỗ trợ và đánh giá");
            startActivity(intent);
        });

        NapRut.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Hỗ trợ Nạp/ Rút ví, lỗi thanh toán");
            startActivity(intent);
        });

        ThueXe.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Liên hệ dịch vụ thuê xe");
            startActivity(intent);
        });

        DonTra.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Dịch vụ đón trả tận nơi");
            startActivity(intent);
        });

        Khac.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Hỗ trợ khác");
            startActivity(intent);
        });

        LichSuMuaVe.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportContentActivity.class);
            intent.putExtra("tieuDe", "Hỗ trợ lịch sử mua vé");
            startActivity(intent);
        });

//  ------------------------ Nút quay lại -----------------------------------------------------------------

        btnBack.setOnClickListener(v -> {
            finish();
        });

//  ------------------------ Chuyển đến trang danh sách các yêu cầu đã gửi ---------------------------------
        btnXemYeuCau.setOnClickListener(v -> {
            Intent intent = new Intent(SupportDirectoryActivity.this, SupportListActivity.class);
            startActivity(intent);
        });

    }

}
