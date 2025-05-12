package com.example.pip_proiect;

public class RecipeEntry {
    private final String ingredients;
    private final String recipes;

    public RecipeEntry(String ingredients, String recipes) {
        this.ingredients = ingredients;
        this.recipes = recipes;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getRecipes() {
        return recipes;
    }
}
