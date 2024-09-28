package com.example.recipebookapp

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebookapp.databinding.ActivityMealPlanningBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MealPlanningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealPlanningBinding
    private lateinit var adapter: MealPlanAdapter
    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupDatePicker()
        setupSaveButton()
        
        loadMealPlan() // Load initial meal plan
    }

    private fun setupRecyclerView() {
        adapter = MealPlanAdapter()
        binding.recyclerViewMealPlan.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMealPlan.adapter = adapter

        binding.btnAddMeal.setOnClickListener {
            val newMeal = Meal()
            val currentList = adapter.currentList.toMutableList()
            currentList.add(newMeal)
            adapter.submitList(currentList)
        }
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    updateDateDisplay()
                    loadMealPlan()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = dateFormat.format(selectedDate.time)
    }

    private fun setupSaveButton() {
        binding.btnSaveMealPlan.setOnClickListener {
            saveMealPlan()
        }
    }

    private fun saveMealPlan() {
        val mealPlan = adapter.currentList
        val dateStr = getDateString()
        val sharedPrefs = getSharedPreferences("MealPlans", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        
        val json = Gson().toJson(mealPlan)
        editor.putString(dateStr, json)
        editor.apply()

        Toast.makeText(this, "Meal plan saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun loadMealPlan() {
        val dateStr = getDateString()
        val sharedPrefs = getSharedPreferences("MealPlans", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(dateStr, null)

        if (json != null) {
            val type = object : TypeToken<List<Meal>>() {}.type
            val mealPlan = Gson().fromJson<List<Meal>>(json, type)
            adapter.submitList(mealPlan)
            binding.tvNoMeals.visibility = View.GONE
        } else {
            adapter.submitList(emptyList())
            binding.tvNoMeals.visibility = View.VISIBLE
        }
    }

    private fun getDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
    }
}
