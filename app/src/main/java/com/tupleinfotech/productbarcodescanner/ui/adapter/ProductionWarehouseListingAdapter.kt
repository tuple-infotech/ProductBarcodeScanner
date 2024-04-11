package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutTwoItemViewBinding
import com.tupleinfotech.productbarcodescanner.model.getProductWarehouseDataResponse

@SuppressLint("NotifyDataSetChanged")
class ProductionWarehouseListingAdapter : RecyclerView.Adapter<ProductionWarehouseListingAdapter.ViewHolder>() {

    var hostlist                :   List<getProductWarehouseDataResponse.Products>          =       mutableListOf()
    var onItemClick                         : ((String) -> Unit)?        = null

    inner class ViewHolder (val binding : LayoutTwoItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionWarehouseListingAdapter.ViewHolder {
        val binding = LayoutTwoItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionWarehouseListingAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.tvFirst.text           =   currentitem.DesignName
                binding.tvThird.text          =   currentitem.Barcode
                binding.tvSecond.text            =   currentitem.FactoryName

                binding.firstview.visibility    =   VISIBLE
                binding.tvFourth.visibility     =   GONE
                binding.thirdview.visibility    =   VISIBLE
                binding.fourthview.visibility   =   GONE


                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostlist[position].Barcode.toString())
                }

            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    fun updateList(list: List<getProductWarehouseDataResponse.Products>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }
}