package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var db: DatabaseHelper = DatabaseHelper(this)
        //db.createTask()
        db.readTasks()
        db.updateTask()
        db.readTasks()
    }
}