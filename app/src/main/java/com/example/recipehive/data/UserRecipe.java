package com.example.recipehive.data;

public class UserRecipe extends Recipe{
    private NewRecipeData recipe;

    public UserRecipe(){
        super();
        this.recipe=new NewRecipeData();
    }
    public UserRecipe(UserRecipe userRecipe){
        super(userRecipe.getRecipeName(),userRecipe.getRecipeType());
        this.recipe=userRecipe.recipe;
    }
    public UserRecipe(String recipeName, String recipeType, NewRecipeData recipe) {
        super(recipeName,recipeType);

        this.recipe = recipe;
    }

    public NewRecipeData getRecipe() {
        return recipe;
    }

    public void setRecipe(NewRecipeData recipe) {
        this.recipe = recipe;
    }
}
