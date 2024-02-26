package com.android.taskmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.android.taskmanager.MainActivity
import com.android.taskmanager.R
import com.android.taskmanager.models.TodoModel

class TodoAdapter(private val activity: MainActivity) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var todoList: List<TodoModel> = listOf()

    // ViewHolder class definition will go here
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val task : AppCompatCheckBox = view.findViewById(R.id.todo_checkBox)
    }

    // Constructor is defined directly in the class header

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : TodoModel = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = item.status != 0
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun setTasks(todoList : List<TodoModel>){
        this.todoList = todoList
    }
}
