package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutTableviewImageRowBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse

@SuppressLint("NotifyDataSetChanged")
class ProductManufactureItemAdapter : RecyclerView.Adapter<ProductManufactureItemAdapter.ViewHolder>() {

    //region VARIABLES
    var hostlist                : List<GetDataByBarcodeResponse.Components>             = mutableListOf()
    var onEditItemClick         : ((GetDataByBarcodeResponse.Components) -> Unit)?      = null
    var onDeleteItemClick       : ((GetDataByBarcodeResponse.Components) -> Unit)?      = null

    inner class ViewHolder (val binding : LayoutTableviewImageRowBinding) : RecyclerView.ViewHolder(binding.root)
    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ProductManufactureItemAdapter.ViewHolder {
        val binding = LayoutTableviewImageRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductManufactureItemAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.tvFirst.text           =   currentitem.ComponentsName
                binding.tvFourth.text          =   currentitem.ComponentsQty
                binding.firstview.visibility    =   VISIBLE


                binding.btnEdit.setOnClickListener {
                    onEditItemClick?.invoke(hostlist[position])
                }
                binding.btnDelete.setOnClickListener {
                    onDeleteItemClick?.invoke(hostlist[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = hostlist.size

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS

    fun updateList(list: List<GetDataByBarcodeResponse.Components>?) {
        hostlist = list ?: emptyList()
        notifyDataSetChanged()
    }

    //endregion ALL FUNCTIONS


}