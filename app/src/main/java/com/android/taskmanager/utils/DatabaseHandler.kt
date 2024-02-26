package com.android.taskmanager.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.android.taskmanager.models.TodoModel

class DatabaseHandler(context : Context) : SQLiteOpenHelper(context, dbName, null, version) {
    companion object {
        private const val version : Int = 1
        private const val dbName : String = "toDoListDatabase"
        private const val todoTable : String = "todo"
        private const val id : String = "id"
        private const val task : String = "task"
        private const val status : String = "status"
    }

    private val createTodoTable : String =
        "CREATE TABLE $todoTable($id INTEGER PRIMARY KEY AUTOINCREMENT, $task TEXT, $status INTEGER)"

    private lateinit var db : SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        this.db = db!!
        this.db.execSQL(createTodoTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $todoTable")  //Drops older table
        onCreate(db)    //Creates new table
    }

    fun openDatabase() {
        db = writableDatabase
    }

    fun insertTask(todoModelTask : TodoModel){
        val cv = ContentValues()
        cv.put(task, todoModelTask.task)
        cv.put(status, 0)
        db.insert(todoTable, null, cv)
    }

    fun getAllTasks() : List<TodoModel> {
        val taskList: MutableList<TodoModel> = mutableListOf()
        var cursor : Cursor? = null
        
        db.beginTransaction()

        try {
            cursor = db.query(todoTable, null, null, null, null, null, null, null)

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex("id")
                        val taskIdIndex = cursor.getColumnIndex("task")
                        val statusIndex = cursor.getColumnIndex("status")

                        if (idIndex >= 0 && taskIdIndex >= 0 && statusIndex >= 0) {
                            val id = cursor.getInt(idIndex)
                            val task = cursor.getString(taskIdIndex)
                            val status = cursor.getInt(statusIndex)
                            val todoModel = TodoModel(id, status, task)
                            taskList.add(todoModel)
                        }

                    } while (cursor.moveToNext())
                }
            }
        } finally {
            db.endTransaction()
            cursor?.close()
        }
        return taskList
    }

    fun updateStatus(i : Int, stat : Int) {
        val cv = ContentValues()
        cv.put(status, stat)
        db.update(todoTable, cv, "$id = ?", arrayOf(i.toString()))
    }

    fun updateTask(i : Int, t : String) {
        val cv = ContentValues()
        cv.put(task, t)
        db.update(todoTable, cv, "$id = ?", arrayOf(i.toString()))
    }

    fun deleteTask(i : Int) {
         db.delete(todoTable, "$id =?", arrayOf(i.toString()))
    }
}