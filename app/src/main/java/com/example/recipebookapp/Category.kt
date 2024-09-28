package com.example.recipebookapp

data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    var recipes: List<Recipe> = listOf()
)
