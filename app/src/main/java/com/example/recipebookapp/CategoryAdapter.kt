package com.example.recipebookapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val context: Context) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categories: List<Category> = listOf()

    fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    fun updateRecipes(categoryName: String, recipes: List<Recipe>) {
        val categoryIndex = categories.indexOfFirst { it.strCategory == categoryName }
        if (categoryIndex != -1) {
            categories[categoryIndex].recipes = recipes
            notifyItemChanged(categoryIndex)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        private val recipeRecyclerView: RecyclerView = itemView.findViewById(R.id.recipe_recycler_view)
        private val recipeAdapter = RecipeAdapter()

        init {
            recipeRecyclerView.layoutManager = LinearLayoutManager(context)
            recipeRecyclerView.adapter = recipeAdapter
        }

        fun bind(category: Category) {
            categoryTitle.text = category.strCategory
            recipeAdapter.setRecipes(category.recipes)
        }
    }
}
