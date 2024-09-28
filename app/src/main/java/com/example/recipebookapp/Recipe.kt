package com.example.recipebookapp

data class Recipe(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String,
    var rating: Float = 0f,
    var ratingCount: Int = 0,
    var calories: Int = 0,
    var protein: Float = 0f,
    var carbs: Float = 0f,
    var fat: Float = 0f,
    var fiber: Float = 0f
)