package com.example.test.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ScheduleResponse implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("location")
    private String benXe;

    @SerializedName("address")
    private String diaChi;

    @SerializedName("estimatedTime")
    private String thoiGian;


    public ScheduleResponse(Integer id, String benXe, String diaChi, String thoiGian) {
        this.id = id;
        this.benXe = benXe;
        this.diaChi = diaChi;
        this.thoiGian = thoiGian;
    }

    public String getBenXe() {
        return benXe;
    }

    public Integer getId() {
        return id;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getThoiGian() {
        return thoiGian;
    }
}
