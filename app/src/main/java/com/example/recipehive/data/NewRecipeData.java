package com.example.recipehive.data;

import java.util.ArrayList;

public class NewRecipeData {
    private ArrayList<IngDataModel> ingredientsToRecipe;
    private ArrayList<String> stepsToRecipe;

    public NewRecipeData() {
        ingredientsToRecipe=new ArrayList<>();
        stepsToRecipe=new ArrayList<>();
    }

    public ArrayList<IngDataModel> getIngredientsToRecipe() {
        return ingredientsToRecipe;
    }

    public void addIngredientToRecipe(IngDataModel ingredientToRecipe) {
        this.ingredientsToRecipe.add(ingredientToRecipe);
    }
    public void removeIngredientFromRecipe(IngDataModel ingredient){
        this.ingredientsToRecipe.remove(ingredient);
    }
    public int getIngredientsSize(){
        return ingredientsToRecipe.size();
    }

    public ArrayList<String> getStepsToRecipe() {
        return stepsToRecipe;
    }

    public void addStepToRecipe(String stepToRecipe) {
        this.stepsToRecipe.add(stepToRecipe);
    }
    public void removeStepFromRecipe(String step){
        this.stepsToRecipe.remove(step);
    }

    public int getStepsSize(){
        return stepsToRecipe.size();
    }
}
