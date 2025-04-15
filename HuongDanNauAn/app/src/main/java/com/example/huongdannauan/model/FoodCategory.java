package com.example.huongdannauan.model;

public class FoodCategory {
    private String name;
    private String imageResId;

    public FoodCategory(String name, String imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getImageResId() {
        return imageResId;
    }
}
