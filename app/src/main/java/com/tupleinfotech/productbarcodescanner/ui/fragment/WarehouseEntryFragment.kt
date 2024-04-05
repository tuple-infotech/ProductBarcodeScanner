package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentWarehouseEntryBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WarehouseEntryFragment : Fragment() {

    //region VARIABLES
    private var _binding                : FragmentWarehouseEntryBinding?        = null
    private val binding                 get()                                   = _binding!!
    private lateinit var prefs          : SharedPreferences
    private val sharedViewModel         : SharedViewModel                       by viewModels()
    private var warehouseID             : String                                = "0"
    private var pipeID                  : String                                = "0"
    private var locationId              : String                                = "0"
    private var vendorId                : String                                = "0"
    private var warehouseInwardNotes    : String                                = ""
    private var barcodetext             : String?                               = ""
    private var observerExecuted        : Boolean                               = false

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding    = FragmentWarehouseEntryBinding.inflate(inflater, container, false)
        prefs       = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()

        return binding.root

    }
    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD
    private fun init(){
        getWarehouseList()
        onBackPressed()
        getScannedBarcodeData()
        scanBtn()
        getBarcodeDetails()
        inOutWardButton()
        initShowDetails()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun scanBtn(){
        binding.scanBtn.setOnClickListener {
            barcodetext = ""
            findNavController().navigate(R.id.scannerFragment)
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

    private fun inOutWardButton(){

        binding.inwardBtn.setOnClickListener {
            if (binding.selectWarehouseTv.text.toString().equals("Warehouse",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select warehouse !!","OK","", onPositiveButtonClick = {})
            }
            else if (binding.etBoxBarcodeScanned.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please enter barcode !!","OK","", onPositiveButtonClick = {})
            }
            else if (binding.etBoxBarcodeRowNo.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please enter row no !!","OK","", onPositiveButtonClick = {})
            }
            else if (binding.etBoxBarcodeCellNo.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please enter cell no !!","OK","", onPositiveButtonClick = {})
            }
            else{
                if (binding.inwardBtn.text.toString().equals("Inward",true)){
                    if (binding.etBoxBarcodeInwardNotes.text.toString().isEmpty()){
                        DialogHelper.Alert_Selection(requireContext(),"Please enter inward notes !!","OK","", onPositiveButtonClick = {})
                    }
                    else {
                        saveWarehouseInData()
                    }
                }else{
                    if (binding.etBoxBarcodeInwardNotes.text.toString().isEmpty()){
                        DialogHelper.Alert_Selection(requireContext(),"Please enter outward notes !!","OK","", onPositiveButtonClick = {})
                    }
                    else {
                        updateWarehouseInData()
                    }
                }
            }
        }


    }

    private fun initShowDetails(){
        binding.showWarehouseDataBtn.setOnClickListener {
            val args = Bundle()
            args.putBoolean("isfromwarehouse",true)
            findNavController().navigate(R.id.productionReportFragment,args)
        }
    }

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

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

    private fun initWarehouseDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWarehouseTv.setOnClickListener {
            sharedViewModel.showWarehouseListingDialog(requireContext(),itemList,false) {
                binding.selectWarehouseTv.text = it.WarehouseName.toString()
                warehouseID = it.WarehouseId.toString()
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

    //http://150.129.105.34/api/v1/warehouseApi/getWarehouseList
    private fun getWarehouseList(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_WAREHOUSE_LIST
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{},{ getWorkshopResponse ->
            println(getWorkshopResponse)
            val workshopListResponse: WorkshopListResponse? = AppHelper.convertJsonToModel(getWorkshopResponse)

            if (workshopListResponse != null) {
                println(workshopListResponse)
                initWarehouseDropDown(workshopListResponse.WarehouseList)
            }
            else {

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    private fun getDataByBarcode(barcodeText : String){

        val map = mapOf(
            "Barcode"            to          barcodeText.trim().replace("\n",""),
        )

        val getDataByBarcodeUrl = prefs.host + UrlEndPoints.GET_DATA_BY_BARCODE
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{},{ getDataByBarcoderesponse ->
            println(getDataByBarcoderesponse)
            val getDataByBarcodeResponse: GetDataByBarcodeResponse? =
                AppHelper.convertJsonToModel(getDataByBarcoderesponse)

            if (getDataByBarcodeResponse != null) {
                println(getDataByBarcodeResponse)

                if (getDataByBarcodeResponse.products != null && getDataByBarcodeResponse.products.toString().isNotEmpty()) {

                    if (getDataByBarcodeResponse.products?.Barcode.toString().isNotEmpty()){
                        getDataByBarcodeResponse.products?.let {
                            binding.etBoxBarcodeScanned.setText(it.Barcode.toString())
                            pipeID                  = it.PipeId.toString()
                            locationId              = it.LocationId.toString()
                            vendorId                = it.VendorId.toString()
                            warehouseInwardNotes    = it.WarehouseInNotes.toString()

                            if(it.LocationId == 0){
                                binding.inwardBtn.text = "Inward"
                                binding.inputLayoutInwardNotes.hint = "Inward Notes"
                            }
                            else{
                                binding.inwardBtn.text = "Outward"
                                binding.inputLayoutInwardNotes.hint = "Outward Notes"
                            }
                        }

                    }
                    else{
                        Toast.makeText(requireContext(), getDataByBarcodeResponse.ErrorMessage.toString(), Toast.LENGTH_SHORT).show()
                        binding.etBoxBarcodeScanned.text?.clear()
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

    //http://150.129.105.34/api/v1/warehouseApi/saveWarehouseInData
    private fun saveWarehouseInData(){

        val map = mapOf(
            "Barcode"           to   binding.etBoxBarcodeScanned.text.toString().trim().replace("\n",""),
            "WarehouseId"       to   warehouseID,
            "WarehouseRowNo"    to   binding.etBoxBarcodeRowNo.text.toString().trim().replace("\n",""),
            "WarehouseCellNo"   to   binding.etBoxBarcodeCellNo.text.toString().trim().replace("\n",""),
            "WarehouseInNotes"  to   binding.etBoxBarcodeInwardNotes.text.toString().trim().replace("\n",""),
            "PipeId"            to   pipeID
        )

        val getDataByBarcodeUrl = prefs.host + UrlEndPoints.SAVE_WAREHOUSE_IN_DATA
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{},{ saveWarehouseData ->
            println(saveWarehouseData)
            val gson = Gson()
            val responseJson = gson.fromJson(saveWarehouseData, JsonObject::class.java)

            if (responseJson["Status"].asString.equals("Success",true)){

                if (responseJson["ErrorMessage"].asString.equals("Pipe Recieved In Warehouse",true)){

                    DialogHelper.Alert_Selection(requireContext(),responseJson["ErrorMessage"].asString,"OK","", onPositiveButtonClick = {
                        binding.selectWarehouseTv.text = "Warehouse"
                        binding.etBoxBarcodeScanned.text?.clear()
                        binding.etBoxBarcodeRowNo.text?.clear()
                        binding.etBoxBarcodeCellNo.text?.clear()
                        binding.etBoxBarcodeInwardNotes.text?.clear()
                        binding.inwardBtn.text = "Inward"
                    })

                }
                else{

                }
            }
            else{

            }

        },
            {
                println(it)
            })
    }

    //http://150.129.105.34/api/v1/warehouseApi/updateWarehouseOutData
    private fun updateWarehouseInData(){

        val map = mapOf(
            "LocationId"        to      locationId,
            "Barcode"           to      binding.etBoxBarcodeScanned.text.toString().trim().replace("\n",""),
            "WarehouseId"       to      warehouseID,
            "WarehouseRowNo"    to      binding.etBoxBarcodeRowNo.text.toString().trim().replace("\n",""),
            "WarehouseCellNo"   to      binding.etBoxBarcodeCellNo.text.toString().trim().replace("\n",""),
            "WarehouseInNotes"  to      warehouseInwardNotes.trim().replace("\n",""),
            "PipeId"            to      pipeID,
            "WarehouseOutNotes" to      binding.etBoxBarcodeInwardNotes.text.toString().trim().replace("\n",""),
            "VendorId"          to      vendorId

        )

        val getDataByBarcodeUrl = prefs.host + UrlEndPoints.UPDATE_WAREHOUSE_IN_DATA
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{},{ saveWarehouseData ->
            println(saveWarehouseData)
            val gson = Gson()
            val responseJson = gson.fromJson(saveWarehouseData, JsonObject::class.java)

            if (responseJson["Status"].asString.equals("Success",true)){

                if (responseJson["ErrorMessage"].asString.equals("Pipe Dispatched from Warehouse",true)){

                    DialogHelper.Alert_Selection(requireContext(),responseJson["ErrorMessage"].asString,"OK","", onPositiveButtonClick = {
                        binding.selectWarehouseTv.text = "Warehouse"
                        binding.etBoxBarcodeScanned.text?.clear()
                        binding.etBoxBarcodeRowNo.text?.clear()
                        binding.etBoxBarcodeCellNo.text?.clear()
                        binding.etBoxBarcodeInwardNotes.text?.clear()
                        binding.inwardBtn.text = "Inward"
                    })

                }
                else{

                }
            }
            else{

            }

        },
            {
                println(it)
            })
    }

    //endregion API SERVICE
}