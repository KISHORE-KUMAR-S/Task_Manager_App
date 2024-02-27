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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.taskmanager.models.TodoModel
import com.android.taskmanager.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddNewTaskViewModel : ViewModel() {
    var isUpdate: Boolean = false
    var taskText: String = ""
}

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
    private lateinit var viewModel: AddNewTaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            insets.isVisible(WindowInsetsCompat.Type.ime())
            insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            insets
        }

        viewModel = ViewModelProvider(this)[AddNewTaskViewModel::class.java]

        newTaskText = view.findViewById(R.id.new_task_text)
        newTaskSaveButton = view.findViewById(R.id.new_task_button)

        if (viewModel.isUpdate) {
            newTaskText.setText(viewModel.taskText)

            if (viewModel.taskText.isNotEmpty()) {
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

        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if(text.isNotEmpty()) {
                if (viewModel.isUpdate) {
                    arguments?.getInt("id")?.let { id -> db.updateTask(id, text) }
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

    override fun onResume() {
        super.onResume()
        // Check if another instance of the dialog is already added, if so, dismiss it
        val existingDialog = parentFragmentManager.findFragmentByTag(tag)
        if (existingDialog != null && existingDialog != this) {
            (existingDialog as DialogFragment).dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = requireActivity()
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }
}
