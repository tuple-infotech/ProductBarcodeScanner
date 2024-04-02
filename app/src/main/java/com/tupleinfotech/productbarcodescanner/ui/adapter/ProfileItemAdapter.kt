package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProfileFieldBinding

@SuppressLint("NotifyDataSetChanged")
class ProfileItemAdapter: RecyclerView.Adapter<ProfileItemAdapter.ViewHolder>() {

    //region VARIABLES
    var fieldLabelList            :   List<String>          =       mutableListOf()

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

                binding.apply {

                    fieldTitleTxt.text = currentitem
                    when(currentitem.toString()){
                        "Full Name" -> {
                            fieldName.text = "VAIBHAV JOSHI"
                            fieldImg.setImageResource(R.drawable.icon_user)
                        }
                        "Email"     -> {
                            fieldName.text = "vaibhavjoshi25098@gmail.com"
                            fieldImg.setImageResource(R.drawable.icon_email_profile)
                        }
                        "Phone"     -> {
                            fieldName.text = "6353229708"
                            fieldImg.setImageResource(R.drawable.icon_mobile_profile)
                        }
                        "Address"   -> {
                            fieldName.text = "48-49. Eath Alpha, Near Fire Station, Vasna-Bhayli, Vadodara, Gujarat - 390021"
                            fieldImg.setImageResource(R.drawable.icon_address_profile)
                        }
                        "DOB"       -> {
                            fieldName.text = "11/20/2002"
                            fieldImg.setImageResource(R.drawable.icon_calendar_profile)
                        }
                        "Gender"    -> {
                            fieldName.text = "Male"
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