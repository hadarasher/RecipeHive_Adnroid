package com.example.recipehive.data;

public class Recipe {
    private String recipeName;
    private String recipeType;

    public Recipe() {
        this.recipeName = "";
        this.recipeType = "";
    }
    public Recipe(String recipeName, String recipeType) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }
}
