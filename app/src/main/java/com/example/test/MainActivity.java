package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.test.fragment.AccountFragment;
import com.example.test.fragment.HistoryFragment;
import com.example.test.fragment.HomeFragment;
import com.example.test.fragment.NotifyFragment;
import com.example.test.support.SupportDirectoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView name;
    private ImageView btnHelpIcon, btnAccountIcon;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        name = findViewById(R.id.home_account_name);
        btnHelpIcon = findViewById(R.id.home_helpIcon);
        btnAccountIcon = findViewById(R.id.home_accountIcon);

        SharedPreferences prefs = getSharedPreferences("VIWAY", MODE_PRIVATE);
        String NamePre = prefs.getString("fullName", null);
        name.setText(NamePre);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (id == R.id.navigation_notifications) {
                selectedFragment = new NotifyFragment();
            } else if (id == R.id.navigation_account) {
                selectedFragment = new AccountFragment();
            } else {
                return false;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

        btnAccountIcon.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        });

        btnHelpIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SupportDirectoryActivity.class);
            startActivity(intent);
        });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null && "viway".equals(uri.getScheme()) && "payment".equals(uri.getHost())) {
            String status = uri.getQueryParameter("status");

            if ("success".equals(status)) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}