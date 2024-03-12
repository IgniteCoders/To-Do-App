package com.example.todoapp.data.entities

import com.example.todoapp.utils.DatabaseManager

class Category (var id: Int, var name: String, var color: String) {

    //var tasks: List<Task>? = null

    companion object {
        const val TABLE_NAME = "Categories"
        const val COLUMN_NAME_CATEGORY = "name"
        const val COLUMN_NAME_COLOR = "color"
        val COLUMN_NAMES = arrayOf(
            DatabaseManager.COLUMN_NAME_ID,
            COLUMN_NAME_CATEGORY,
            COLUMN_NAME_COLOR
        )
    }

    override fun toString(): String {
        return "$id -> Task: $name - $color"
    }

    override fun equals(other: Any?): Boolean{
        if(other is Category){
            return id == other.id
        }
        return false;
    }
}