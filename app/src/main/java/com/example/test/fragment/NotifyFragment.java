package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.test.config.Config;
import com.example.test.response.NotifyResponse;
import com.example.test.R;
import com.example.test.adapter.NotifyAdapter;
import com.example.test.response.TicketHistoryResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotifyFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotifyAdapter adapterNotify;
    private List<NotifyResponse> notifyResponseList;
    private String token;
    private Integer userId;
    private LinearLayout textNoData;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        recyclerView = view.findViewById(R.id.item_Notify);
        textNoData = view.findViewById(R.id.FragmentNotify_noDaTa);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        requestNotifi(userId, token);
        notifyResponseList = new ArrayList<>();
        adapterNotify = new NotifyAdapter(notifyResponseList);
        recyclerView.setAdapter(adapterNotify);

        return view;
    }

    private void requestNotifi(Integer userId, String token) {
        String baseUrl = Config.BASE_URL+"/notifications/user/"+ userId;
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
                                notifyResponseList.clear();
                                adapterNotify.notifyDataSetChanged();
                            });
                            return;
                        }

                        List<NotifyResponse> newNotifyList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            NotifyResponse ticket = new NotifyResponse(
                                    obj.optString("title", "Trống"),
                                    obj.optString("sent_time", "Trống"),
                                    obj.optString("content", "Trống")
                            );
                            newNotifyList.add(ticket);
                        }

                        requireActivity().runOnUiThread(() -> {
                            textNoData.setVisibility(View.GONE);
                            notifyResponseList.clear();
                            Collections.reverse(newNotifyList);
                            notifyResponseList.addAll(newNotifyList);
                            adapterNotify.notifyDataSetChanged();
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

