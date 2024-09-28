package com.example.recipebookapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryRecipeAdapter: CategoryRecipeAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        setupDrawerContent(navigationView, toolbar)

        // Set User Name and Email in Navigation Header
        val headerView: View = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.userNameTextView)
        val userEmailTextView: TextView = headerView.findViewById(R.id.userEmailTextView)

        // Update the TextViews with the user's information
        userNameTextView.text = currentUser.displayName ?: "User Name"
        userEmailTextView.text = currentUser.email

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        categoryRecipeAdapter = CategoryRecipeAdapter(this)
        recyclerView.adapter = categoryRecipeAdapter

        // Setup SearchView
        searchView = findViewById(R.id.search_view)
        setupSearchView()

        // Setup Meal Planning Button
        val mealPlanningButton: Button = findViewById(R.id.btn_meal_planning)
        mealPlanningButton.setOnClickListener {
            startMealPlanningActivity()
        }

        // Setup Generate Recipe Button
        val generateRecipeButton: Button = findViewById(R.id.generateRecipeButton)
        generateRecipeButton.setOnClickListener {
            startActivity(Intent(this, RecipeGeneratorActivity::class.java))
        }

        // Fetch Categories and Recipes initially
        fetchCategoriesAndRecipes()
    }

    private fun setupDrawerContent(navigationView: NavigationView, toolbar: Toolbar) {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    fetchCategoriesAndRecipes(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    fetchCategoriesAndRecipes()
                }
                return true
            }
        })
    }

    private fun fetchCategoriesAndRecipes(searchQuery: String = "") {
        val url = "https://www.themealdb.com/api/json/v1/1/categories.php"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                val jsonObject = JSONObject(response)
                val categoriesArray = jsonObject.getJSONArray("categories")
                val items = mutableListOf<Any>()
                for (i in 0 until categoriesArray.length()) {
                    val categoryObject = categoriesArray.getJSONObject(i)
                    val category = Category(
                        categoryObject.getString("strCategory"),
                        categoryObject.getString("strCategoryThumb"),
                        categoryObject.getString("idCategory")
                    )
                    items.add(category)
                    val recipes = fetchRecipes(category.idCategory, searchQuery)
                    items.addAll(recipes)
                }
                withContext(Dispatchers.Main) {
                    if (items.isEmpty()) {
                        Toast.makeText(this@MainActivity, "No recipes found", Toast.LENGTH_SHORT).show()
                    }
                    categoryRecipeAdapter.setItems(items)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to load categories and recipes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun fetchRecipes(categoryName: String, searchQuery: String): List<Recipe> {
        val url = "https://www.themealdb.com/api/json/v1/1/filter.php?c=$categoryName"
        return try {
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val mealsArray = jsonObject.getJSONArray("meals")
            val recipes = mutableListOf<Recipe>()
            for (i in 0 until mealsArray.length()) {
                val mealObject = mealsArray.getJSONObject(i)
                val recipe = Recipe(
                    mealObject.getString("strMeal"),
                    mealObject.getString("strMealThumb"),
                    mealObject.getString("idMeal")
                )
                if (searchQuery.isEmpty() || recipe.strMeal.contains(searchQuery, ignoreCase = true)) {
                    recipes.add(recipe)
                }
            }
            recipes.shuffled().take(5)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Failed to load recipes for $categoryName", Toast.LENGTH_SHORT).show()
            }
            emptyList()
        }
    }

    private fun startMealPlanningActivity() {
        startActivity(Intent(this, MealPlanningActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_recipes -> fetchCategoriesAndRecipes()
            R.id.nav_shopping_list -> {
                startActivity(Intent(this, ShoppingListActivity::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_meal_planning -> {
                startMealPlanningActivity()
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
