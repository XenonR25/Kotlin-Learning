package com.example.assignment1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.dataClass.Task
import com.example.assignment1.databinding.ItemTaskBinding

class TaskAdapter(
    private val onEditClick : (Task) -> Unit,
    private val onDeleteClick : (Task) -> Unit
): ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallBack) { //I am an adapter that shows Task items using TaskViewHolder.

    //ViewHolder:Will hold the references to the views in the row
    inner class TaskViewHolder(private val binding : ItemTaskBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(task : Task){
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description

            binding.btnEdit.setOnClickListener { onEditClick(task) }
            binding.btnDelete.setOnClickListener { onDeleteClick(task) }
        }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
    val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TaskViewHolder(binding)
     }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    //2.DiffUtil: The logic that checks if the new list is different from the old ones
    companion object DiffCallBack : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
           oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
           oldItem == newItem
    }
}