package com.example.recipebookapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.squareup.picasso.Picasso
import com.example.recipebookapp.databinding.ActivityRecipeDetailsBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class RecipeDetailsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var drawerLayout: DrawerLayout
    private var recipe: RecipeDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        val currentUser = auth.currentUser
        val inflater = LayoutInflater.from(this)
        val mainMenuLayout = inflater.inflate(R.layout.nav_header, null) as LinearLayout

        // Find the TextView within the inflated layout
        val userEmailTextView = mainMenuLayout.findViewById<TextView>(R.id.userEmailTextView)

        // Load current user info
        if (currentUser != null) {
            userEmailTextView.text = currentUser.email
        }

        // Retrieve recipe ID passed from MainActivity
        val recipeId = intent.getStringExtra("recipe")

        // Setup Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Fetch recipe details using CoroutineScope
        GlobalScope.launch(Dispatchers.IO) {
            recipe = fetchRecipeDetails(recipeId)
            launch(Dispatchers.Main) {
                displayRecipeDetails(recipe)
                setupRatingBar()
                loadNutritionInfo()
            }
        }

        // Add to Shopping List Button
        binding.btnAddToShoppingList.setOnClickListener {
            val ingredients = binding.recipeIngredients.text.toString().split("\n")
            addToShoppingList(ingredients)
            Toast.makeText(this, "Added to shopping list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRatingBar() {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateRecipeRating(rating)
        }
    }

    private fun updateRecipeRating(newRating: Float) {
        recipe?.let {
            it.ratingCount++
            it.rating = (it.rating * (it.ratingCount - 1) + newRating) / it.ratingCount
            binding.ratingBar.rating = it.rating
            binding.ratingCount.text = "(${it.ratingCount} ratings)"
            // TODO: Update rating in database or API
        }
    }

    private fun loadNutritionInfo() {
        recipe?.let {
            it.calories = 300
            it.protein = 10f
            it.carbs = 40f
            it.fat = 15f
            it.fiber = 5f
            updateNutritionInfoUI()
        }
    }

    private fun updateNutritionInfoUI() {
        recipe?.let {
            binding.caloriesValue.text = "${it.calories} kcal"
            binding.proteinValue.text = "${it.protein}g"
            binding.carbsValue.text = "${it.carbs}g"
            binding.fatValue.text = "${it.fat}g"
            binding.fiberValue.text = "${it.fiber}g"
        }
    }

    private fun fetchRecipeDetails(recipeId: String?): RecipeDetails? {
        recipeId ?: return null
        val url = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=$recipeId"
        return try {
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val mealsArray = jsonObject.getJSONArray("meals")
            if (mealsArray.length() > 0) {
                val mealObject = mealsArray.getJSONObject(0)
                RecipeDetails(
                    mealObject.getString("idMeal"),
                    mealObject.getString("strMeal"),
                    mealObject.getString("strCategory"),
                    mealObject.getString("strArea"),
                    mealObject.getString("strInstructions"),
                    mealObject.getString("strMealThumb"),
                    extractIngredients(mealObject),
                    extractMeasures(mealObject),
                    mealObject.getString("strYoutube")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractIngredients(mealObject: JSONObject): List<String> {
        val ingredients = mutableListOf<String>()
        for (i in 1..20) {
            val ingredient = mealObject.getString("strIngredient$i").trim()
            if (ingredient.isNotEmpty()) {
                ingredients.add(ingredient)
            }
        }
        return ingredients
    }

    private fun extractMeasures(mealObject: JSONObject): List<String> {
        val measures = mutableListOf<String>()
        for (i in 1..20) {
            val measure = mealObject.getString("strMeasure$i").trim()
            if (measure.isNotEmpty()) {
                measures.add(measure)
            }
        }
        return measures
    }

    private fun displayRecipeDetails(recipe: RecipeDetails?) {
        recipe?.let {
            supportActionBar?.title = it.strMeal
            Picasso.get().load(it.strMealThumb).into(binding.recipeImage)
            binding.recipeTitle.text = it.strMeal
            binding.recipeIngredients.text = it.getIngredientsWithMeasurements().joinToString("\n")
            binding.recipeInstructions.text = HtmlCompat.fromHtml(it.strInstructions, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.ratingBar.rating = it.rating
            binding.ratingCount.text = "(${it.ratingCount} ratings)"
        }
    }

    private fun addToShoppingList(ingredients: List<String>) {
        val sharedPreferences = getSharedPreferences("shopping_list", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val existingItems = sharedPreferences.getStringSet("items", mutableSetOf()) ?: mutableSetOf()

        existingItems.addAll(ingredients)
        editor.putStringSet("items", existingItems)
        editor.apply()

        Toast.makeText(this, "Ingredients added to shopping list", Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_recipes -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.nav_shopping_list -> {
                startActivity(Intent(this, ShoppingListActivity::class.java))
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}