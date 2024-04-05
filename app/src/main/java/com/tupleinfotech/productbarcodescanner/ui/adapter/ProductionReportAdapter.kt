package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProductManufactureItemviewBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse

@SuppressLint("NotifyDataSetChanged")
class ProductionReportAdapter : RecyclerView.Adapter<ProductionReportAdapter.ViewHolder>() {

    var hostlist                :   List<ProductionDetailsResponse.Products>          =       mutableListOf()
    var onItemClick                         : ((String) -> Unit)?        = null

    inner class ViewHolder (val binding : LayoutProductManufactureItemviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionReportAdapter.ViewHolder {
        val binding = LayoutProductManufactureItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionReportAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.srNo.text        = currentitem.DesignName
                binding.fieldName.text   = currentitem.Barcode
                binding.fieldQty.text    = currentitem.FactoryName
                binding.fieldOthers.text = currentitem.WarehouseName

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostlist[position].Barcode.toString())
                }
                binding.lastCl.visibility       = VISIBLE
                binding.editbtn.visibility      = GONE
                binding.deletebtn.visibility    = GONE
                binding.fieldOthers.visibility  = VISIBLE

            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    fun updateList(list: List<ProductionDetailsResponse.Products>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }
}