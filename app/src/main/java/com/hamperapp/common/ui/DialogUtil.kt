package com.hamperapp.common.ui

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.hamperapp.R

object DialogUtil {

    fun alert(
        activity: Activity,
        title: String,
        message: String,
        positiveHandler: DialogInterface.OnClickListener?,
        negativeHandler: DialogInterface.OnClickListener?,
        neutralHandler: DialogInterface.OnClickListener?
    ) {
        var positiveHandlerLocal = positiveHandler
        var negativeHandlerLocal = negativeHandler
        var neutralHandlerLocal = neutralHandler

        if (positiveHandlerLocal == null) {
            // Ok handler
            positiveHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        if (negativeHandlerLocal == null) {
            // Deny handler
            negativeHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        if (neutralHandlerLocal == null) {
            // Cancel handler
            neutralHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        val dialog = AlertDialog.Builder(activity).create()

        dialog.setTitle(title)

        dialog.setMessage(message)

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            activity.getString(R.string.accept),
            positiveHandlerLocal
        )

        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            activity.getString(R.string.deny),
            negativeHandlerLocal
        )

        dialog.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            activity.getString(R.string.cancel),
            neutralHandlerLocal
        )

        dialog.show()
    }

    fun alert(
        activity: Activity,
        title: String,
        message: String,
        positiveHandler: DialogInterface.OnClickListener?,
        negativeHandler: DialogInterface.OnClickListener?
    ) {
        var positiveHandlerLocal = positiveHandler
        var negativeHandlerLocal = negativeHandler

        if (positiveHandlerLocal == null) {
            // Ok handler
            positiveHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        if (negativeHandlerLocal == null) {
            // Cancel handler
            negativeHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        val dialog = AlertDialog.Builder(activity).create()

        dialog.setTitle(title)

        dialog.setMessage(message)

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            activity.getString(R.string.ok),
            positiveHandlerLocal
        )

        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            activity.getString(R.string.cancel),
            negativeHandlerLocal
        )

        dialog.show()
    }

    fun alert(
        activity: Activity,
        title: String,
        message: String,
        positiveHandler: DialogInterface.OnClickListener?
    ) {
        var positiveHandlerLocal = positiveHandler

        if (positiveHandlerLocal == null) {
            // Ok handler
            positiveHandlerLocal = DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        }

        val dialog = AlertDialog.Builder(activity).create()

        dialog.setTitle(title)

        dialog.setMessage(message)

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            activity.getString(R.string.ok),
            positiveHandlerLocal
        )

        dialog.show()
    }

    fun alert(activity: Activity, title: String, message: String) {
        alert(activity, title, message, null)
    }

}
