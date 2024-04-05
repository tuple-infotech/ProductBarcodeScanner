package com.tupleinfotech.productbarcodescanner.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.databinding.LayoutQuickInfoItemViewBinding
import com.tupleinfotech.productbarcodescanner.model.QuickInfoDataResponse

@SuppressLint("NotifyDataSetChanged")
class QuickInfoAdapter : RecyclerView.Adapter<QuickInfoAdapter.ViewHolder>() {

    //region VARIABLES
    var hostList            :   List<Pair<String,Int>>          = mutableListOf()
    var quickInfoDataCount  :   QuickInfoDataResponse.QuickInfo = QuickInfoDataResponse.QuickInfo()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickInfoAdapter.ViewHolder {
        val binding = LayoutQuickInfoItemViewBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickInfoAdapter.ViewHolder, position: Int) {
        val curuntitem = hostList[position]

        with(holder) {
            with(hostList[position]) {
                binding.quickInfoLabelTv.text = curuntitem.first.toString()
                binding.quickInfoIv.setImageResource(curuntitem.second)

                when(binding.quickInfoLabelTv.text.toString()){
                    "Total Barcode"     -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalBarcodePrint
                    }
                    "Total Production"  -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalProduction

                    }
                    "Inward"            -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalInWard

                    }
                    "Outward"           -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalOutWard

                    }
                    "Total Users"       -> {
                        binding.quickInfoFirstCountTv.visibility    = VISIBLE
                        binding.quickInfoSecondCountTv.visibility   = GONE
                        binding.quickInfoThirdCountTv.visibility    = VISIBLE
                        binding.quickInfoFirstCountTv.text          = quickInfoDataCount.ActiveUsers
                        binding.quickInfoThirdCountTv.text          = quickInfoDataCount.InActiveUsers

                    }
                    "Warehouse"         -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalWarehouse

                    }
                    "Workshop"          -> {
                        binding.quickInfoFirstCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.visibility   = VISIBLE
                        binding.quickInfoThirdCountTv.visibility    = GONE
                        binding.quickInfoSecondCountTv.text         = quickInfoDataCount.TotalFactory

                    }
                }

                println(quickInfoDataCount)
            }
        }
    }

    override fun getItemCount(): Int = hostList.size

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region ALL FUNCTIONS

    inner class ViewHolder(val binding : LayoutQuickInfoItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(list: List<Pair<String,Int>>,quickInfoData : QuickInfoDataResponse.QuickInfo) {
        hostList            = list ?: emptyList()
        quickInfoDataCount  = quickInfoData ?: QuickInfoDataResponse.QuickInfo()
        notifyDataSetChanged()
    }

    //endregion ALL FUNCTIONS


}