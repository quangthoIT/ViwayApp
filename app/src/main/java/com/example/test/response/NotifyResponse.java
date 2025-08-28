package com.example.test.response;
import com.google.gson.annotations.SerializedName;
public class NotifyResponse {

    @SerializedName("title")
    private String tieuDeThongBao;

    @SerializedName("sent_time")
    private String thoiGianThongBao;

    @SerializedName("content")
    private String noiDungThongBao;

    public NotifyResponse(String tieuDeThongBao, String thoiGianThongBao, String noiDungThongBao){
        this.tieuDeThongBao = tieuDeThongBao;
        this.thoiGianThongBao = thoiGianThongBao;
        this.noiDungThongBao = noiDungThongBao;
    }

    public String getTieuDeThongBao() {
        return tieuDeThongBao;
    }

    public String getThoiGianThongBao() {
        return thoiGianThongBao;
    }

    public String getNoiDungThongBao() {
        return noiDungThongBao;
    }

}
