package com.example.recipebookapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebookapp.databinding.ActivityShoppingListBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class ShoppingListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
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

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ShoppingListAdapter(getShoppingList().toMutableList()){ item ->
            removeItemFromShoppingList(item)}
        binding.recyclerView.adapter = adapter

        // Set up Clear Button
        binding.clearButton.setOnClickListener {
            clearShoppingList()
        }
    }

    private fun getShoppingList(): List<String> {
        val sharedPreferences = getSharedPreferences("shopping_list", MODE_PRIVATE)
        return sharedPreferences.getStringSet("items", mutableSetOf())?.toList() ?: emptyList()
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
    private fun removeItemFromShoppingList(item: String) {
        val sharedPreferences = getSharedPreferences("shopping_list", MODE_PRIVATE)
        val items = sharedPreferences.getStringSet("items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        items.remove(item)
        sharedPreferences.edit().putStringSet("items", items).apply()
    }

    private fun clearShoppingList() {
        val sharedPreferences = getSharedPreferences("shopping_list", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        adapter.items.clear()
        adapter.notifyDataSetChanged()
    }
}
