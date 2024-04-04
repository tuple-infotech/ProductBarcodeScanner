package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProductionReportItemviewBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse

class ProductionReportAdapter : RecyclerView.Adapter<ProductionReportAdapter.ViewHolder>() {

    var hostlist                :   List<ProductionDetailsResponse.Products>          =       mutableListOf()

    inner class ViewHolder (val binding : LayoutProductionReportItemviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionReportAdapter.ViewHolder {
        val binding = LayoutProductionReportItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionReportAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.barcode.text        =   currentitem.Barcode
                binding.designName.text     =   currentitem.DesignName
                binding.factoryName.text    =   currentitem.FactoryName
                binding.warehouseName.text  =   currentitem.WarehouseName

            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    fun updateList(list: List<ProductionDetailsResponse.Products>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }
}