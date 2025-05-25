package com.example.pip_proiect;

/**
 * Reprezintă o intrare de rețetă care conține ingrediente și rețeta propriu-zisă.
 */
public class RecipeEntry {

    private final String ingredients;
    private final String recipes;

    /**
     * Constructor pentru {@code RecipeEntry}.
     *
     * @param ingredients Lista de ingrediente necesare rețetei, sub formă de text.
     * @param recipes     Instrucțiuni sau descrierea rețetei, sub formă de text.
     */
    public RecipeEntry(String ingredients, String recipes) {
        this.ingredients = ingredients;
        this.recipes = recipes;
    }

    /**
     * Obține lista de ingrediente.
     *
     * @return Un șir de caractere care conține ingredientele rețetei.
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Obține instrucțiunile sau descrierea rețetei.
     *
     * @return Un șir de caractere care conține rețeta.
     */
    public String getRecipes() {
        return recipes;
    }
}
