package com.example.test.utils;

public class Validator {

    public static boolean isEmail(String input) {
        return input.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isPhoneNumber(String input) {
        return input.matches("^(0|\\+84)(3[2-9]|5[2-9]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$");
    }


}
