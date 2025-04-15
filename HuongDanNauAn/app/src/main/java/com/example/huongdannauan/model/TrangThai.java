package com.example.huongdannauan.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrangThai {
    public static String userEmail = "";
    public static User currentUser;
    public static String getCurrentDateString() {
        // Định dạng ngày theo yêu cầu
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Lấy ngày hiện tại
        Date date = new Date();
        // Chuyển đổi thành chuỗi
        return dateFormat.format(date);
    }

}
