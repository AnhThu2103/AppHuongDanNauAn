package com.example.huongdannauan.model;

import java.util.List;

public class Instruction {

    private String name; // Tên của hướng dẫn, có thể là chuỗi rỗng ""
    private List<Step> steps; // Danh sách các bước

    public Instruction() {
    }

    // Constructor cho Instruction
    public Instruction(String name, List<Step> steps) {
        this.name = name;
        this.steps = steps;
    }

    // Getters và setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
