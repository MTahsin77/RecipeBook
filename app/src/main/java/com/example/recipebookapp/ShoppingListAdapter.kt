package com.example.recipebookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebookapp.databinding.ItemShoppingListBinding

class ShoppingListAdapter(val items:  MutableList<String>, private val onItemRemoved: (String) -> Unit) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textView.text = item
        holder.binding.removeButton.setOnClickListener {
            onItemRemoved(item)
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
        holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Optionally handle checkbox change
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root)
}
