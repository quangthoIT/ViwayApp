package com.example.test.response;

import com.google.gson.annotations.SerializedName;

public class SupportResponse {

    @SerializedName("id")
    private Integer idYeuCau;
    @SerializedName("phone_number")
    private String phone;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("status")
    private String trangThai;

    @SerializedName("code")
    private String maYeuCau;

    @SerializedName("created_up")
    private String thoiGianYeuCau;

    public SupportResponse (Integer id, String phone, String fullName, String trangThai, String maYeuCau, String thoiGianYeuCau) {

        this.idYeuCau = id;
        this.phone = phone;
        this.fullName = fullName;
        this.trangThai = trangThai;
        this.maYeuCau = maYeuCau;
        this.thoiGianYeuCau = thoiGianYeuCau;

    }

    public Integer getIdYeuCau() {
        return idYeuCau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getMaYeuCau() {
        return maYeuCau;
    }

    public String getThoiGianYeuCau() {
        return thoiGianYeuCau;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }
}
