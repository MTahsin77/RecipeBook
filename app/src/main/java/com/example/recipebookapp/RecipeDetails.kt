package com.example.recipebookapp

data class RecipeDetails(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String,
    val ingredients: List<String> = emptyList(),
    val measures: List<String> = emptyList(),
    val strYoutube: String,
    var rating: Float = 0f,
    var ratingCount: Int = 0,
    var calories: Int = 0,
    var protein: Float = 0f,
    var carbs: Float = 0f,
    var fat: Float = 0f,
    var fiber: Float = 0f
) {
    fun getIngredientsWithMeasurements(): List<String> {
        val ingredientsWithMeasurements = mutableListOf<String>()
        for (i in ingredients.indices) {
            val ingredient = ingredients[i].trim()
            val measure = measures.getOrElse(i) { "" }.trim()
            if (ingredient.isNotEmpty() && measure.isNotEmpty()) {
                ingredientsWithMeasurements.add("$ingredient - $measure")
            } else if (ingredient.isNotEmpty()) {
                ingredientsWithMeasurements.add(ingredient)
            }
        }
        return ingredientsWithMeasurements
    }
}
