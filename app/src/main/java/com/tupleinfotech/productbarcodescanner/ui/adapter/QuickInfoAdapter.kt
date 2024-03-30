package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutQuickInfoItemViewBinding
import com.tupleinfotech.productbarcodescanner.model.AccessRights

@SuppressLint("NotifyDataSetChanged")
class QuickInfoAdapter : RecyclerView.Adapter<QuickInfoAdapter.ViewHolder>() {

    //region VARIABLES
    var hostList            :   List<AccessRights.Access>          =       mutableListOf()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickInfoAdapter.ViewHolder {
        val binding = LayoutQuickInfoItemViewBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickInfoAdapter.ViewHolder, position: Int) {
        val curuntitem = hostList[position]

        with(holder) {
            with(hostList[position]) {
                binding.quickInfoLabelTv.text = curuntitem.accessname
                curuntitem.staticimage?.let { binding.quickInfoIv.setImageResource(it) }
            }
        }
    }

    override fun getItemCount(): Int = hostList.size

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS

    inner class ViewHolder(val binding : LayoutQuickInfoItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(list: List<AccessRights.Access>?) {
        hostList = list ?: emptyList()
        notifyDataSetChanged()
    }

    //endregion ALL FUNCTIONS


}