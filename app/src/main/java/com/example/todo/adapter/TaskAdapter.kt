package com.example.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TaskItemBinding
import com.example.todo.model.TaskModel

/** Created By zen
 * 09/11/22
 * 22.34
 */
class TaskAdapter(private var taskAdapterCallback: TaskAdapterCallback): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    interface TaskAdapterCallback {
        fun onBindAction(viewHolder: ViewHolder, taskModel: TaskModel)
    }

    private val asyncListDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel) = oldItem == newItem
    })

    fun updateListTaskData(list: List<TaskModel>?) = with(asyncListDiffer) {
        submitList(null)
        list?.let {
            submitList(it)
        }
    }

    class ViewHolder(taskItemBinding: TaskItemBinding): RecyclerView.ViewHolder(taskItemBinding.root) {
        val cb = taskItemBinding.cbTask
        val txt = taskItemBinding.mtvTask
        val mDate = taskItemBinding.mtvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        taskAdapterCallback.onBindAction(holder, asyncListDiffer.currentList[position])
    }

    override fun getItemCount() = asyncListDiffer.currentList.size
    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return position
    }
    override fun getItemId(position: Int): Long {
        super.getItemId(position)
        return position.toLong()
    }
}