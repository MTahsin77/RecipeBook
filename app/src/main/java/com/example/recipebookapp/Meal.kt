package com.example.recipebookapp

import java.util.UUID

data class Meal(
    val id: String = UUID.randomUUID().toString(),
    var type: MealType = MealType.BREAKFAST,
    var name: String = ""
)

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}
