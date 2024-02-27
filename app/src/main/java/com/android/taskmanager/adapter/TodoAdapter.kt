package com.android.taskmanager.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.android.taskmanager.AddNewTask
import com.android.taskmanager.MainActivity
import com.android.taskmanager.R
import com.android.taskmanager.models.TodoModel
import com.android.taskmanager.utils.DatabaseHandler

class TodoAdapter(private val activity: MainActivity, private val db : DatabaseHandler) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var todoList: MutableList<TodoModel> = mutableListOf()

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
        db.openDatabase()

        val item : TodoModel = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = item.status != 0
        holder.task.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(todoList : MutableList<TodoModel>){
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun editItem(position : Int) {
        val item : TodoModel = todoList[position]
        val bundle : Bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
        }

        val fragment : AddNewTask = AddNewTask().apply {
            arguments = bundle
        }
        fragment.show(activity.supportFragmentManager, AddNewTask.tag)
    }

    fun deleteItem(position : Int) {
        val item : TodoModel = todoList[position]
        db.deleteTask(item.id)

        todoList.removeAt(position)

        notifyItemRemoved(position)
    }
}
