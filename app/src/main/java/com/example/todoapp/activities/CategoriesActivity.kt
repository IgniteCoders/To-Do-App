package com.example.todoapp.activities

import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.adapters.CategoryAdapter
import com.example.todoapp.data.entities.Category
import com.example.todoapp.data.providers.CategoryDAO
import com.example.todoapp.databinding.ActivityCategoriesBinding
import com.example.todoapp.databinding.AddCategoryDialogBinding


class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding

    private lateinit var adapter: CategoryAdapter
    private lateinit var categoryList: MutableList<Category>
    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.categories_list)

        categoryDAO = CategoryDAO(this)
        categoryList = categoryDAO.findAll().toMutableList()

        initView()
    }

    override fun onResume() {
        super.onResume()

        loadData()
    }

    private fun initView() {
        binding.addCategoryButton.setOnClickListener {
            addCategory()
        }

        adapter = CategoryAdapter(this, categoryList, {
            onItemClickListener(it)
        }, {
            editCategory(it)
            return@CategoryAdapter true
        })

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadData() {
        categoryList = categoryDAO.findAll().toMutableList()
        adapter.updateItems(categoryList)

        binding.emptyView.visibility = if (categoryList.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_category -> {
                addCategory()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onItemClickListener(position:Int) {
        val category: Category = categoryList[position]
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_ID, category.id)
        startActivity(intent)
    }

    private fun addCategory() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding: AddCategoryDialogBinding = AddCategoryDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

        dialogBuilder.setTitle(R.string.add_category_title)
        dialogBuilder.setIcon(R.drawable.ic_add_task)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.add_category_button, null)

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()

        // Need to move listener after show dialog to prevent dismiss
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val categoryName = binding.taskTextField.editText?.text.toString()
            if (categoryName.isNotEmpty()) {
                val category = Category(-1, categoryName, "#000000")
                categoryDAO.insert(category)
                loadData()
                Toast.makeText(this, R.string.add_category_success_message, Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                binding.taskTextField.error = getString(R.string.add_category_empty_error)
            }
        }
    }

    private fun editCategory(position: Int) {
        val category: Category = categoryList[position]

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding: AddCategoryDialogBinding = AddCategoryDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(binding.root)

        binding.taskTextField.editText?.setText(category.name)

        dialogBuilder.setTitle(R.string.edit_category_title)
        dialogBuilder.setIcon(R.drawable.ic_add_task)
        dialogBuilder.setNeutralButton(R.string.delete_category_button) { dialog, _ ->
            deleteCategory(category)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.edit_category_button, null)

        val alertDialog: AlertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener(OnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getColor(R.color.negative_red))
        })

        alertDialog.show()

        // Need to move listener after show dialog to prevent dismiss
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val categoryName = binding.taskTextField.editText?.text.toString()
            if (categoryName.isNotEmpty()) {
                category.name = categoryName
                category.color = "#000000"
                categoryDAO.update(category)
                adapter.notifyItemChanged(position)
                Toast.makeText(this, R.string.edit_category_success_message, Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                binding.taskTextField.error = getString(R.string.add_category_empty_error)
            }
        }
    }

    private fun deleteCategory(category: Category) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(R.string.delete_category_title)
        dialogBuilder.setMessage(getString(R.string.delete_category_confirm_message, category.name))
        dialogBuilder.setIcon(R.drawable.ic_delete)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setPositiveButton(R.string.delete_category_button) { dialog, _ ->
            categoryDAO.delete(category)
            loadData()
            //categoryList.remove(category)
            //adapter.notifyDataSetChanged()
            //adapter.notifyItemRemoved(position)
            dialog.dismiss()
            Toast.makeText(this, R.string.delete_category_success_message, Toast.LENGTH_SHORT).show()
        }

        val alertDialog: AlertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener(OnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.negative_red))
        })

        alertDialog.show()
    }
}