package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProductManufactureItemviewBinding
import com.tupleinfotech.productbarcodescanner.model.getProductWarehouseDataResponse

@SuppressLint("NotifyDataSetChanged")
class ProductionWarehouseListingAdapter : RecyclerView.Adapter<ProductionWarehouseListingAdapter.ViewHolder>() {

    var hostlist                :   List<getProductWarehouseDataResponse.Products>          =       mutableListOf()
    var onItemClick                         : ((String) -> Unit)?        = null

    inner class ViewHolder (val binding : LayoutProductManufactureItemviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionWarehouseListingAdapter.ViewHolder {
        val binding = LayoutProductManufactureItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionWarehouseListingAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.srNo.text        =   currentitem.DesignName
                binding.fieldName.text   =   currentitem.Barcode
                binding.fieldQty.text    =   currentitem.FactoryName

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostlist[position].Barcode.toString())
                }

                binding.lastCl.visibility       = View.GONE
                binding.editbtn.visibility      = View.GONE
                binding.deletebtn.visibility    = View.GONE
                binding.fieldOthers.visibility  = View.GONE

            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    fun updateList(list: List<getProductWarehouseDataResponse.Products>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }
}