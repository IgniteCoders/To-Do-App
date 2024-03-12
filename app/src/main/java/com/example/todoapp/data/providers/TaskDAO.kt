package com.example.todoapp.data.providers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.todoapp.data.entities.Category
import com.example.todoapp.data.entities.Task
import com.example.todoapp.utils.DatabaseManager

class TaskDAO (val context: Context) {

    private var databaseManager: DatabaseManager = DatabaseManager(context)

    fun insert(task: Task): Task {
        val db = databaseManager.writableDatabase

        val values = ContentValues()
        values.put(Task.COLUMN_NAME_TASK, task.task)
        values.put(Task.COLUMN_NAME_DONE, task.done)
        values.put(Task.COLUMN_NAME_CATEGORY, task.category.id)

        val newRowId = db.insert(Task.TABLE_NAME, null, values)
        Log.i("DATABASE", "New record id: $newRowId")

        db.close()


        task.id = newRowId.toInt()
        return task
    }

    fun update(task: Task) {
        val db = databaseManager.writableDatabase

        val values = ContentValues()
        values.put(Task.COLUMN_NAME_TASK, task.task)
        values.put(Task.COLUMN_NAME_DONE, task.done)
        values.put(Task.COLUMN_NAME_CATEGORY, task.category.id)

        val updatedRows = db.update(Task.TABLE_NAME, values, "${DatabaseManager.COLUMN_NAME_ID} = ${task.id}", null)
        Log.i("DATABASE", "Updated records: $updatedRows")

        db.close()
    }

    fun delete(task: Task) {
        val db = databaseManager.writableDatabase

        val deletedRows = db.delete(Task.TABLE_NAME, "${DatabaseManager.COLUMN_NAME_ID} = ${task.id}", null)
        Log.i("DATABASE", "Deleted rows: $deletedRows")

        db.close()
    }

    @SuppressLint("Range")
    fun find(id: Int): Task? {
        val db = databaseManager.writableDatabase

        val cursor = db.query(
            Task.TABLE_NAME,                         // The table to query
            Task.COLUMN_NAMES,       // The array of columns to return (pass null to get all)
            "${DatabaseManager.COLUMN_NAME_ID} = $id",                        // The columns for the WHERE clause
            null,                    // The values for the WHERE clause
            null,                        // don't group the rows
            null,                         // don't filter by row groups
            null                         // The sort order
        )

        var task: Task? = null

        if (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_NAME_ID))
            val taskName = cursor.getString(cursor.getColumnIndex(Task.COLUMN_NAME_TASK))
            val done = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_DONE)) == 1
            val categoryId = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_CATEGORY))
            //Log.i("DATABASE", "$id -> Task: $taskName, Done: $done")

            val category = CategoryDAO(context).find(categoryId)!!
            task = Task(id, taskName, done, category)
        }

        cursor.close()
        db.close()

        return task
    }

    @SuppressLint("Range")
    fun findAll(): List<Task> {
        val db = databaseManager.writableDatabase

        val cursor = db.query(
            Task.TABLE_NAME,                 // The table to query
            Task.COLUMN_NAMES,     // The array of columns to return (pass null to get all)
            null,                // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            "${Task.COLUMN_NAME_DONE} ASC"               // The sort order
        )

        val list: MutableList<Task> = mutableListOf()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_NAME_ID))
            val taskName = cursor.getString(cursor.getColumnIndex(Task.COLUMN_NAME_TASK))
            val done = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_DONE)) == 1
            val categoryId = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_CATEGORY))
            //Log.i("DATABASE", "$id -> Task: $taskName, Done: $done")

            val category = CategoryDAO(context).find(categoryId)!!
            val task = Task(id, taskName, done, category)
            list.add(task)
        }

        cursor.close()
        db.close()

        return list
    }

    @SuppressLint("Range")
    fun findAllByCategory(category: Category): List<Task> {
        val db = databaseManager.writableDatabase

        val cursor = db.query(
            Task.TABLE_NAME,                 // The table to query
            Task.COLUMN_NAMES,     // The array of columns to return (pass null to get all)
            "${Task.COLUMN_NAME_CATEGORY} = ${category.id}",                // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            "${Task.COLUMN_NAME_DONE} ASC"               // The sort order
        )

        val list: MutableList<Task> = mutableListOf()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_NAME_ID))
            val taskName = cursor.getString(cursor.getColumnIndex(Task.COLUMN_NAME_TASK))
            val done = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_DONE)) == 1
            val categoryId = cursor.getInt(cursor.getColumnIndex(Task.COLUMN_NAME_CATEGORY))
            //Log.i("DATABASE", "$id -> Task: $taskName, Done: $done")

            val category = CategoryDAO(context).find(categoryId)!!
            val task = Task(id, taskName, done, category)
            list.add(task)
        }

        cursor.close()
        db.close()

        return list
    }

    @SuppressLint("Range")
    fun countByCategoryAndDone(category: Category): Int {
        val db = databaseManager.writableDatabase

        val cursor = db.query(
            Task.TABLE_NAME,                 // The table to query
            arrayOf("COUNT(*)"),     // The array of columns to return (pass null to get all)
            "${Task.COLUMN_NAME_CATEGORY} = ${category.id} AND ${Task.COLUMN_NAME_DONE} = false",                // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        var count = -1
        if (cursor.moveToNext()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }

}