package com.example.pip_proiect;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Teste pentru clasa RecipeEntry.
 */
public class RecipeEntryTest {

    @Test
    public void testConstructorAndGetters() {

        String testIngredients = "ouă, făină, zahăr";
        String testRecipes = "Amestecă ingredientele și coace la 180°C 30 min.";

        RecipeEntry entry = new RecipeEntry(testIngredients, testRecipes);

        assertEquals(testIngredients, entry.getIngredients());
        assertEquals(testRecipes, entry.getRecipes());
    }
}


