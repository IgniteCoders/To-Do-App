package com.example.todoapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.adapters.TaskAdapter
import com.example.todoapp.data.entities.Task
import com.example.todoapp.data.providers.TaskDAO
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.databinding.AddDialogBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>
    private lateinit var taskDAO: TaskDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskDAO = TaskDAO(this)

        initView()

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
    }

    private fun loadData() {
        taskList = taskDAO.findAll().toMutableList()
        adapter.updateItems(taskList)
    }

    private fun addTask() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding: AddDialogBinding = AddDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

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
                val task: Task = Task(-1, taskName, false)
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
        val binding: AddDialogBinding = AddDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

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
        //adapter.notifyItemChanged(position)
        //adapter.notifyDataSetChanged()
    }

    private fun onItemClickRemoveListener(position:Int) {
        val task: Task = taskList[position]
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(R.string.delete_task_title)
        dialogBuilder.setMessage(getString(R.string.delete_task_confirm_message, task.task))
        dialogBuilder.setIcon(R.drawable.ic_delete)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.delete_task_button) { dialog, _ ->
            taskDAO.delete(task)
            taskList.removeAt(position)
            //adapter.notifyItemRemoved(position)
            adapter.notifyDataSetChanged()
            dialog.dismiss()
            Toast.makeText(this, R.string.delete_task_success_message, Toast.LENGTH_SHORT).show()
        }
        dialogBuilder.show()
    }
}