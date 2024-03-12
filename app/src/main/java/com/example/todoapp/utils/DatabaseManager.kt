package com.example.todoapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoapp.data.entities.Category
import com.example.todoapp.data.entities.Task
import com.example.todoapp.data.providers.CategoryDAO

class DatabaseManager(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "to_do_app.db"
        const val DATABASE_VERSION = 1
        const val COLUMN_NAME_ID = "id"

        private const val SQL_CREATE_TABLE_TASK =
            "CREATE TABLE ${Task.TABLE_NAME} (" +
                    "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Task.COLUMN_NAME_TASK} TEXT," +
                    "${Task.COLUMN_NAME_CATEGORY} INTEGER," +
                    "${Task.COLUMN_NAME_DONE} BOOLEAN, " +
                    "CONSTRAINT fk_category " +
                    "FOREIGN KEY(${Task.COLUMN_NAME_CATEGORY}) " +
                    "REFERENCES ${Category.TABLE_NAME}($COLUMN_NAME_ID) ON DELETE CASCADE)"

        private const val SQL_DELETE_TABLE_TASK = "DROP TABLE IF EXISTS ${Task.TABLE_NAME}"

        private const val SQL_CREATE_TABLE_CATEGORY =
            "CREATE TABLE ${Category.TABLE_NAME} (" +
                    "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Category.COLUMN_NAME_CATEGORY} TEXT," +
                    "${Category.COLUMN_NAME_COLOR} TEXT)"

        private const val SQL_DELETE_TABLE_CATEGORY = "DROP TABLE IF EXISTS ${Category.TABLE_NAME}"
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;");
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_CATEGORY)
        db.execSQL(SQL_CREATE_TABLE_TASK)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        destroyDatabase(db)
        onCreate(db)
    }

    private fun destroyDatabase (db: SQLiteDatabase) {
        db.execSQL(SQL_DELETE_TABLE_TASK)
        db.execSQL(SQL_DELETE_TABLE_CATEGORY)
    }
}