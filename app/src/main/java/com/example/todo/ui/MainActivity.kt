package com.example.todo.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todo.R
import com.example.todo.adapter.TaskAdapter
import com.example.todo.callback.PostListener
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.dialog.GlobalDialog
import com.example.todo.model.TaskModel
import com.example.todo.viewmodel.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/** Created By zen
 * 09/11/22
 * 21.26
 */
class MainActivity: AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    val mainViewModel by viewModel<MainViewModel>()
    val globalDialog by inject<GlobalDialog>()
    var mDialog: AlertDialog? = null

    private val taskAdapter = TaskAdapter(object : TaskAdapter.TaskAdapterCallback {
        override fun onBindAction(viewHolder: TaskAdapter.ViewHolder, taskModel: TaskModel) {
            with(viewHolder) {
                txt.text = taskModel._text
                with(cb) {
                    isChecked = taskModel._done
                    addOnCheckedStateChangedListener { checkBox, state ->
                        if (checkBox.isPressed) {
                            mainViewModel.itemOnChecked(taskModel, state)
                        }
                    }
                }
                mDate.text = taskModel._date
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initView()
        initViewModel()
    }

    private fun initView() {
        with(activityMainBinding) {
            with(newTask) {
                etNewTask.doAfterTextChanged {
                    mainViewModel.postEtNewTask(it)
                }
                btnDate.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.select_date))
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                    with(datePicker) {
                        addOnPositiveButtonClickListener {
                            mainViewModel.postEtNewDate(it)
                        }
                        show(supportFragmentManager, "dateTask")
                    }
                }
                btnPost.setOnClickListener {
                    mainViewModel.postNewTaskData(this@MainActivity, object : PostListener {
                        override fun onFailed(msg: String) {
                            closeDialog()
                            mDialog = globalDialog.initDialog(
                                context = this@MainActivity,
                                title = "INFO",
                                msg = msg,
                                okListener = { _, _ ->
                                    closeDialog()
                                }
                            ).create()
                            showTheDialog()
                        }

                        override fun onSuccess() {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "New task saved!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }

            cbAllComplete.addOnCheckedStateChangedListener { checkBox, state ->
                if (checkBox.isPressed) {
                    mainViewModel.postAllCompleteChecked(state)
                }
            }

            with(rcvTask) {
                adapter = taskAdapter
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@MainActivity, 1)
                itemAnimator = null
            }

            btnDelete.setOnClickListener {
                mDialog = globalDialog.initDialog(
                    context = this@MainActivity,
                    title = "CONFIRMATION",
                    msg = "Delete completed item?",
                    okBtnTxt = "Yes",
                    okListener = { _, _ ->
                        closeDialog()
                        mainViewModel.deleteTaskCompleted()
                    },
                    isShowCancelBtn = true,
                    cancelListener = { _, _ ->
                        closeDialog()
                    }
                ).create()
                showTheDialog()
            }
        }
    }

    private fun showTheDialog() {
        if (!isFinishing && !isDestroyed) {
            runOnUiThread {
                mDialog?.show()
            }
        }
    }

    private fun closeDialog() {
        if (!isFinishing && !isDestroyed) {
            runOnUiThread {
                mDialog?.dismiss()
            }
            mDialog = null
        }
    }

    private fun initViewModel() {
        with(mainViewModel) {
            vmNewTask().observe(this@MainActivity) {
                with(activityMainBinding.newTask.etNewTask) {
                    when (it) {
                        null -> text?.clear()
                        else -> setText(it)
                    }
                }
            }

            vmNewDate().observe(this@MainActivity) {
                activityMainBinding.newTask.btnDate.text = when (it) {
                    null -> getString(R.string.date_hint)
                    else -> it
                }
            }

            vmCbAllCompleteState().observe(this@MainActivity) {
                activityMainBinding.cbAllComplete.isEnabled = it
            }

            vmCbAllCompleteCheck().observe(this@MainActivity) {
                activityMainBinding.cbAllComplete.isChecked = it
            }

            vmAllTaskData().observe(this@MainActivity) {
                taskAdapter.updateListTaskData(it.sortedBy { model ->
                    model._date
                })
            }

            vmTvItemLeftTxt().observe(this@MainActivity) {
                it?.let { s ->
                    activityMainBinding.mtvItemLeft.text = getString(R.string.items_left, s)
                }
            }

            vmBtnClearState().observe(this@MainActivity) {
                it?.let { b ->
                    activityMainBinding.btnDelete.isEnabled = b
                }
            }

            vmBtnClearTxt().observe(this@MainActivity) {
                it?.let { s ->
                    activityMainBinding.btnDelete.text = getString(R.string.delete_btn, s)
                }
            }

            getAllTaskData()
        }
    }
}