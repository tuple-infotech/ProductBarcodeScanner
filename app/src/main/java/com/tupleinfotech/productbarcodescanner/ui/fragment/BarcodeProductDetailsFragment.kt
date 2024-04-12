package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentBarcodeProductDetailsBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ComponentDataAdapter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.convertIso8601ToReadable
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.convertJsonToModel
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BarcodeProductDetailsFragment : Fragment() {

    //region VARIABLES
    private var _binding                    : FragmentBarcodeProductDetailsBinding?                 = null
    private val binding                     get()                                                   = _binding!!
    private lateinit var prefs              : SharedPreferences
    private val sharedViewModel             : SharedViewModel                                       by viewModels()
    private var observerExecuted            : Boolean                                               = false
    private var barcodetext                 : String?                                               = ""
    private var componentDataAdapter        : ComponentDataAdapter?                                 = ComponentDataAdapter()
    private var componentData               : ArrayList<GetDataByBarcodeResponse.Components>        = ArrayList<GetDataByBarcodeResponse.Components>()
    private var isEditable                  : Boolean                                               = true
    private var barcode                     : String                                                = ""
    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isEditable  = it.getBoolean("isEditable")
            barcode     = it.getString("barcode",barcode)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding    = FragmentBarcodeProductDetailsBinding.inflate(inflater, container, false)
        prefs       = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()
        return binding.root
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        if (!isEditable){
            binding.etBoxBarcode.setText(barcode.toString())
            binding.etBoxBarcodeScanned.setText(barcode.toString())
            binding.inputLayoutBarcode.hint     = "Vendor Name"
            binding.scanBtn.visibility          = GONE
            binding.clearBtn.visibility         = GONE
            binding.whCell.visibility           = VISIBLE
            binding.etBoxWhRowNo.visibility     = VISIBLE
            binding.etBoxWhCellNo.visibility    = VISIBLE
            getDataByBarcode(barcode)

        }
        else{
            binding.inputLayoutBarcode.hint     = "Barcode"
            binding.scanBtn.visibility          = VISIBLE
            binding.clearBtn.visibility         = VISIBLE
            binding.whCell.visibility           = GONE
            binding.etBoxWhRowNo.visibility     = GONE
            binding.etBoxWhCellNo.visibility    = GONE

        }
        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)

        scanButton()
        clearButton()
        getScannedBarcodeData()
        initorderlist()
        onBackPressed()
        getBarcodeDetails()
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
            binding.etBoxWhRowNo.text?.clear()
            binding.etBoxWhCellNo.text?.clear()
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
                binding.etBoxWhRowNo.text?.clear()
                binding.etBoxWhCellNo.text?.clear()

                barcodetext = ""
                val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                componentDataAdapter?.updateList(hostList)
            }
        }
    }

    private fun getBarcodeDetails(){
        binding.etBoxBarcodeScanned.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.etBoxBarcodeScanned.text.toString().isNotEmpty()) {
                    getDataByBarcode(binding.etBoxBarcodeScanned.text.toString())
                }
                else{
                    DialogHelper.Alert_Selection(requireContext(),"Enter Barcode !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
                }
            }
            false
        }

    }

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun initorderlist() {

        binding.recyclerviewDetails.tvFirst.text              =   "Comp. Name"
        binding.recyclerviewDetails.tvSecond.text               =   "Comp. Qty"
        binding.recyclerviewDetails.tvThird.visibility               =   GONE
        binding.recyclerviewDetails.tvFourth.visibility               =   GONE
        binding.recyclerviewDetails.fourthview.visibility               =   GONE

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(requireActivity())
        val recycleviewOrderlist                        = binding.recyclerviewDetails.itemListRv
        componentDataAdapter                            = ComponentDataAdapter()
        componentDataAdapter?.updateList(componentData)

        recycleviewOrderlist.adapter                    = componentDataAdapter
        recycleviewOrderlist.layoutManager              = layoutManager

        recycleviewOrderlist.adapter                    = componentDataAdapter

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

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS

    private fun onBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,onBackPressedCallback)

    }
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    private fun getDataByBarcode(barcodeText : String){

        val map = mapOf(
            "Barcode"            to          barcodeText.trim().replace("\n",""),
        )

        val getDataByBarcodeUrl = prefs.host + UrlEndPoints.GET_DATA_BY_BARCODE
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{},{ getDataByBarcoderesponse ->
            println(getDataByBarcoderesponse)
            val getDataByBarcodeResponse: GetDataByBarcodeResponse? = convertJsonToModel(getDataByBarcoderesponse)

            if (getDataByBarcodeResponse != null) {
                println(getDataByBarcodeResponse)

                if (getDataByBarcodeResponse.products != null && getDataByBarcodeResponse.products.toString().isNotEmpty()) {

                    if (getDataByBarcodeResponse.products?.Barcode.toString().isNotEmpty()){
                        getDataByBarcodeResponse.products?.let {
                            binding.etBoxBarcodeScanned.setText(it.Barcode.toString())

                            if (!isEditable){
                                binding.etBoxBarcode.setText(it.VendorName.toString())
                            }else{
                                binding.etBoxBarcode.setText(it.Barcode.toString())
                            }
                            binding.etBoxDesignName.setText(it.DesignName.toString())
                            binding.etBoxWhName.setText(it.WarehouseName.toString())
                            binding.etBoxFtName.setText(it.FactoryName.toString())
                            binding.etBoxWhInTime.setText(convertIso8601ToReadable(it.WarehouseInTime.toString()))
                            binding.etBoxWhOutTime.setText(convertIso8601ToReadable(it.WarehouseOutTime.toString()))
                            binding.etBoxWhInNotes.setText(it.WarehouseInNotes.toString())
                            binding.etBoxWhOutNotes.setText(it.WarehouseOutNotes.toString())
                            binding.etBoxWhRowNo.setText(it.WarehouseRowNo.toString())
                            binding.etBoxWhCellNo.setText(it.WarehouseCellNo.toString())
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
                        binding.etBoxWhRowNo.text?.clear()
                        binding.etBoxWhCellNo.text?.clear()
                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        componentDataAdapter?.updateList(hostList)
                    }

                }
                else {
                    Toast.makeText(requireContext(), "Details Not Found", Toast.LENGTH_SHORT).show()
                }

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