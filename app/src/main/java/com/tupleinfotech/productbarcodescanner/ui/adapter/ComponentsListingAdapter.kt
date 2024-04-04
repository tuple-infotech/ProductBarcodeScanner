package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutListingItemBinding
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class ComponentsListingAdapter(): RecyclerView.Adapter<ComponentsListingAdapter.ViewHolder>(), Filterable {

    //region VARIABLES
//    private var hostList    : List<Pair<String,String>> = mutableListOf()
    var hostList    : List<String> = mutableListOf()
    private var filterList  : List<String> = mutableListOf()
    var onItemClick         : ((String) -> Unit)? =    null

    inner class ViewHolder(val binding: LayoutListingItemBinding) : RecyclerView.ViewHolder(binding.root)

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutListingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = filterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filterList[position]
        holder.binding.apply {
            fieldQty.visibility = GONE
            fieldName.text = currentItem.toString()
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(hostList[position])
        }
    }

    //CATEGORY SEARCH FILTER FUNCTIONALITY
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = hostList.filter { item ->
                    item.toString().lowercase(Locale.getDefault()).contains(constraint.toString().lowercase(Locale.getDefault()))
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is List<*>) {
                    filterList = results.values as List<String>
                    notifyDataSetChanged()
                }
            }
        }
    }
    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS

    fun updateItems(items: List<String>?) {
        hostList = items ?: emptyList()
        filterList = items ?: emptyList()
        notifyDataSetChanged()
    }
    //endregion ALL FUNCTIONS
}

