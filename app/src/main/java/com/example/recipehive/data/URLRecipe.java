package com.example.recipehive.data;

import java.net.URL;

public class URLRecipe extends Recipe{
    private String  recipeURL;

    public URLRecipe(){
        super();
        recipeURL="";
    }
    public URLRecipe(URLRecipe urlRecipe){
        super(urlRecipe.getRecipeName(), urlRecipe.getRecipeType());
        this.recipeURL=urlRecipe.recipeURL;
    }
    public URLRecipe(String recipeName, String recipeType, String recipeURL) {
        super(recipeName,recipeType);
        this.recipeURL = recipeURL;
    }

    public String getRecipeURL() {
        return recipeURL;
    }

    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }
}
