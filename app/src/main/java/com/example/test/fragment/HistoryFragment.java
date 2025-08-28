package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.test.ticket.DetailHistoryActivity;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.TicketHistoryResponse;
import com.example.test.adapter.TicketHistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TicketHistoryAdapter ticketHistoryAdapter;
    private List<TicketHistoryResponse> ticketHistoryList;
    private Integer userId;
    private String token;
    private LinearLayout textNoData;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.item_History);
        textNoData = view.findViewById(R.id.FragmentHistory_noDaTa);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ticketHistoryList = new ArrayList<>();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        requestTicketHistory(userId, token);

        ticketHistoryAdapter = new TicketHistoryAdapter(ticketHistoryList, ticketHistory -> {
            Intent intent = new Intent(getContext(), DetailHistoryActivity.class);
            intent.putExtra("ticketHistory", ticketHistory);
            startActivity(intent);
        });
        recyclerView.setAdapter(ticketHistoryAdapter);

        return view;
    }


    private void requestTicketHistory(Integer userId, String token) {
        String baseUrl = Config.BASE_URL+"/ticket/history/"+ userId;
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
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if (jsonArray.length() == 0) {
                            requireActivity().runOnUiThread(() -> {
                                textNoData.setVisibility(View.VISIBLE);
                                ticketHistoryList.clear();
                                ticketHistoryAdapter.notifyDataSetChanged();
                            });
                            return;
                        }

                        List<TicketHistoryResponse> newTicketList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            JSONArray seatArray = obj.getJSONArray("seat_code");
                            List<String> seatList = new ArrayList<>();
                            for (int j = 0; j < seatArray.length(); j++) {
                                seatList.add(seatArray.getString(j));
                            }
                            String seatCodeStr = String.join(", ", seatList);
                            TicketHistoryResponse ticket = new TicketHistoryResponse(
                                    obj.optString("code_ticket", "Trống"),
                                    obj.optString("status", "Trống"),
                                    obj.optString("origin", ""),
                                    obj.optString("destination", "Trống"),
                                    seatCodeStr,
                                    obj.optString("full_time", "Trống")
                            );
                            newTicketList.add(ticket);
                        }

                        requireActivity().runOnUiThread(() -> {
                            textNoData.setVisibility(View.GONE);
                            ticketHistoryList.clear();
                            Collections.reverse(newTicketList);
                            ticketHistoryList.addAll(newTicketList);
                            ticketHistoryAdapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    requireActivity().runOnUiThread(() -> {
                        textNoData.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }

}