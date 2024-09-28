package com.example.recipebookapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class RecipeGeneratorActivity : AppCompatActivity() {

    private lateinit var ingredientsEditText: EditText
    private lateinit var generateButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_generator)

        ingredientsEditText = findViewById(R.id.ingredientsEditText)
        generateButton = findViewById(R.id.generateButton)
        resultTextView = findViewById(R.id.resultTextView)

        generateButton.setOnClickListener {
            val ingredients = ingredientsEditText.text.toString()
            if (ingredients.isNotEmpty()) {
                generateRecipe(ingredients)
            }
        }
    }

    private fun generateRecipe(ingredients: String) {
        val model = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        lifecycleScope.launch {
            try {
                val prompt = "Generate a recipe using these ingredients: $ingredients"
                val response = model.generateContent(prompt)
                resultTextView.text = response.text
            } catch (e: Exception) {
                resultTextView.text = "Error: ${e.message}"
            }
        }
    }
}