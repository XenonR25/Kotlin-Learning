package com.example.assignment1

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.dataClass.Task
import com.example.assignment1.database.AppDatabase
import com.example.assignment1.databinding.DialogTaskBinding
import com.example.assignment1.viewModel.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task) {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize Adapter with click logic
        taskAdapter = TaskAdapter(
            onEditClick = {task -> showTaskDialog(task)},
            onDeleteClick = {task -> viewModel.delete(task)}
        )

        //Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTasks)
        recyclerView.adapter = taskAdapter

        //for adding the task,
        val btnAdd = view.findViewById<View>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            showTaskDialog(null)
        }

        //COLLECT FLOW(Lifecycle aware)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.allTasks.collect {
                    taskList -> taskAdapter.submitList(taskList) //ListAdapter handles the update animation
                }

            }

        }

    }

    private fun showTaskDialog(task: Task?) {
        // 1. Inflate the custom layout using ViewBinding
        val dialogBinding = DialogTaskBinding.inflate(layoutInflater)

        // 2. Pre-fill data if we are EDITING an existing task
        task?.let {
            dialogBinding.etTitle.setText(it.title)
            dialogBinding.etDescription.setText(it.description)
        }

        // 3. Build the Dialog
        // We use a MaterialAlertDialogBuilder but we will customize the window
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root) // This is your modern XML layout
            .setCancelable(true)
            .create()

        // 4. THE MAGIC: Make the system dialog background transparent
        // This allows the 28dp rounded corners of your XML to be visible
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        // 5. Logic for the "Save" and "Cancel" buttons
        // If you have buttons inside your XML (recommended for iOS look), use these:

        // Example: Using buttons defined in your dialog_task.xml
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val desc = dialogBinding.etDescription.text.toString().trim()

            if (title.isNotEmpty()) {
                if (task == null) {
                    // ADD NEW
                    viewModel.insert(Task(title = title, description = desc))
                } else {
                    // UPDATE EXISTING
                    viewModel.update(task.copy(title = title, description = desc))
                }
                dialog.dismiss()
            } else {
                // Shake effect or simple error
                dialogBinding.etTitle.error = "Title is required"
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}