package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProductManufactureItemviewBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse

@SuppressLint("NotifyDataSetChanged")
class ComponentDataAdapter : RecyclerView.Adapter<ComponentDataAdapter.ViewHolder>() {

    //region VARIABLES
    private var hostList: List<GetDataByBarcodeResponse.Components> = mutableListOf()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentDataAdapter.ViewHolder {
        val binding = LayoutProductManufactureItemviewBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComponentDataAdapter.ViewHolder, position: Int) {
        val curuntitem = hostList[position]

        with(holder) {
            with(hostList[position]) {
                binding.apply {
                    srNo.visibility     = GONE
                    lastCl.visibility   = GONE
                    fieldName.text      = curuntitem.ComponentsName
                    fieldQty.text       = curuntitem.ComponentsQty
                }
            }
        }
    }

    override fun getItemCount(): Int = hostList.size
    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS
    inner class ViewHolder(val binding : LayoutProductManufactureItemviewBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(items: ArrayList<GetDataByBarcodeResponse.Components>?) {
        hostList = (items ?: emptyList()) as ArrayList<GetDataByBarcodeResponse.Components>
        notifyDataSetChanged()
    }

    //endregion ALL FUNCTIONS

}