package com.example.recipehive.data;

public class Ingredient {
    private String name;

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Boolean isEqual(Ingredient ingredient){
        return name.equalsIgnoreCase(ingredient.getName());
    }
    public String toString(){
        return name;
    }
}
