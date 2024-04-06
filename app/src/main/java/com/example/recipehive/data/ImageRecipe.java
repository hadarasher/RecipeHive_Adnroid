package com.example.recipehive.data;

public class ImageRecipe {

    private String recipeName;
    private String recipeType;
    private String recipeImageUrl;

    public ImageRecipe(String recipeName, String recipeType, String recipeImageUrl) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipeImageUrl = recipeImageUrl;
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

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

}
