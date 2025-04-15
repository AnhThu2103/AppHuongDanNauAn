package com.example.huongdannauan.model;

public class Step {

    private int number; // Số thứ tự bước
    private String step; // Mô tả chi tiết bước

    public Step() {
    }

    // Constructor cho Step
    public Step(int number, String step) {
        this.number = number;
        this.step = step;
    }

    // Getters và setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
