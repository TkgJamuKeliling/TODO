package com.example.todo.callback

/** Created By zen
 * 10/11/22
 * 00.15
 */
interface PostListener {
    fun onFailed(msg: String)
    fun onSuccess()
}