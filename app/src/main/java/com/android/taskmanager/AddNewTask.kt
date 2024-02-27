package com.android.taskmanager

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.android.taskmanager.models.TodoModel
import com.android.taskmanager.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddNewTask : BottomSheetDialogFragment() {

    companion object {
        const val tag = "ActionBottomDialog"

        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }

    private lateinit var newTaskText: EditText
    private lateinit var newTaskSaveButton: Button
    private lateinit var db: DatabaseHandler

    private var isUpdate: Boolean = false
    private var taskText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newTaskText = view.findViewById(R.id.new_task_text)
        newTaskSaveButton = view.findViewById(R.id.new_task_button)

        var isUpdate = false

        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            newTaskText.setText(task)

            if ((task?.length ?: 0) > 0) {
                context?.let {
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(it, R.color.colorPrimaryDark))
                }
            }
        }

        db = DatabaseHandler(requireActivity())
        db.openDatabase()

        newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    newTaskSaveButton.isEnabled = false
                    newTaskSaveButton.setTextColor(Color.GRAY)
                } else {
                    newTaskSaveButton.isEnabled = true
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val finalIsUpdate = isUpdate
        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if(text.isNotEmpty()) {
                if (finalIsUpdate) {
                    bundle?.getInt("id")?.let { it1 -> db.updateTask(it1, text) }
                } else {
                    val task = TodoModel().apply {
                        task = text
                        status = 0
                    }
                    db.insertTask(task)
                }
                dismiss()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isUpdate", isUpdate)
        outState.putString("taskText", newTaskText.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            isUpdate = it.getBoolean("isUpdate", false)
            taskText = it.getString("taskText", "")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        val activity = requireActivity()
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }
}
