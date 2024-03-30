package com.tupleinfotech.productbarcodescanner.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.DialogAlertBinding

class DialogHelper {
    companion object {

        fun showErrorDialog(context: Context, title: String,btnText : String="") {
            val dialog = Dialog(context)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.alert_dailog_error)
            val body = dialog.findViewById(R.id.tv_message) as TextView
            body.text = title
            val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton
            if(btnText.isNotEmpty()) btnOk.text = btnText
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        fun Alert_Selection(
            context: Context,
            message: String,
            positiveButtonTitle: String,
            negativeButtonTitle: String,
            onPositiveButtonClick: () -> Unit,
            onNegativeButtonClick: () -> Unit,
            showNegativeButton: Boolean = false,
            onDismiss: () -> Unit
        ) {
            val prefs = AppHelper.Companion.PreferenceHelper.customPreference(context, GlobalVariables.CUSTOM_PREF_NAME)

            if (prefs.getString("ALERTTYPE", null).toString() == "Custom Dialog") {
                ShowCustomAlertDialog(
                    context,
                    message,
                    positiveButtonTitle,
                    negativeButtonTitle,
                    onPositiveButtonClick,
                    onNegativeButtonClick,
                    showNegativeButton,
                    onDismiss
                )
            } else {
                ShowAlertDialog(
                    context,
                    message,
                    positiveButtonTitle,
                    negativeButtonTitle,
                    onPositiveButtonClick,
                    onNegativeButtonClick,
                    showNegativeButton,
                    onDismiss
                )
            }
        }

        private fun ShowAlertDialog(
            context: Context,
            message: String,
            positiveButtonTitle: String,
            negativeButtonTitle: String,
            onPositiveButtonClick: () -> Unit,
            onNegativeButtonClick: () -> Unit,
            showNegativeButton: Boolean = false,
            onDismiss: () -> Unit
        ) {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle("")
            builder.setMessage(message)

            // Set a positive button and its click listener
            builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                onPositiveButtonClick()
                dialog.dismiss()
            }

            if (showNegativeButton) {
                // Set a negative button and its click listener only if showNegativeButton is true
                builder.setNegativeButton(negativeButtonTitle) { dialog, _ ->
                    onNegativeButtonClick()
                    dialog.dismiss()
                }
            }

            // Set a dismiss listener
            builder.setOnDismissListener {
                onDismiss()
            }

            // Create and show the dialog
            val alertDialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(false)

            alertDialog.show()
        }

        private fun ShowCustomAlertDialog(
            context: Context,
            message: String,
            positiveButtonTitle: String,
            negativeButtonTitle: String,
            onPositiveButtonClick: () -> Unit,
            onNegativeButtonClick: () -> Unit,
            showNegativeButton: Boolean = false,
            onDismiss: () -> Unit
        ) {

            //region Dialog Creation
            val _binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
            val builder = android.app.AlertDialog.Builder(context)
            builder.setView(_binding.root)

            val alertDialog = builder.create()

            // Create and show the dialog
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(false)
            //endregion Dialog Creation

            //region Set the dialog title and message
            _binding.alertTitle.text = GlobalVariables.AlertTitle
            _binding.alertText.text = message
            //endregion Set the dialog title and message

            //region Dialog Image
            Glide.with(_binding.alertIconImg)
                .load(GlobalVariables.ImageBaseUrl + GlobalVariables.ALERTIMAGE)
                .into(_binding.alertIconImg)
            //endregion Dialog Image

            //region positive and negetive button click, text and visibility
            _binding.alertBtnOk.text = positiveButtonTitle
            _binding.alertBtnCancel.visibility = GONE

            _binding.alertBtnOk.setOnClickListener {
                onPositiveButtonClick()
                alertDialog.dismiss()
            }
            if (showNegativeButton) {
                _binding.alertBtnCancel.visibility = VISIBLE
                _binding.alertBtnCancel.text = negativeButtonTitle

                _binding.alertBtnCancel.setOnClickListener {
                    onNegativeButtonClick()
                    alertDialog.dismiss()
                }
            }

            // Set a dismiss listener
            builder.setOnDismissListener {
                onDismiss()
                alertDialog.dismiss()
            }

            //endregion positive and negetive button click, text and visibility

            //region Dialog Show
            alertDialog.show()
            //endregion Dialog Show
        }

    }
}