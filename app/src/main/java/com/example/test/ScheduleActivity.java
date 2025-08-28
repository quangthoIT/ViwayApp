package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapter.ScheduleAdapter;
import com.example.test.config.Config;
import com.example.test.response.ScheduleResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleResponse> scheduleResponseList;
    private TextView diemDi, diemDen;
    private ImageButton btnBack;
    private String token, DiemDi, DiemDen;
    private Integer tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.schedule_viway), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        btnBack = findViewById(R.id.Schedule_btnBack);
        diemDi = findViewById(R.id.Schedule_tvDiemDi);
        diemDen = findViewById(R.id.Schedule_tvDiemDen);
        recyclerView = findViewById(R.id.item_Schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        tripId = intent.getIntExtra("ticketId", -1);
        DiemDi = intent.getStringExtra("diemDi");
        DiemDen = intent.getStringExtra("diemDen");

        diemDi.setText(DiemDi);
        diemDen.setText(DiemDen);

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        requestSchedule(token, tripId);
        scheduleResponseList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleResponseList, DiemDi, DiemDen);
        recyclerView.setAdapter(scheduleAdapter);

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void requestSchedule(String token, Integer tripId) {
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
                        Toast.makeText(ScheduleActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
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
                            scheduleResponseList.clear();
                            scheduleResponseList.addAll(newScheduleList);
                            scheduleAdapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(ScheduleActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ScheduleActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

}
