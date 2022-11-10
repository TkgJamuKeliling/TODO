package com.example.todo.util

import java.text.SimpleDateFormat
import java.util.*

/** Created By zen
 * 09/11/22
 * 23.49
 */
object DateUtil {
    private fun takeSimpleDateFormat() = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).also {
        it.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convertToString(l: Long?): String? {
        var s: String? = null
        l?.let {
            val date = Date(it)
            s = takeSimpleDateFormat().format(date)
        }
        return s
    }

    fun isValidDate(newDate: String): Boolean {
        var result = true
        val minDate = takeSimpleDateFormat().parse("16/07/2011")
        val maxDate = Date()
        minDate?.let {
            takeSimpleDateFormat().parse(newDate)?.let { nDate ->
                result = minDate.before(nDate) && nDate.before(maxDate)
            }
        }
        return result
    }
}