package com.example.todoapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.todoapp.utils.DatabaseManager
import com.example.todoapp.R
import com.example.todoapp.data.Task
import com.example.todoapp.data.providers.TaskDAO

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var task: Task = Task(-1, "Comprar leche", false)

        val taskDAO = TaskDAO(this)

        task = taskDAO.insert(task)

        val taskList = taskDAO.findAll()

        for (task in taskList) {
            Log.i("DATABASE", task.toString())
        }

        var task2: Task? = taskDAO.find(2)

        if (task2 != null) {
            Log.i("DATABASE", task2.toString())

            task2.done = true
            task2.task = "Pagar facturas"

            taskDAO.update(task2)
        }

        task2 = taskDAO.find(2)
        Log.i("DATABASE", task2.toString())

        if (task2 != null) {
            taskDAO.delete(task2)
            task2 = taskDAO.find(2)
            if (task2 != null) {
                Log.i("DATABASE", task2.toString())
            } else {
                Log.i("DATABASE", "La tarea ha sido borrada")
            }
        }


    }
}