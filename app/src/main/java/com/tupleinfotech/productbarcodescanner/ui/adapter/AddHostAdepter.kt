package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmsc.postab.db.AddHost
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.LayoutTableRowAddhostTypeListBinding

@SuppressLint("SetTextI18n")
class AddHostAdepter (var hostList: List<AddHost>,) : RecyclerView.Adapter<AddHostAdepter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutTableRowAddhostTypeListBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick: ((AddHost) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutTableRowAddhostTypeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

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
                    binding.hostRow.setBackgroundResource(R.color.even_row)
                }else{
                    binding.hostRow.setBackgroundResource(R.color.odd_row)
                }
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostList[adapterPosition])
                }
            }
        }
    }

    override fun getItemCount(): Int = hostList.size

}