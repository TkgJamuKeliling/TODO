package com.example.todo.model

/** Created By zen
 * 10/11/22
 * 01.06
 */
data class TaskModel(
    var id: Long? = null,
    var _done: Boolean = false,
    var _text: String? = null,
    var _date: String? = null
)