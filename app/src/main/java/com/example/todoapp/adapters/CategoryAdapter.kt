package com.example.todoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entities.Category
import com.example.todoapp.data.providers.TaskDAO
import com.example.todoapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    context: Context,
    private var items:List<Category> = listOf(),
    private val onClickListener: (position:Int) -> Unit,
    private val onLongClickListener: (position:Int) -> Boolean
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var taskDAO: TaskDAO = TaskDAO(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.render(items[position])
        holder.itemView.setOnClickListener { onClickListener(position) }
        holder.itemView.setOnLongClickListener { onLongClickListener(position) }
    }

    fun updateItems(results: List<Category>) {
        items = results
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun render(category: Category) {
            binding.nameTextView.text = category.name
            binding.numberOfTasksTextView.text = taskDAO.countByCategoryAndDone(category).toString()
            binding.colorView.setBackgroundColor(category.color.toColorInt())
        }
    }
}