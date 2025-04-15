package com.example.huongdannauan.model;

import java.util.List;

public class Recipe {
    private String title;
    private String summary;
    private Integer cookingMinutes;
    private Integer preparationMinutes;
    private Integer healthScore;
    private Integer id;
    private String image;
    private List<String> cuisines;
    private List<String> dishTypes;
    private List<Ingredient> extendedIngredients;
    private List<Instruction> analyzedInstructions;

    public Recipe() {
        // Required empty constructor for Firebase
    }

    public List<Instruction> getAnalyzedInstructions() {
        return analyzedInstructions;
    }

    public void setAnalyzedInstructions(List<Instruction> analyzedInstructions) {
        this.analyzedInstructions = analyzedInstructions;
    }

    // Getters và Setters
    public String getTitle() {
        return title != null ? title : "No Title"; // Trả về giá trị mặc định nếu null
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary != null ? summary : "No Summary Available"; // Trả về giá trị mặc định nếu null
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getCookingMinutes() {
        return cookingMinutes != null ? cookingMinutes : 0; // Giá trị mặc định là 0 nếu null
    }

    public void setCookingMinutes(Integer cookingMinutes) {
        this.cookingMinutes = cookingMinutes;
    }

    public Integer getPreparationMinutes() {
        return preparationMinutes != null ? preparationMinutes : 0; // Giá trị mặc định là 0 nếu null
    }

    public void setPreparationMinutes(Integer preparationMinutes) {
        this.preparationMinutes = preparationMinutes;
    }

    public Integer getHealthScore() {
        return healthScore != null ? healthScore : 0; // Giá trị mặc định là 0 nếu null
    }

    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }

    public Integer getId() {
        return id != null ? id : -1; // Giá trị mặc định là -1 nếu null
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<Ingredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

}