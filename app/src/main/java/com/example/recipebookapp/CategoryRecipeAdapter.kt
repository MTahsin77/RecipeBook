package com.example.recipebookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CategoryRecipeAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_RECIPE = 1
    }

    fun setItems(newItems: List<Any>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Category -> VIEW_TYPE_TITLE
            is Recipe -> VIEW_TYPE_RECIPE
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
                TitleViewHolder(view)
            }
            VIEW_TYPE_RECIPE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false)
                RecipeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> holder.bind(items[position] as Category)
            is RecipeViewHolder -> holder.bind(items[position] as Recipe)
        }
    }

    override fun getItemCount(): Int = items.size*6

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.category_title)

        fun bind(category: Category) {
            title.text = category.idCategory
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.recipe_title)
        private val image: ImageView = itemView.findViewById(R.id.recipe_image)

        fun bind(recipe: Recipe) {
            title.text = recipe.strMeal
            Picasso.get().load(recipe.strMealThumb).into(image)

            // Handle click on recipe item
            itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailsActivity::class.java)
                intent.putExtra("recipe", recipe.idMeal)
                context.startActivity(intent)
            }
        }
    }

}

