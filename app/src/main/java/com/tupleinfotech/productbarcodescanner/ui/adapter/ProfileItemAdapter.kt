package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProfileFieldBinding
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.useraddress
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userdob
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.useremail
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userfirstname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.usergender
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userlastname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userphone

@SuppressLint("NotifyDataSetChanged","SetTextI18n")
class ProfileItemAdapter: RecyclerView.Adapter<ProfileItemAdapter.ViewHolder>() {

    //region VARIABLES
    var fieldLabelList            :   List<String>          =       mutableListOf()
    private lateinit var prefs    : SharedPreferences

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ProfileItemAdapter.ViewHolder {
        val binding = LayoutProfileFieldBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileItemAdapter.ViewHolder, position: Int) {
        val currentitem = fieldLabelList[position]

        with(holder){
            with(fieldLabelList[position]) {
                prefs   = PreferenceHelper.customPreference(itemView.context, Constants.CUSTOM_PREF_NAME)

                binding.apply {

                    fieldTitleTxt.text = currentitem
                    when(currentitem.toString()){
                        "Full Name" -> {
                            fieldName.text = prefs.userfirstname + " " + prefs.userlastname
                            fieldImg.setImageResource(R.drawable.icon_user)
                        }
                        "Email"     -> {
                            fieldName.text = prefs.useremail
                            fieldImg.setImageResource(R.drawable.icon_email_profile)
                        }
                        "Phone"     -> {
                            fieldName.text = prefs.userphone
                            fieldImg.setImageResource(R.drawable.icon_mobile_profile)
                        }
                        "Address"   -> {
                            fieldName.text = prefs.useraddress
                            fieldImg.setImageResource(R.drawable.icon_address_profile)
                        }
                        "DOB"       -> {
                            fieldName.text = AppHelper.convertIso8601ToReadable(prefs.userdob.toString())
                            fieldImg.setImageResource(R.drawable.icon_calendar_profile)
                        }
                        "Gender"    -> {
                            fieldName.text = prefs.usergender
                            fieldImg.setImageResource(R.drawable.icon_user)
                        }
                    }

                }

            }
        }
    }

    override fun getItemCount(): Int = fieldLabelList.size

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS
    inner class ViewHolder(val binding : LayoutProfileFieldBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(list: List<String>?) {
        fieldLabelList = list ?: emptyList()
        notifyDataSetChanged()
    }

    //endregion ALL FUNCTIONS

}