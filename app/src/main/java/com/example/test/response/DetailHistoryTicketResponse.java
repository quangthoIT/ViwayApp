package com.example.test.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailHistoryTicketResponse implements Serializable {

    @SerializedName("fullname")
    private String fullname;
    @SerializedName("phone_number")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("status")
    private String status;
    @SerializedName("route")
    private String route;
    @SerializedName("total_seat")
    private Integer total_seat;
    @SerializedName("seat_code")
    private String seat_code;
    @SerializedName("full_time")
    private String full_time;

    @SerializedName("pickUpPoint")
    private String pickUpPoint;
    @SerializedName("dropOffPoint")
    private String dropOffPoint;
    @SerializedName("price")
    private Integer price;

    public DetailHistoryTicketResponse(String fullname,String phone, String email, String status,
                                       String route, Integer total_seat, String seat_code, String full_time,
                                       String pickUpPoint, String dropOffPoint, Integer price){
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.route = route;
        this. total_seat = total_seat;
        this.seat_code = seat_code;
        this.full_time = full_time;
        this.pickUpPoint = pickUpPoint;
        this.dropOffPoint = dropOffPoint;
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public String getFull_time() {
        return full_time;
    }

    public Integer getPrice() {
        return price;
    }

    public String getDropOffPoint() {
        return dropOffPoint;
    }

    public String getPickUpPoint() {
        return pickUpPoint;
    }

    public String getRoute() {
        return route;
    }

    public String getSeat_code() {
        return seat_code;
    }

    public Integer getTotal_seat() {
        return total_seat;
    }
}
