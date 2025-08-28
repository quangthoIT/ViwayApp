package com.example.test.login_logout_forgotpass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;

public class RegisterNameActivity extends AppCompatActivity {

    private EditText edName;
    private Button btnNext;
    private String contact, username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entername), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edName = findViewById(R.id.EnterName_editName);
        btnNext = findViewById(R.id.EnterName_btnNext);

        Intent intent = getIntent();
        contact = intent.getStringExtra("contact");

//        ------- Nhập họ tên và tiếp tục -------------------
        btnNext.setOnClickListener(v -> {
            username = edName.getText().toString().trim();

            if (username.isEmpty()) {
                NotifyDialogHelper.showNotifyDialog(
                        RegisterNameActivity.this,
                        "Vui lòng nhập họ tên trước khi chuyển trang!"
                );
            } else {
                Intent it = new Intent(RegisterNameActivity.this, RegisterPassWord_Contact_Activity.class);
                it.putExtra("contact", contact);
                it.putExtra("username", username);
                startActivity(it);
            }
        });

    }

}
