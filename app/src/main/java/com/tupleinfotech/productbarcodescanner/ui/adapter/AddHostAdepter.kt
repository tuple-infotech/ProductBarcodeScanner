package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmsc.postab.db.AddHost
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.LayoutTableRowAddhostTypeListBinding

class AddHostAdepter (var hostList: List<AddHost>,) : RecyclerView.Adapter<AddHostAdepter.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: LayoutTableRowAddhostTypeListBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick: ((AddHost) -> Unit)? = null

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutTableRowAddhostTypeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(hostList[position]){
                binding.tvHostName.text=this.host_name

                if (this.host_port.isEmpty()){
                    binding.tvHostAddress.text=this.host_ip
                }else{
                    binding.tvHostAddress.text=this.host_ip+":"+this.host_port
                }

                if(position%2==0){
                    binding.hostRow.setBackgroundResource(R.color.odd_row)
                }else{
                    binding.hostRow.setBackgroundResource(R.color.odd_row)
                }
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostList[adapterPosition])
                }
            }
        }
    }

    // return the size of languageList
    override fun getItemCount(): Int {
        return hostList.size
    }
}