package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutTwoItemViewBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse

@SuppressLint("NotifyDataSetChanged")
class ProductionReportAdapter : RecyclerView.Adapter<ProductionReportAdapter.ViewHolder>() {

    var hostlist                :   List<ProductionDetailsResponse.Products>          =       mutableListOf()
    var onItemClick                         : ((String) -> Unit)?        = null

    inner class ViewHolder (val binding : LayoutTwoItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionReportAdapter.ViewHolder {
        val binding = LayoutTwoItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionReportAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.tvFirst.text           =   currentitem.DesignName
                binding.tvThird.text          =   currentitem.Barcode
                binding.tvFourth.text            =   currentitem.FactoryName
                binding.tvSecond.text            =   currentitem.WarehouseName

                binding.firstview.visibility    =   VISIBLE
                binding.tvFourth.visibility     =   VISIBLE
                binding.thirdview.visibility    =   VISIBLE
                binding.fourthview.visibility   =   VISIBLE

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostlist[position].Barcode.toString())
                }

            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    fun updateList(list: List<ProductionDetailsResponse.Products>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }
}