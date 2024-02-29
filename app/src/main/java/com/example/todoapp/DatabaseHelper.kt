package com.example.todoapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "to_do_app.db"
        const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE =
            "CREATE TABLE Task (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "task TEXT," +
            "done BOOLEAN)"

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS Task"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        destroyDatabase(db)
        onCreate(db)
    }

    private fun destroyDatabase (db: SQLiteDatabase) {
        db.execSQL(SQL_DELETE_TABLE)
    }

    fun createTask () {
        val db = writableDatabase
        //db.execSQL("INSERT INTO Task (task, done) VALUES ('Comprar leche', false)")

        var values = ContentValues()
        values.put("task", "Comprar leche")
        values.put("done", false)

        var newRowId = db.insert("Task", null, values)
        Log.i("DATABASE", "New record id: $newRowId")

        values = ContentValues()
        values.put("task", "Limpiar el coche")
        values.put("done", false)

        newRowId = db.insert("Task", null, values)
        Log.i("DATABASE", "New record id: $newRowId")
    }

    @SuppressLint("Range")
    fun readTasks () {
        val db = readableDatabase
        val cursor = db.query(
            "Task",                 // The table to query
            arrayOf("id", "task", "done"),     // The array of columns to return (pass null to get all)
            null,                // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            "done DESC"               // The sort order
        )

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val task = cursor.getString(cursor.getColumnIndex("task"))
            val done = cursor.getInt(cursor.getColumnIndex("done")) == 1
            Log.i("DATABASE", "$id -> Task: $task, Done: $done")
        }
        cursor.close()
    }

    fun updateTask() {
        val db = writableDatabase
        //db.execSQL("INSERT INTO Task (task, done) VALUES ('Comprar leche', false)")

        var values = ContentValues()
        values.put("done", true)

        var updatedRows = db.update("Task", values, "id = ? OR id = ?", arrayOf("1", "3"))
        Log.i("DATABASE", "Updated records: $updatedRows")
    }
}