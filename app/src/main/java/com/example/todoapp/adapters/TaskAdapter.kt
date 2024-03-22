package com.example.todoapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entities.Task
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.utils.TaskDiffUtils

class TaskAdapter(private var items:List<Task> = listOf(),
                  private val onClickListener: (position:Int) -> Unit,
                  private val onCheckedListener: (position:Int) -> Unit,
                  private val onRemoveListener: (position:Int) -> Unit
) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.render(items[position])
        holder.itemView.setOnClickListener { onClickListener(holder.adapterPosition) }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { checkbox, isChecked ->
            if (checkbox.isPressed) {
                onCheckedListener(holder.adapterPosition)
            }
        }
        holder.binding.deleteButton.setOnClickListener {
            onRemoveListener(holder.adapterPosition)
        }
    }

    fun updateItems(results: List<Task>) {
        val diffUtils = TaskDiffUtils(items, results)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        items = results
        diffResult.dispatchUpdatesTo(this)
    }
}

class TaskViewHolder(val binding:ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(task: Task) {
        binding.nameTextView.text = task.task
        binding.doneCheckBox.isChecked = task.done
    }

}