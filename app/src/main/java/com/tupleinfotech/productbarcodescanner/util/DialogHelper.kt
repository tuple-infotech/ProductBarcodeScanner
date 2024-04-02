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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.DialogAlertBinding
import com.tupleinfotech.productbarcodescanner.databinding.DialogSelectListItemBinding
import com.tupleinfotech.productbarcodescanner.ui.adapter.ListingAdapter

class DialogHelper {
    companion object {

        var loading : Dialog? = null

        fun showLoading(context: Context,title: String){

            if(loading==null){
                loading = Dialog(context)
                loading?.window?.setBackgroundDrawableResource(android.R.color.transparent)
                loading?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                loading?.setCancelable(false)
                loading?.setContentView(R.layout.loading_progress)

                val body: TextView = loading?.findViewById(R.id.message)!!
                if(title.isNotEmpty())
                    body.text = title

                loading?.setCancelable(false)
            }
            if(loading!=null && !loading!!.isShowing)
                loading!!.show()

        }

        fun dismissLoading(){
            if(loading!=null && loading!!.isShowing)
                loading!!.dismiss()
        }

        fun showErrorDialog(context: Context, title: String,btnText : String="") {
            val dialog = Dialog(context)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.alert_dailog_error)
            val body: TextView = dialog.findViewById(R.id.tv_message)
            body.text = title
            val btnOk: AppCompatButton = dialog.findViewById(R.id.btn_ok)
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
            onPositiveButtonClick: () -> Unit = {},
            onNegativeButtonClick: () -> Unit = {},
            showNegativeButton: Boolean = false,
            onDismiss: () -> Unit = {}
        ) {
            val prefs = AppHelper.Companion.PreferenceHelper.customPreference(context, GlobalVariables.CUSTOM_PREF_NAME)

//            if (prefs.getString("ALERTTYPE", null).toString() == "Custom Dialog") {
//                ShowCustomAlertDialog(
//                    context,
//                    message,
//                    positiveButtonTitle,
//                    negativeButtonTitle,
//                    onPositiveButtonClick,
//                    onNegativeButtonClick,
//                    showNegativeButton,
//                    onDismiss
//                )
//            } else {
//                ShowAlertDialog(
//                    context,
//                    message,
//                    positiveButtonTitle,
//                    negativeButtonTitle,
//                    onPositiveButtonClick,
//                    onNegativeButtonClick,
//                    showNegativeButton,
//                    onDismiss
//                )
//            }

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

        fun showListingDialog(
            context: Context,
            itemList: ArrayList<Pair<String,String>>,
            isSearchVisible : Boolean = false,
            onListItemClick         : ((Pair<String,String>) -> Unit)? =    {}
        ) {
            //region Dialog Creation

            val _binding = DialogSelectListItemBinding.inflate(LayoutInflater.from(context))
            val builder = android.app.AlertDialog.Builder(context)
            builder.setView(_binding.root)

            val alertDialog = builder.create()
            /*
                        val onListItemClick         : ((Pair<String,String>) -> Unit)? =    null
            */

            // Create and show the dialog
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(false)
            //endregion Dialog Creation

            if (isSearchVisible) _binding.searchbox.visibility = VISIBLE else _binding.searchbox.visibility = GONE

            _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
            _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
            _binding.customActionBar.arrowBnt.visibility = GONE
            _binding.customActionBar.setOurText.text = "Select"
            _binding.customActionBar.notificationBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
            val recyclerViewPaymentList                     = _binding.itemListingRv
            recyclerViewPaymentList.layoutManager           = layoutManager
            recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
            val listingAdapter                     : ListingAdapter = ListingAdapter(itemList)
            listingAdapter.updateItems(itemList)
            listingAdapter.onItemClick = {
                onListItemClick?.invoke(Pair(it.first,it.second))
            }
            recyclerViewPaymentList.adapter                 = listingAdapter

            //region Dialog Show
            alertDialog.show()
            //endregion Dialog Show
        }
    }
}