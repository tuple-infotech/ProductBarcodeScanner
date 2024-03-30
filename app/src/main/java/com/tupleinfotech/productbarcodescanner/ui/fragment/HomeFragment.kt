package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentHomeBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse
import com.tupleinfotech.productbarcodescanner.ui.adapter.ComponentDataAdapter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    //region VARIABLES
    private var _binding                    : FragmentHomeBinding?                                  = null
    private val binding                     get()                                                   = _binding!!
    private var observerExecuted            : Boolean                                               = false
    private var barcodetext                 : String?                                               = null
    private val sharedViewModel             : SharedViewModel by viewModels()
    private var componentDataAdapter        : ComponentDataAdapter?                           = ComponentDataAdapter()
    private var componentData = ArrayList<GetDataByBarcodeResponse.Components>()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        scanButton()
        clearButton()
        getScannedBarcodeData()
//        getDataByBarcode("4154545")
        initorderlist()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun scanButton(){
        binding.scanBtn.setOnClickListener {
            barcodetext = ""
            binding.etBoxBarcodeScanned.text?.clear()
            binding.etBoxBarcode.text?.clear()
            binding.etBoxDesignName.text?.clear()
            binding.etBoxFtName.text?.clear()
            binding.etBoxWhInNotes.text?.clear()
            binding.etBoxWhInTime.text?.clear()
            binding.etBoxWhName.text?.clear()
            binding.etBoxWhOutNotes.text?.clear()
            binding.etBoxWhOutTime.text?.clear()
            val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
            componentDataAdapter?.updateList(hostList)
            findNavController().navigate(R.id.scannerFragment)
        }
    }

    private fun clearButton(){
        binding.apply {
            clearBtn.setOnClickListener {
                etBoxBarcodeScanned.text?.clear()
                etBoxBarcode.text?.clear()
                etBoxDesignName.text?.clear()
                etBoxFtName.text?.clear()
                etBoxWhInNotes.text?.clear()
                etBoxWhInTime.text?.clear()
                etBoxWhName.text?.clear()
                etBoxWhOutNotes.text?.clear()
                etBoxWhOutTime.text?.clear()
                val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                componentDataAdapter?.updateList(hostList)
            }
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun initorderlist() {

        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())
        val recycleviewOrderlist            = binding.itemListRv
        componentDataAdapter                = ComponentDataAdapter()
        componentDataAdapter?.updateList(componentData)

        recycleviewOrderlist.adapter        = componentDataAdapter
        recycleviewOrderlist.layoutManager  = layoutManager

        recycleviewOrderlist.adapter = componentDataAdapter

    }

    private fun getScannedBarcodeData(){
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>("ScannedResult")?.observe(viewLifecycleOwner) { resultData ->
            if (!observerExecuted) {
                barcodetext = resultData?.getString("Scanner")
                if (barcodetext != null) {
                    binding.etBoxBarcodeScanned.setText(barcodetext)
                    getDataByBarcode(barcodetext.toString())
                }
            }
        }
    }

    companion object Apphelper {

        //region CONVERT STRING INTO JSON
        inline fun <reified T> convertJsonToModel(jsonString: String): T? {
            return try {
                Gson().fromJson(jsonString, T::class.java)
            } catch (e: Exception) {
                Log.i("==>", "ERROR: Unable to parse JSON into model")
                null
            }
        }
    }
    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    //4154545

    private fun getDataByBarcode(barcodeText : String){

        val map = mapOf(
            "Barcode"            to          barcodeText.trim().replace("\n",""),
        )

        sharedViewModel.api_service(requireContext(),"product/getdatabybarcode",map,{ getDataByBarcoderesponse ->
            println(getDataByBarcoderesponse)
            val getDataByBarcodeResponse: GetDataByBarcodeResponse? = convertJsonToModel(getDataByBarcoderesponse)

            if (getDataByBarcodeResponse != null) {
                println(getDataByBarcodeResponse)

/*
                if (getDataByBarcodeResponse.ErrorMessage.toString().isEmpty()) {
*/
                    if (getDataByBarcodeResponse.products != null && getDataByBarcodeResponse.products.toString().isNotEmpty()) {

                        if (getDataByBarcodeResponse.products?.Barcode.toString().isNotEmpty()){
                            getDataByBarcodeResponse.products?.let {
                                binding.etBoxBarcodeScanned.setText(it.Barcode.toString())
                                binding.etBoxBarcode.setText(it.Barcode.toString())
                                binding.etBoxDesignName.setText(it.DesignName.toString())
                                binding.etBoxWhName.setText(it.WarehouseName.toString())
                                binding.etBoxFtName.setText(it.FactoryName.toString())
                                binding.etBoxWhInTime.setText(it.WarehouseInTime.toString())
                                binding.etBoxWhOutTime.setText(it.WarehouseOutTime.toString())
                                binding.etBoxWhInNotes.setText(it.WarehouseInNotes.toString())
                                binding.etBoxWhOutNotes.setText(it.WarehouseOutNotes.toString())
                            }

                            getDataByBarcodeResponse.products?.components.let {
                                componentDataAdapter?.updateList(it)
                            }
                        }
                        else{
                            Toast.makeText(requireContext(), getDataByBarcodeResponse.ErrorMessage.toString(), Toast.LENGTH_SHORT).show()
                            binding.etBoxBarcodeScanned.text?.clear()
                            binding.etBoxBarcode.text?.clear()
                            binding.etBoxDesignName.text?.clear()
                            binding.etBoxFtName.text?.clear()
                            binding.etBoxWhInNotes.text?.clear()
                            binding.etBoxWhInTime.text?.clear()
                            binding.etBoxWhName.text?.clear()
                            binding.etBoxWhOutNotes.text?.clear()
                            binding.etBoxWhOutTime.text?.clear()
                            val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                            componentDataAdapter?.updateList(hostList)
                        }


                    }
                    else {
                        Toast.makeText(requireContext(), "Details Not Found", Toast.LENGTH_SHORT)
                            .show()
                    }
/*
                }
*/
/*                else {
                    Toast.makeText(requireContext(), getDataByBarcodeResponse.ErrorMessage.toString(), Toast.LENGTH_SHORT).show()
                }*/


            }
            else {
                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    //endregion API SERVICE
}