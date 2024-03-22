package com.example.todoapp.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.adapters.TaskAdapter
import com.example.todoapp.data.entities.Category
import com.example.todoapp.data.entities.Task
import com.example.todoapp.data.providers.CategoryDAO
import com.example.todoapp.data.providers.TaskDAO
import com.example.todoapp.databinding.ActivityTasksBinding
import com.example.todoapp.databinding.AddTaskDialogBinding


class TasksActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "CATEGORY_ID"
    }

    private lateinit var categoryDAO: CategoryDAO
    private lateinit var binding: ActivityTasksBinding
    private lateinit var adapter: TaskAdapter

    private lateinit var taskList: MutableList<Task>
    private lateinit var taskDAO: TaskDAO

    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskDAO = TaskDAO(this)
        categoryDAO = CategoryDAO(this)
        //categoryDAO.insert(Category(-1, "Trabajo", "#FF00FF"))
        //categoryDAO.insert(Category(-1, "Personal", "#AA0088"))
        //categoryDAO.insert(Category(-1, "Compra", "#AAFFBB"))

        val categoryId = intent.getIntExtra(EXTRA_ID, -1)
        category = categoryDAO.find(categoryId)!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = category.name

        initView()
    }

    override fun onResume() {
        super.onResume()

        loadData()
    }

    private fun initView() {
        binding.addTaskButton.setOnClickListener {
            addTask()
        }

        adapter = TaskAdapter(listOf(), {
            onItemClickListener(it)
        }, {
            onItemClickCheckBoxListener(it)
        }, {
            onItemClickRemoveListener(it)
        })

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        configureGestures()
    }

    private fun loadData() {
        taskList = taskDAO.findAllByCategory(category).toMutableList()
        adapter.updateItems(taskList)
    }

    private fun configureGestures() {
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        onItemClickRemoveListener(viewHolder.adapterPosition)
                    } else {
                        onItemClickCheckBoxListener(viewHolder.adapterPosition)
                    }
                }
        })
        gestures.attachToRecyclerView(binding.recyclerView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addTask() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding: AddTaskDialogBinding = AddTaskDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

        /*val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList.map { it.name })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter*/

        dialogBuilder.setTitle(R.string.add_task_title)
        dialogBuilder.setIcon(R.drawable.ic_add_task)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.add_task_button, null)

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()

        // Need to move listener after show dialog to prevent dismiss
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val taskName = binding.taskTextField.editText?.text.toString()
            if (taskName.isNotEmpty()) {
                val task = Task(-1, taskName, false, category)
                taskDAO.insert(task)
                loadData()
                Toast.makeText(this, R.string.add_task_success_message, Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                binding.taskTextField.error = getString(R.string.add_task_empty_error)
            }
        }
    }

    private fun editTask(position: Int) {
        val task: Task = taskList[position]

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding: AddTaskDialogBinding = AddTaskDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

        /*val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList.map { it.name })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter
        binding.categorySpinner.setSelection(categoryList.indexOf(task.category))*/

        binding.taskTextField.editText?.setText(task.task)

        dialogBuilder.setTitle(R.string.edit_task_title)
        dialogBuilder.setIcon(R.drawable.ic_add_task)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.edit_task_button, null)

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()

        // Need to move listener after show dialog to prevent dismiss
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val taskName = binding.taskTextField.editText?.text.toString()
            if (taskName.isNotEmpty()) {
                task.task = taskName
                task.category = category
                taskDAO.update(task)
                adapter.notifyItemChanged(position)
                Toast.makeText(this, R.string.edit_task_success_message, Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                binding.taskTextField.error = getString(R.string.add_task_empty_error)
            }
        }
    }

    private fun onItemClickListener(position:Int) {
        editTask(position)
    }

    private fun onItemClickCheckBoxListener(position:Int) {
        val task: Task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)
        adapter.notifyItemChanged(position)
        loadData()
        //adapter.notifyDataSetChanged()
    }

    private fun onItemClickRemoveListener(position:Int) {
        val task: Task = taskList[position]
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(R.string.delete_task_title)
        dialogBuilder.setMessage(getString(R.string.delete_task_confirm_message, task.task))
        dialogBuilder.setIcon(R.drawable.ic_delete)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.delete_task_button) { dialog, _ ->
            taskDAO.delete(task)

            val newList = taskList.minus(task)
            adapter.updateItems(newList)

            taskList = newList.toMutableList()
            dialog.dismiss()
            Toast.makeText(this, R.string.delete_task_success_message, Toast.LENGTH_SHORT).show()
        }
        dialogBuilder.show()
    }


}