package com.example.recipehive.data;

public class ImageRecipe extends Recipe{


    private String recipeImageUrl;

    public ImageRecipe(){
        super();
        this.recipeImageUrl="";
    }
    public ImageRecipe(ImageRecipe imageRecipe){
        super(imageRecipe.getRecipeName(), imageRecipe.getRecipeType());
        this.recipeImageUrl=imageRecipe.recipeImageUrl;
    }
    public ImageRecipe(String recipeName, String recipeType, String recipeImageUrl) {
        super(recipeName,recipeType);
        this.recipeImageUrl = recipeImageUrl;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

}
