package com.example.recipehive.data;

public class UserRecipe {
    private String recipeName;
    private String recipeType;
    private NewRecipeData recipe;

    public UserRecipe(String recipeName, String recipeType, NewRecipeData recipe) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipe = recipe;
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

    public NewRecipeData getRecipe() {
        return recipe;
    }

    public void setRecipe(NewRecipeData recipe) {
        this.recipe = recipe;
    }
}
