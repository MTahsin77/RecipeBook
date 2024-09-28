package com.example.recipebookapp

const val MODEL = "gemini-1.5-flash"
const val INITIAL_INSTRUCTION = """
Generate a recipe using the following ingredients. The recipe should include:
1. A creative name for the dish
2. A list of ingredients with measurements
3. Step-by-step cooking instructions
4. Estimated cooking time
5. Nutritional information (approximate calories, protein, carbs, fat)
6. Any tips or variations

Ingredients:
"""