package com.example.test.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InfoCustomerResponse implements Serializable {

    @SerializedName("id")
    private Integer userId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phoneNumber")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("sex")
    private String sex;
    @SerializedName("job")
    private String job;
    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    public InfoCustomerResponse (Integer id, String fullName, String phone, String email,
                                 String address, String sex, String job, String dateOfBirth) {

        this.userId = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.sex = sex;
        this.job = job;
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getJob() {
        return job;
    }

    public String getSex() {
        return sex;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
