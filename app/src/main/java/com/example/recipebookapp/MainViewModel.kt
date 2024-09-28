package com.example.recipebookapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    val categories: LiveData<List<Category>>

    init {
        val apiService = ApiClient.mealApiService
        repository = CategoryRepository(apiService)
        categories = liveData {
            val data = repository.getCategories().categories
            emit(data)
        }
    }
}
