package com.example.todo.dialog

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Created By zen
 * 10/11/22
 * 00.10
 */
class GlobalDialog {
    fun initDialog(
        context: Context,
        title: String,
        msg: String,
        okBtnTxt: String? = context.getString(android.R.string.ok),
        okListener: DialogInterface.OnClickListener,
        cancelBtnTxt: String? = context.getString(android.R.string.cancel),
        cancelListener: DialogInterface.OnClickListener? = null,
        isCancelable: Boolean = false,
        isShowCancelBtn: Boolean = false
    ) = MaterialAlertDialogBuilder(context).apply {
        setTitle(title)
        setMessage(msg)
        setPositiveButton(okBtnTxt, okListener)
        if (isShowCancelBtn) {
            setNegativeButton(cancelBtnTxt, cancelListener)
        }
        setCancelable(isCancelable)
    }
}