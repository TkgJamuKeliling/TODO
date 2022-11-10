package com.example.todo.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todo.model.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/** Created By zen
 * 10/11/22
 * 00.37
 */
class DbUtil(context: Context): SQLiteOpenHelper(context, "taskDb", null, 1) {
    companion object {
        const val tableName = "task"
        const val key_id = "_id"
        const val key_text = "_text"
        const val key_date = "_date"
        const val key_done = "_done"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE ${tableName}(${key_id} INTEGER PRIMARY KEY,${key_text} TEXT,${key_date} TEXT, $key_done BIT)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS $tableName"
        db?.execSQL(sql)
        onCreate(db)
    }

    suspend fun saveData(taskModel: TaskModel) {
        withContext(Dispatchers.Default) {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(key_text, taskModel._text)
                put(key_date, taskModel._date)
                put(key_done, taskModel._done)
            }
            db?.let {
                it.insert(tableName, null, contentValues)
                it.close()
            }
        }
    }

    suspend fun getAllTaskData(): List<TaskModel> {
        val listTaskModel = mutableListOf<TaskModel>()
        withContext(Dispatchers.Default) {
            val sql = "SELECT * FROM $tableName"

            val db = writableDatabase
            db?.let {
                val cursor = async { it.rawQuery(sql, null) }
                cursor.await()?.let { c ->
                    if (c.moveToFirst()) {
                        do {
                            val taskModel = TaskModel(
                                id = c.getLong(0),
                                _text = c.getString(1),
                                _date = c.getString(2),
                                _done = c.getInt(3) > 0
                            )
                            listTaskModel.add(taskModel)
                        } while (c.moveToNext())
                    }
                    c.close()
                }
                it.close()
            }
        }
        return listTaskModel
    }

    suspend fun setAllTaskDone(state: Int) {
        withContext(Dispatchers.Default) {
            var db: SQLiteDatabase? = null
            for (taskModel in getAllTaskData()) {
                db = writableDatabase
                db?.let {
                    val contentValues = ContentValues().apply {
                        put(key_done, state)
                    }
                    it.update(tableName, contentValues, null, null)
                }
            }
            db?.close()
        }
    }

    suspend fun deleteTaskCompleted() {
        withContext(Dispatchers.Default) {
            val db = writableDatabase
            db?.let {
                val whereClause = "${key_done}=?"
                val whereArgs = arrayOf("1")

                it.delete(tableName, whereClause, whereArgs)
                it.close()
            }
        }
    }

    suspend fun updateTask(taskModel: TaskModel, state: Int) {
        withContext(Dispatchers.Default) {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(key_done, state)
            }
            val whereClause = "$key_id=?"
            val whereArgs = arrayOf("${taskModel.id}")
            db?.let {
                it.update(tableName, contentValues, whereClause, whereArgs)
                it.close()
            }
        }
    }
}