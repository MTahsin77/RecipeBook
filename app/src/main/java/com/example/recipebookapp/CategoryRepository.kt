package com.example.recipebookapp

class CategoryRepository(private val apiService: MealApiService) {
    suspend fun getCategories() = apiService.getCategories()
}
