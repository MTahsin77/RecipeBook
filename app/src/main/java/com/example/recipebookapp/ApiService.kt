package com.example.recipebookapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MealApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse
}

object ApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val mealApiService: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}