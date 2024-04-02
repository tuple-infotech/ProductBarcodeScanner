package com.jmsc.postab.ui.dialogfragment.addhost

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.jmsc.postab.db.AddHost
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.LayoutSearchHostRowBinding
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.TEXT_COLOR

class AdapterHostList (var hostList: List<AddHost>,
) : RecyclerView.Adapter<AdapterHostList.ViewHolder>(), Filterable {
    inner class ViewHolder(val binding: LayoutSearchHostRowBinding) : RecyclerView.ViewHolder(binding.root)

    var hostFilterList = ArrayList<AddHost>()
    // exampleListFull . exampleList

    init {
        hostFilterList = hostList as ArrayList<AddHost>
    }

    var id_host = 0

    var onItemClick: ((AddHost,Int) -> Unit)? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutSearchHostRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(hostFilterList[position]){


                if(this.id == id_host){
                    binding.tvHostLeft.text=(this.host_name)
                    binding.tvHostRight.text=(this.host_ip+":"+this.host_port)
                }else{
                    binding.tvHostLeft.text=(this.host_name)
                    binding.tvHostRight.text=(this.host_ip+":"+this.host_port)
                }
                if(position%2==0){
                    binding.bgCustomer.setBackgroundResource(R.color.odd_row)
                }else{
                    binding.bgCustomer.setBackgroundResource(R.color.odd_row)
                }

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(hostFilterList[position],position)
                }

                if(position == selectedPosition){
                    val colorType = TEXT_COLOR.SELECTED
                    AppHelper.setSelectedRowTextViewColor(binding.tvHostLeft,colorType)
                    AppHelper.setSelectedRowTextViewColor(binding.tvHostRight,colorType)
                    AppHelper.setSelectedRowColor(binding.bgCustomer)
                }else{
                    val colorType = TEXT_COLOR.DEFAULT
                    AppHelper.setSelectedRowTextViewColor(binding.tvHostLeft,colorType)
                    AppHelper.setSelectedRowTextViewColor(binding.tvHostRight,colorType)
                }
            }
        }
    }

    fun updateHostList(items: List<AddHost>) {
        hostFilterList = ArrayList(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return hostFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                hostFilterList = if (charSearch.isEmpty()) {
                    hostList as ArrayList<AddHost>
                } else {
                    val resultList = ArrayList<AddHost>()
                    for (row in hostList) {
                        if (row.host_name.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = hostFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                hostFilterList = results?.values as ArrayList<AddHost>
                notifyDataSetChanged()
            }
        }
    }
}