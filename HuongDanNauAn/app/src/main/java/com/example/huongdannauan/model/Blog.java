package com.example.huongdannauan.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Blog {
    String title, img, content, date;
    Integer id;

    public Blog() {
    }

    public Blog(String title, String img, String content, String date, Integer id) {
        this.title = title;
        this.img = img;
        this.content = content;
        this.date = date;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Date getParsedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Trả về null nếu lỗi
        }
    }
}
