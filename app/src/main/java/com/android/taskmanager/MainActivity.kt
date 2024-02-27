package com.android.taskmanager

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.taskmanager.adapter.TodoAdapter
import com.android.taskmanager.databinding.ActivityMainBinding
import com.android.taskmanager.models.TodoModel
import com.android.taskmanager.utils.DatabaseHandler
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var tasksAdapter : TodoAdapter
    private lateinit var db : DatabaseHandler
    private var taskList =  mutableListOf<TodoModel>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivitiesIfAvailable(application)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        db = DatabaseHandler(this)
        db.openDatabase()

        binding.tasksRecyclerview.layoutManager = LinearLayoutManager(this)
        tasksAdapter = TodoAdapter(this, db)
        binding.tasksRecyclerview.adapter = tasksAdapter

        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)

        binding.fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.tag)
        }

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(this, tasksAdapter))
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerview)


//        val task = TodoModel(
//            id = 1,
//            status = 0,
//            task = "This is a test task"
//        )

//        taskList.add(task)

//        tasksAdapter.setTasks(todoList = taskList)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun handleDialogClose(dialog: DialogInterface) {
        super.handleDialogClose(dialog)

        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.run {
            setTasks(taskList)
            notifyDataSetChanged()
        }
    }
}