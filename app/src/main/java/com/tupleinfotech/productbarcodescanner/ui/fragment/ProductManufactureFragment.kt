package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.DialogSelectListItemBinding
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProductManufactureBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.adapter.ComponentsListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductManufactureItemAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.WorkshopListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userId
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductManufactureFragment : Fragment() {

    //region VARIABLES

    private var _binding                                    : FragmentProductManufactureBinding?    =  null
    private val binding                                     get()                                   =  _binding!!
    private lateinit var prefs                              : SharedPreferences
    private var productManufactureItemAdapter               : ProductManufactureItemAdapter?        = null
    private var barcodetext                 : String?                                               = null
    private var observerExecuted            : Boolean                                               = false
    private val sharedViewModel             : SharedViewModel by viewModels()
    private var componentData               = ArrayList<GetDataByBarcodeResponse.Components>()
    private var factoryID = "0"
    private var barcodeScannedFactoryID = "10"
    private var barcodeScannedPipeID = "0"

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentProductManufactureBinding.inflate(inflater, container, false)
        val view = binding.root
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)
        init()
        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        initProductManufactureItem()
        scanButton()
        getScannedBarcodeData()
        getBarcodeDetails()
        getWorkshopList()
        addButtonFunctionality()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun initWorkshopDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.inputLayoutWorkshop.setOnClickListener {
            showWorkshopListingDialog(requireContext(),itemList,false) {
                binding.inputLayoutWorkshop.text = it.FactoryName.toString()
                factoryID = it.FactoryId.toString()
            }
        }

        val componentList : ArrayList<String> = arrayListOf()
        componentList.add("Pipe")
        componentList.add("Allbow")
        componentList.add("T")
        componentList.add("Coupling")
        componentList.add("Cup")
        componentList.add("Flame")

        binding.inputLayoutComponent.setOnClickListener {
            showComponentsListingDialog(requireContext(),componentList,false){
                binding.inputLayoutComponent.text = it.toString()
            }
        }
    }

    private fun scanButton(){
        binding.scanBtn.setOnClickListener {
            barcodetext = ""
            findNavController().navigate(R.id.scannerFragment)
        }
    }

    private fun getBarcodeDetails(){
        binding.etBoxBarcode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.etBoxBarcode.text.toString().isNotEmpty()) {
                    getDataByBarcode(binding.etBoxBarcode.text.toString())
                }
                else{
                    DialogHelper.Alert_Selection(requireContext(),"Enter Barcode !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
                }
            }
            false
        }

    }

    private fun updateButtonFunctionality(pipeId : String){
        binding.addBtn.setOnClickListener {
            if (binding.inputLayoutWorkshop.text.toString().equals("WorkShop",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Workshop","OK","")
            }
            else if (binding.inputLayoutWorkshop.text.toString().equals("Components",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Component","OK","")
            }
            else {
                val componentsMap = mapOf(
                    "ComponentsName" to binding.inputLayoutComponent.text.toString(),
                    "ComponentsQty" to binding.etBoxComponentQty.text.toString()
                )

                val componentsArrayList = ArrayList<Map<String, Any>>()
                componentsArrayList.add(componentsMap)

                val map = mapOf<String, Any>(
                    "PipeId" to pipeId.trim(),
                    "Barcode" to binding.etBoxBarcode.text.toString().trim(),
                    "DesignName" to binding.etBoxDesignName.text.toString().trim(),
                    "CreatedBy" to prefs.userId.toString(),
                    "FactoryId" to factoryID,
                    "Components" to componentsArrayList
                )
                updatePipeEntry(map)
            }
        }
    }

    private fun addButtonFunctionality(){
        binding.addBtn.setOnClickListener {
            if (binding.inputLayoutWorkshop.text.toString().equals("WorkShop",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Workshop!!","OK","")
            }
            else if (binding.inputLayoutWorkshop.text.toString().equals("Components",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Component!!","OK","")
            }
            else if (binding.etBoxBarcode.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please Enter Barcode!!","OK","")
            }
            else if (binding.etBoxComponentQty.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please Enter Component Quantity!!","OK","")
            }
            else {
                val componentsMap = mapOf(
                    "ComponentsName" to binding.inputLayoutComponent.text.toString(),
                    "ComponentsQty" to binding.etBoxComponentQty.text.toString()
                )

                val componentsArrayList = ArrayList<Map<String, Any>>()
                componentsArrayList.add(componentsMap)

                val map = mapOf<String, Any>(
                    "PipeId" to barcodeScannedPipeID.trim(),
                    "Barcode" to binding.etBoxBarcode.text.toString().trim(),
                    "DesignName" to binding.etBoxDesignName.text.toString().trim(),
                    "CreatedBy" to prefs.userId.toString(),
                    "FactoryId" to barcodeScannedFactoryID,
                    "Components" to componentsArrayList
                )
                addPipeEntry(map)
            }
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS
    private fun getScannedBarcodeData(){
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>("ScannedResult")?.observe(viewLifecycleOwner) { resultData ->
            if (!observerExecuted) {
                barcodetext = resultData?.getString("Scanner")
                if (barcodetext != null) {
                    binding.etBoxBarcode.setText(barcodetext)
                    getDataByBarcode(barcodetext.toString())
                }
            }
        }
    }

    private fun initProductManufactureItem(){
        if (componentData.isEmpty()){
            binding.recyclerviewDetails.root.visibility = GONE
        }else{
            binding.recyclerviewDetails.root.visibility = VISIBLE
        }
        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerviewItemList                                = binding.recyclerviewDetails.itemListRv
        recyclerviewItemList.layoutManager                      = linearLayoutManager
        recyclerviewItemList.itemAnimator                       = DefaultItemAnimator()

        productManufactureItemAdapter                           = ProductManufactureItemAdapter()

        productManufactureItemAdapter?.apply {
            updateList(componentData)

            onEditItemClick = {
                binding.etBoxBarcode.setText(it.Barcode.toString())
                binding.etBoxComponentQty.setText(it.ComponentsQty.toString())
                binding.inputLayoutComponent.text = it.ComponentsName.toString()

                binding.addBtn.text = "Update"
                updateButtonFunctionality(it.PipeId.toString())
            }

            onDeleteItemClick = {

            }
        }
        recyclerviewItemList.adapter                            = productManufactureItemAdapter

    }

    fun showWorkshopListingDialog(
        context: Context,
        itemList: ArrayList<WorkshopListResponse.List>,
        isSearchVisible : Boolean = false,
        onListItemClick         : ((WorkshopListResponse.List) -> Unit)? =    {}
    ) {
        //region Dialog Creation

        val _binding = DialogSelectListItemBinding.inflate(LayoutInflater.from(context))
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(_binding.root)

        val alertDialog = builder.create()
        /*
                    val onListItemClick         : ((Pair<String,String>) -> Unit)? =    null
        */

        // Create and show the dialog
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        //endregion Dialog Creation

        if (isSearchVisible) _binding.searchbox.visibility = VISIBLE else _binding.searchbox.visibility = GONE

        _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = GONE
        _binding.customActionBar.setOurText.text = "Select"
        _binding.customActionBar.notificationBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
        val recyclerViewPaymentList                     = _binding.itemListingRv
        recyclerViewPaymentList.layoutManager           = layoutManager
        recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
        val listingAdapter                     : WorkshopListingAdapter = WorkshopListingAdapter()
        listingAdapter.updateItems(itemList)
        listingAdapter.onItemClick = {
            onListItemClick?.invoke(it)
            alertDialog.dismiss()
        }

        recyclerViewPaymentList.adapter                 = listingAdapter

        //region Dialog Show
        alertDialog.show()
        //endregion Dialog Show
    }

    fun showComponentsListingDialog(
        context: Context,
        itemList: ArrayList<String>,
        isSearchVisible : Boolean = false,
        onListItemClick         : ((String) -> Unit)? =    {}
    ) {
        //region Dialog Creation

        val _binding = DialogSelectListItemBinding.inflate(LayoutInflater.from(context))
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(_binding.root)

        val alertDialog = builder.create()
        /*
                    val onListItemClick         : ((Pair<String,String>) -> Unit)? =    null
        */

        // Create and show the dialog
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        //endregion Dialog Creation

        if (isSearchVisible) _binding.searchbox.visibility = VISIBLE else _binding.searchbox.visibility = GONE

        _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = GONE
        _binding.customActionBar.setOurText.text = "Select"
        _binding.customActionBar.notificationBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
        val recyclerViewPaymentList                     = _binding.itemListingRv
        recyclerViewPaymentList.layoutManager           = layoutManager
        recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
        val listingAdapter                     : ComponentsListingAdapter = ComponentsListingAdapter()
        listingAdapter.updateItems(itemList)
        listingAdapter.onItemClick = {
            onListItemClick?.invoke(it)
            alertDialog.dismiss()
        }

        recyclerViewPaymentList.adapter                 = listingAdapter

        //region Dialog Show
        alertDialog.show()
        //endregion Dialog Show
    }

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    //http://150.129.105.34/api/v1/productmanufacture/getDataByBarcode
    private fun getDataByBarcode(barcodeText : String){

        val map = mapOf(
            "Barcode"            to          barcodeText.trim().replace("\n",""),
        )

        val getDataByBarcodeUrl = prefs.host + UrlEndPoints.GET_DATA_BY_BARCODE_MANUFACTURE
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{ getDataByBarcoderesponse ->
            println(getDataByBarcoderesponse)
            val getDataByBarcodeResponse: GetDataByBarcodeResponse? = AppHelper.convertJsonToModel(getDataByBarcoderesponse)

            if (getDataByBarcodeResponse != null) {
                println(getDataByBarcodeResponse)

                if (getDataByBarcodeResponse.pipes.isNotEmpty()) {

                    if (getDataByBarcodeResponse.pipes.first().Barcode.toString().isNotEmpty()){

                        getDataByBarcodeResponse.pipes.first().let {
                            binding.etBoxBarcode.setText(it.Barcode.toString())
                            binding.etBoxDesignName.setText(it.DesignName.toString())
                            barcodeScannedPipeID    = it.PipeId.toString()
                            barcodeScannedFactoryID = it.FactoryId.toString()
                        }

                        getDataByBarcodeResponse.pipes.first().Components.let {
                            componentData = it
                            productManufactureItemAdapter?.updateList(it)

                            if (componentData.isEmpty()){
                                binding.recyclerviewDetails.root.visibility = GONE
                            }else{
                                binding.recyclerviewDetails.root.visibility = VISIBLE
                            }
                        }
                    }else{
                        binding.etBoxBarcode.text?.clear()
                        binding.etBoxDesignName.text?.clear()
                        binding.recyclerviewDetails.root.visibility = GONE

                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        productManufactureItemAdapter?.updateList(hostList)
                    }

                }
                else {
                    binding.recyclerviewDetails.root.visibility = GONE

                    Toast.makeText(requireContext(), "Details Not Found", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                binding.recyclerviewDetails.root.visibility = GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    //http://150.129.105.34/api/v1/productmanufacture/getWorkshopList
    private fun getWorkshopList(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_WORKSHOP_LIST
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{ getWorkshopResponse ->
            println(getWorkshopResponse)
            val workshopListResponse: WorkshopListResponse? = AppHelper.convertJsonToModel(getWorkshopResponse)

            if (workshopListResponse != null) {
                println(workshopListResponse)
                initWorkshopDropDown(workshopListResponse.Factorylist)
            }
            else {
                binding.recyclerviewDetails.root.visibility = GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    //http://150.129.105.34/api/v1/productmanufacture/updatePipeEntry
    private fun updatePipeEntry(requestMap : Map<String,Any>){

        val updatePipeEntryUrl = prefs.host + UrlEndPoints.UPDATE_PIPE_ENTRY

        sharedViewModel.api_service(requireContext(),updatePipeEntryUrl,requestMap,{ getUpdatePipeEntryResponse ->
            println(getUpdatePipeEntryResponse)

            val gson = Gson()
            val responseJson = gson.fromJson(getUpdatePipeEntryResponse, JsonObject::class.java)

            if (responseJson["Status"].asString.equals("Success",true)){

                if (responseJson["ErrorMessage"].asString.equals("Pipe updated successfully",true)){

                    DialogHelper.Alert_Selection(requireContext(),responseJson["ErrorMessage"].asString,"OK","", onPositiveButtonClick = {
                        binding.inputLayoutWorkshop.text = "WorkShop"
                        binding.inputLayoutComponent.text = "Components"
                        binding.etBoxBarcode.text?.clear()
                        binding.etBoxDesignName.text?.clear()
                        binding.etBoxComponentQty.text?.clear()
                        binding.addBtn.text = "Add"
                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        productManufactureItemAdapter?.updateList(hostList)
                        binding.recyclerviewDetails.root.visibility = GONE
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

    //http://150.129.105.34/api/v1/productmanufacture/addPipeEntry
    private fun addPipeEntry(requestMap : Map<String,Any>){

        val updatePipeEntryUrl = prefs.host + UrlEndPoints.UPDATE_ADD_ENTRY

        sharedViewModel.api_service(requireContext(),updatePipeEntryUrl,requestMap,{ getUpdatePipeEntryResponse ->
            println(getUpdatePipeEntryResponse)

            val gson = Gson()
            val responseJson = gson.fromJson(getUpdatePipeEntryResponse, JsonObject::class.java)

            if (responseJson["Status"].asString.equals("Success",true)){

                if (responseJson["ErrorMessage"].asString.equals("Pipe added successfully",true)){

                    DialogHelper.Alert_Selection(requireContext(),responseJson["ErrorMessage"].asString,"OK","", onPositiveButtonClick = {
                        binding.inputLayoutWorkshop.text = "WorkShop"
                        binding.inputLayoutComponent.text = "Components"
                        binding.etBoxBarcode.text?.clear()
                        binding.etBoxDesignName.text?.clear()
                        binding.etBoxComponentQty.text?.clear()
                        binding.addBtn.text = "Add"
                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        productManufactureItemAdapter?.updateList(hostList)
                        binding.recyclerviewDetails.root.visibility = GONE
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