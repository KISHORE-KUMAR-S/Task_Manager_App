package com.android.taskmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.taskmanager.adapter.TodoAdapter
import com.android.taskmanager.databinding.ActivityMainBinding
import com.android.taskmanager.models.TodoModel
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var tasksAdapter : TodoAdapter
    private var taskList =  mutableListOf<TodoModel>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivitiesIfAvailable(application)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        binding.tasksRecyclerview.layoutManager = LinearLayoutManager(this)
        tasksAdapter = TodoAdapter(this)
        binding.tasksRecyclerview.adapter = tasksAdapter

        val task = TodoModel(
            id = 1,
            status = 0,
            task = "This is a test task"
        )

        taskList.add(task)
        taskList.add(task)
        taskList.add(task)
        taskList.add(task)
        taskList.add(task)

        tasksAdapter.setTasks(todoList = taskList)
    }
}