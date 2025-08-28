package com.example.test.response;

import com.google.gson.annotations.SerializedName;

public class SeatStatusResponse {

    @SerializedName("SeatID")
    private String seatId;

    @SerializedName("Status")
    private String status;

    public SeatStatusResponse(String seatId, String status) {
        this.seatId = seatId;
        this.status = status;
    }

    public String getSeatId() {
        return seatId;
    }

    public String getStatus() {
        return status;
    }
}
