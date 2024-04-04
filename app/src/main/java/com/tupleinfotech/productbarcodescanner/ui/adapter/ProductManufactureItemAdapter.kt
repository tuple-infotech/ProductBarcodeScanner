package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutProductManufactureItemviewBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse

class ProductManufactureItemAdapter : RecyclerView.Adapter<ProductManufactureItemAdapter.ViewHolder>() {

    //region VARIABLES
    var hostlist                :   List<GetDataByBarcodeResponse.Components>          =       mutableListOf()
    var onEditItemClick         : ((GetDataByBarcodeResponse.Components) -> Unit)? =    null
    var onDeleteItemClick         : ((GetDataByBarcodeResponse.Components) -> Unit)? =    null

    inner class ViewHolder (val binding : LayoutProductManufactureItemviewBinding) : RecyclerView.ViewHolder(binding.root)
    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ProductManufactureItemAdapter.ViewHolder {
        val binding = LayoutProductManufactureItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductManufactureItemAdapter.ViewHolder, position: Int) {
        val currentitem = hostlist[position]

        with(holder){
            with(hostlist[position]){

                binding.fieldName.text = currentitem.ComponentsName
                binding.fieldQty.text = currentitem.ComponentsQty
                binding.lastCl.visibility = VISIBLE
                binding.fieldOthers.visibility = GONE
                binding.editbtn.visibility = VISIBLE
                binding.deletebtn.visibility = VISIBLE

                binding.editbtn.setOnClickListener {
                    onEditItemClick?.invoke(hostlist[position])
                }
                binding.deletebtn.setOnClickListener {
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