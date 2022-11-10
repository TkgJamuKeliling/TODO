package com.example.todo.viewmodel

import android.content.Context
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.TodoApp
import com.example.todo.callback.PostListener
import com.example.todo.model.TaskModel
import com.example.todo.util.DateUtil
import com.example.todo.util.DbUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Created By zen
 * 09/11/22
 * 21.31
 */
class MainViewModel: ViewModel(), KoinComponent {
    private val app by inject<TodoApp>()

    private var newTask: String? = null

    private val _etNewDate = MutableLiveData<String?>()
    fun vmNewDate(): LiveData<String?> = _etNewDate
    fun postEtNewDate(l: Long? = null) = _etNewDate.postValue(DateUtil.convertToString(l))

    private val _etNewTask = MutableLiveData<String?>()
    fun vmNewTask(): LiveData<String?> = _etNewTask
    fun postEtNewTask(editable: Editable? = null) {
        editable?.let {
            newTask = "$it"
            return
        }
        newTask = null
    }

    fun postNewTaskData(context: Context, postListener: PostListener) {
        var msg = "Task content cannot empty!"
        var flag = true
        newTask?.let { mTask ->
            _etNewDate.value?.let { mDate ->
                if (DateUtil.isValidDate(mDate)) {
                    viewModelScope.launch {
                        DbUtil(context).saveData(TaskModel(
                            _text = mTask,
                            _date = mDate
                        ))
                    }
                    _etNewTask.postValue(null)
                    postEtNewDate()
                    getAllTaskData()
                    postListener.onSuccess()
                    return
                }
                msg = "Date must be range from July 16 2011 until today!"
                flag = false
            }
            if (flag) {
                msg = "Please select date!"
            }
        }
        postListener.onFailed(msg)
    }

    private val _cbAllCompleteState = MutableLiveData<Boolean>()
    fun vmCbAllCompleteState(): LiveData<Boolean> = _cbAllCompleteState
    private fun postCbAllCompleteState(b: Boolean) = _cbAllCompleteState.postValue(b)

    private val _cbAllCompleteCheck = MutableLiveData<Boolean>()
    fun vmCbAllCompleteCheck(): LiveData<Boolean> = _cbAllCompleteCheck
    private fun postCbAllCompleteCheck(b: Boolean) = _cbAllCompleteCheck.postValue(b)

    fun postAllCompleteChecked(state: Int) {
        viewModelScope.launch {
            DbUtil(app).setAllTaskDone(state)
            getAllTaskData()
        }
    }

    fun getAllTaskData() {
        viewModelScope.launch {
            val getData = async { DbUtil(app).getAllTaskData() }
            val data = getData.await()
            _allTaskData.postValue(data)

            postTvItemLeftTxt("${data.filter { !it._done }.size}")
            postBtnClearState(when {
                data.any { it._done } -> true.also {
                    postBtnClearTxt("${data.filter { it._done }.size}")
                }
                else -> false.also {
                    postBtnClearTxt("0")
                }
            })

            postCbAllCompleteCheck(when {
                data.isEmpty() -> false
                else -> when {
                    data.any { !it._done } -> false
                    else -> true
                }
            })
            var flag = false
            for (taskModel in data) {
                if (!taskModel._done) {
                    flag = true
                    break
                }
            }
            postCbAllCompleteState(flag)
        }
    }

    private val _allTaskData = MutableLiveData<List<TaskModel>>()
    fun vmAllTaskData(): LiveData<List<TaskModel>> = _allTaskData

    fun deleteTaskCompleted() {
        viewModelScope.launch {
            DbUtil(app).deleteTaskCompleted()
            getAllTaskData()
        }
    }

    fun itemOnChecked(taskModel: TaskModel, state: Int) {
        viewModelScope.launch {
            DbUtil(app).updateTask(taskModel, state)
            getAllTaskData()
        }
    }

    private val _tvItemLeftTxt = MutableLiveData<String?>()
    fun vmTvItemLeftTxt(): LiveData<String?> = _tvItemLeftTxt
    private fun postTvItemLeftTxt(s: String? = null) = _tvItemLeftTxt.postValue(s)

    private val _btnClearTxt = MutableLiveData<String?>()
    fun vmBtnClearTxt(): LiveData<String?> = _btnClearTxt
    private fun postBtnClearTxt(s: String? = null) = _btnClearTxt.postValue(s)

    private val _btnClearState = MutableLiveData<Boolean>()
    fun vmBtnClearState(): LiveData<Boolean> = _btnClearState
    private fun postBtnClearState(b: Boolean) = _btnClearState.postValue(b)
}