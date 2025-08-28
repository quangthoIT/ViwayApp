package com.example.test.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.SupportAdapter;
import com.example.test.config.Config;
import com.example.test.response.SupportResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SupportListActivity extends AppCompatActivity {

    private RecyclerView supportRecycleView;
    private SupportAdapter supportAdapter;
    private List<SupportResponse> supportResponseList;
    private LinearLayout textNoData;
    private ImageButton btnBack;
    private String token;
    private Integer userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_support), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        supportRecycleView = findViewById(R.id.item_Feedback);
        textNoData = findViewById(R.id.ListFeedback_noDaTa);
        btnBack = findViewById(R.id.ListFeedback_btnBack);

        supportRecycleView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        requestSupport(userId, token);
        supportResponseList = new ArrayList<>();
        supportAdapter = new SupportAdapter(supportResponseList);
        supportRecycleView.setAdapter(supportAdapter);

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

//    -------------- Hiển thị danh sách các yêu cầu hỗ trợ đã gửi -------------------------------------
    private void requestSupport(Integer userId, String token) {
        String baseUrl = Config.BASE_URL+"/feedbacks/user/"+ userId;
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
                        Toast.makeText(SupportListActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if (jsonArray.length() == 0) {
                            runOnUiThread(() -> {
                                textNoData.setVisibility(View.VISIBLE);
                                supportResponseList.clear();
                                supportAdapter.notifyDataSetChanged();
                            });
                            return;
                        }

                        List<SupportResponse> newSupportList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            SupportResponse support = new SupportResponse(
                                    obj.optInt("id", -1),
                                    obj.optString("phone_number", "Trống"),
                                    obj.optString("full_name", "Trống"),
                                    obj.optString("status", "Trống"),
                                    obj.optString("ticket_code", "Trống"),
                                    obj.optString("created_up", "Trống")

                            );
                            newSupportList.add(support);
                        }

                        runOnUiThread(() -> {
                            textNoData.setVisibility(View.GONE);
                            supportResponseList.clear();
                            Collections.reverse(newSupportList);
                            supportResponseList.addAll(newSupportList);
                            supportAdapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(SupportListActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() -> {
                        textNoData.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }



}
