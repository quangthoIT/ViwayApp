package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.utils.NotifyDialogHelper;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.info.InfoCustumerActivity;
import com.example.test.login_logout_forgotpass.InputPhoneActivity;
import com.example.test.response.UserInfoResponse;
import com.example.test.support.SupportDirectoryActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountFragment extends Fragment {

    private LinearLayout accountInfo, logOut, deleteAccount, setting, social, support;
    private TextView fullName, phone;
    private ImageView imagePerson;
    private Integer userId;
    private String token, imageUrl;
    private UserInfoResponse userInfo;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        accountInfo = view.findViewById(R.id.Account_Info);
        setting = view.findViewById(R.id.Account_setting);
        social = view.findViewById(R.id.Account_social);
        support = view.findViewById(R.id.Account_support);
        logOut = view.findViewById(R.id.Account_Logout);
        deleteAccount = view.findViewById(R.id.Account_DeleteAccount);
        imagePerson = view.findViewById(R.id.Account_imgPerson);
        fullName = view.findViewById(R.id.Account_tvFullName);
        phone = view.findViewById(R.id.Account_tvPhone);

//  ----------------------- Lấy thông tin được lưu trữ -------------------------------------
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");
        imageUrl = sharedPreferences.getString("imageUrl", null);

//  ----------------------- Hiển thị hình đại diện nếu có ----------------------------------
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullImageUrl = Config.BASE_URL_IMAGE + imageUrl;

            GlideUrl glideUrl = new GlideUrl(fullImageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imagePerson);
        } else {
            imagePerson.setImageResource(R.drawable.ic_person);
        }

//  ----------------------- Lấy thông tin khách hàng --------------------------------
        GetUserInfo(userId, token);

//        ------------------ Chuyển đến trang thông tin -----------------------------
        accountInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InfoCustumerActivity.class);
            startActivity(intent);
        });

//        ------------------- Tính năng cài đặt -------------------------------------
        setting.setOnClickListener(v -> {
            NotifyDialogHelper.showNotifyDialog(
                    getContext(),
                    "Tính năng đang được nâng cấp. Chân thành xin lỗi!"
            );
        });

//        ------------------- Chuyển đến trang hỗ trợ -------------------------------
        support.setOnClickListener(v -> {
            Intent iten = new Intent(getContext(), SupportDirectoryActivity.class);
            startActivity(iten);
        });

//        ------------------- Chuyển đến cộng đồng chỉ cần thay thế bằng link ------
        social.setOnClickListener(v -> {
            NotifyDialogHelper.showNotifyDialog(
                    getContext(),
                    "Tính năng đang được nâng cấp. Chân thành xin lỗi!"
            );
        });

//      ---------------------- Yêu cầu thoát tài khoản -----------------------------
        logOut.setOnClickListener(v -> {
            showLogoutDialog();
        });

//      ---------------------- Yêu cầu xóa tài khoản -------------------------------
        deleteAccount.setOnClickListener(v -> {
            showDeleteAccount();
        });

        return view;
    }


//    ------------------------ Hàm lấy thông tin khách hàng ------------------------
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
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);

                        String fullname = getSafeString(obj, "fullname");
                        String phone = getSafeString(obj, "phone_number");
                        String email = getSafeString(obj, "email");

                        userInfo = new UserInfoResponse(fullname, phone, email);

                        requireActivity().runOnUiThread(() -> updateDisplay(userInfo));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

//    ----------------------- Hàm cập nhật giao diện màn hình ------------------------------
    private void updateDisplay(UserInfoResponse userInfo) {
        fullName.setText(userInfo.getFullname());
        phone.setText(userInfo.getPhone());
    }


//    ----------------------- Hộp thoại cảnh báo khi đăng xuất -----------------------------
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_logout, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.Logout_btnNo);
        Button btnYes = view.findViewById(R.id.Logout_btnYes);

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            requestLogout(token);
            dialog.dismiss();

        });
    }

//----------------- Hộp thoại cảnh báo khi xóa tài khoản --------------------------------------
    private void showDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_account, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.Delete_btnNo);
        Button btnYes = view.findViewById(R.id.Delete_btnYes);

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            requireContext().getSharedPreferences("VIWAY", getContext().MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            requestDeleteAccount(token, userId);
            dialog.dismiss();


        });
    }

//    ---------------- Hàm yêu cầu đăng xuất --------------------------------------
    private void requestLogout (String token) {
        String baseUrl = Config.BASE_URL+ "/users/logout" ;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .post(RequestBody.create(new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                    requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                            requireContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                            Intent intent = new Intent(requireContext(), InputPhoneActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                        Log.d("Lỗi: ", "Response code: " + response.code());
                    });
                }
            }
        });
    }

//  --------------------- Hàm yêu cầu xóa tài khoản -----------------------------------------
    private void requestDeleteAccount (String token, Integer userId) {
        String baseUrl = Config.BASE_URL+ "/users/delete/" + userId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                        requireContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Intent intent = new Intent(requireContext(), InputPhoneActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

//  -------------------- Hàm kiểm tra xem dữ liệu nhận có bị null không ---------------------------------
    private String getSafeString(JSONObject obj, String key) {
        if (obj.isNull(key)) return "Chưa cập nhật";
        String value = obj.optString(key, "Chưa cập nhật");
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return "Chưa cập nhật";
        }
        return value;
    }

}
