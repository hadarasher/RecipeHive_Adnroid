package com.example.recipehive.data;

import java.net.URL;

public class URLRecipe {
    private String recipeName;
    private String recipeType;
    private String  recipeURL;

    public URLRecipe(String recipeName, String recipeType, String recipeURL) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipeURL = recipeURL;
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

    public String getRecipeURL() {
        return recipeURL;
    }

    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }
}
