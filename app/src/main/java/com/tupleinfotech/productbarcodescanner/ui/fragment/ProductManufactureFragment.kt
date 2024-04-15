package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProductManufactureBinding
import com.tupleinfotech.productbarcodescanner.model.GetDataByBarcodeResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductManufactureItemAdapter
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
@SuppressLint("SetTextI18n")
class ProductManufactureFragment : Fragment() {

    //region VARIABLES

    private var _binding                            : FragmentProductManufactureBinding?                =  null
    private val binding                             get()                                               =  _binding!!
    private lateinit var prefs                      : SharedPreferences
    private val sharedViewModel                     : SharedViewModel                                   by viewModels()
    private var productManufactureItemAdapter       : ProductManufactureItemAdapter?                    = ProductManufactureItemAdapter()
    private var barcodetext                         : String?                                           = ""
    private var observerExecuted                    : Boolean                                           = false
    private var componentData                       : ArrayList<GetDataByBarcodeResponse.Components>    = arrayListOf()
    private var factoryID                           : String                                            = "0"
    private var barcodeScannedPipeID                : String                                            = "0"
    private var barcode                : String                                            = "0"
    private var factorylist       : ArrayList<WorkshopListResponse.List> = arrayListOf()
    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding    = FragmentProductManufactureBinding.inflate(inflater, container, false)
        val view    = binding.root
        prefs       = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)
        init()
        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.getItem(1).isChecked = true

        initProductManufactureItem()
        scanButton()
        getScannedBarcodeData()
        getBarcodeDetails()
        getWorkshopList()
        initShowDetails()
        cancelButtonFunctionality()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun cancelButtonFunctionality(){
        binding.btnCancel.setOnClickListener {
            binding.inputLayoutWorkshop.text    = "WorkShop"
            binding.inputLayoutComponent.text   = "Components"
            binding.etBoxBarcode.text?.clear()
            binding.etBoxDesignName.text?.clear()
            binding.etBoxComponentQty.text?.clear()
            barcodeScannedPipeID = "0"
            factoryID = "0"
            componentData.clear()
            binding.addBtn.text = "Add"
            val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
            productManufactureItemAdapter?.updateList(hostList)
            binding.recyclerviewDetails.root.visibility = GONE
        }
    }

    private fun initShowDetails(){
        binding.btnShowDetails.setOnClickListener {
            findNavController().navigate(R.id.productionReportFragment)
        }
    }

    private fun saveButtonfunctionality(){
        binding.btnSave.setOnClickListener {
            var componentsMap : Map<String,String> = mapOf()
            val componentsArrayList = ArrayList<Map<String, Any>>()

            componentData.forEach {
                componentsMap = mapOf(
                    "ComponentsName"    to it.ComponentsName.toString(),
                    "ComponentsQty"     to it.ComponentsQty.toString(),
                )
                componentsArrayList.add(componentsMap)
            }
            println(componentsArrayList)

            val map = mapOf<String, Any>(
                "PipeId"        to barcodeScannedPipeID.trim(),
                "Barcode"       to binding.etBoxBarcode.text.toString().trim(),
                "DesignName"    to binding.etBoxDesignName.text.toString().trim(),
                "CreatedBy"     to prefs.userId.toString(),
                "FactoryId"     to factoryID,
                "Components"    to componentsArrayList
            )

            if (componentData.isNotEmpty()){
                if (barcodeScannedPipeID == "0"){
                    addPipeEntry(map)
                }
                else{
                    updatePipeEntry(map)
                }
            }else{
                if (barcodeScannedPipeID == "0"){
                    DialogHelper.Alert_Selection(requireContext(),"Please Enter Components!!","OK","")
                }
                else{
                    updatePipeEntry(map)
                }
            }
        }
    }

    private fun initWorkshopDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.inputLayoutWorkshop.setOnClickListener {
            sharedViewModel.showWorkshopListingDialog(requireContext(),itemList,false) {
                binding.inputLayoutWorkshop.text    = it.FactoryName.toString()
                factoryID                           = it.FactoryId.toString()
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
            sharedViewModel.showComponentsListingDialog(requireContext(),componentList,false){
                binding.inputLayoutComponent.text = it.toString()
            }
        }
    }

    private fun scanButton(){
        binding.scanBtn.setOnClickListener {
            barcodetext = ""
            binding.etBoxBarcode.text?.clear()
            binding.etBoxDesignName.text?.clear()
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>("ScannedResult")
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

    private fun addButtonFunctionality(updateItemIndex : Int,componentId : Int){
        
        binding.addBtn.setOnClickListener {
            if (binding.inputLayoutWorkshop.text.toString().equals("Workshop",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Workshop!!","OK","")
            }
            else if (binding.inputLayoutComponent.text.toString().equals("Components",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Select Component!!","OK","")
            }
            else if (binding.etBoxBarcode.text.toString().isEmpty()){
                DialogHelper.Alert_Selection(requireContext(),"Please Enter Barcode!!","OK","")
            }
            else if (binding.etBoxComponentQty.text.toString().isEmpty() || binding.etBoxComponentQty.text.toString().equals("0",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please Enter Component Quantity!!","OK","")
            }
            else {

                if (binding.addBtn.text == "Add"){
                    binding.inputLayoutComponent.isEnabled      = true
                    binding.inputLayoutComponent.isClickable    = true
                    println(isComponentNameExists(componentData,binding.inputLayoutComponent.text.toString().trim()))

                    val componentName = binding.inputLayoutComponent.text.toString().trim()
                    if (isComponentNameExists(componentData, componentName)) {
                        // Component name exists in componentData
                        DialogHelper.Alert_Selection(requireContext(), "Item Already Exists!!", "OK", "", onPositiveButtonClick = {
                            binding.inputLayoutComponent.text = "Components"
                            binding.etBoxComponentQty.text?.clear()
                        })
                    }
                    else {
                        // Component name doesn't exist in componentData
                        val component = GetDataByBarcodeResponse.Components()
                        component.ComponentsId = 0
                        component.PipeId = barcodeScannedPipeID.toString().toInt()
                        component.Barcode = binding.etBoxBarcode.text.toString().trim()
                        component.ComponentsName = componentName
                        component.ComponentsQty = binding.etBoxComponentQty.text.toString()
                        componentData.add(component)
                        productManufactureItemAdapter?.updateList(componentData)
                        DialogHelper.Alert_Selection(requireContext(), "Item Added Successfully!!", "OK", "", onPositiveButtonClick = {
                            binding.inputLayoutComponent.text = "Components"
                            binding.etBoxComponentQty.text?.clear()
                            binding.inputLayoutComponent.isEnabled = true
                            binding.inputLayoutComponent.isClickable = true
                        })
                        if (componentData.isEmpty()){
                            binding.recyclerviewDetails.root.visibility = GONE
                        }
                        else{
                            binding.recyclerviewDetails.root.visibility = VISIBLE
                        }
                    }
                }
                else{

                    if (updateItemIndex != -1 && updateItemIndex < componentData.size) {

                        val existingComponent = componentData[updateItemIndex]
                        existingComponent.ComponentsId = componentId
                        existingComponent.PipeId = barcodeScannedPipeID.toString().toInt()
                        existingComponent.Barcode = binding.etBoxBarcode.text.toString().trim()
                        existingComponent.ComponentsName =
                            binding.inputLayoutComponent.text.toString().trim()
                        existingComponent.ComponentsQty = binding.etBoxComponentQty.text.toString()

                        // Update the adapter with the updated list

                        productManufactureItemAdapter?.updateList(componentData)

                        DialogHelper.Alert_Selection(
                            requireContext(),
                            "Item Updated Successfully!!",
                            "OK",
                            "",
                            onPositiveButtonClick = {
                                binding.inputLayoutComponent.text = "Components"
                                binding.etBoxComponentQty.text?.clear()
                                binding.addBtn.text = "Add"
                                binding.inputLayoutComponent.isEnabled      = true
                                binding.inputLayoutComponent.isClickable    = true
                            })

                        // Update UI visibility based on componentData list
                        if (componentData.isEmpty()) {
                            binding.recyclerviewDetails.root.visibility = View.GONE
                        } else {
                            binding.recyclerviewDetails.root.visibility = View.VISIBLE
                        }
                    }else{
                        println("123456")
                    }

                }

            }
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    fun isComponentNameExists(componentData: List<GetDataByBarcodeResponse.Components>, componentName: String): Boolean {
        for (component in componentData) {
            if (component.ComponentsName.toString().trim() == componentName.trim()) {
                return true
            }
        }
        return false
    }

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
        }
        else{
            binding.recyclerviewDetails.root.visibility = VISIBLE
        }


        binding.recyclerviewDetails.tvFirst.text            =   "Name"
        binding.recyclerviewDetails.tvFourth.text            =   "Comp. Qty"
        binding.recyclerviewDetails.tvLast.text           =   "Action"

        binding.recyclerviewDetails.tvFirst.visibility            =   VISIBLE
        binding.recyclerviewDetails.tvLast.visibility            =   VISIBLE
        binding.recyclerviewDetails.tvFourth.visibility            =   VISIBLE

        binding.recyclerviewDetails.firstview.visibility            =   VISIBLE
        binding.recyclerviewDetails.thirdview.visibility            =   VISIBLE

        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerviewItemList                                = binding.recyclerviewDetails.itemListRv
        recyclerviewItemList.layoutManager                      = linearLayoutManager
        recyclerviewItemList.itemAnimator                       = DefaultItemAnimator()

        productManufactureItemAdapter                           = ProductManufactureItemAdapter()

        productManufactureItemAdapter?.apply {
            updateList(componentData)

            onEditItemClick = {
                val index = componentData.indexOf(it)

                binding.etBoxBarcode.setText(it.Barcode.toString())
                binding.etBoxComponentQty.setText(it.ComponentsQty.toString())
                binding.inputLayoutComponent.text = it.ComponentsName.toString()

                binding.addBtn.text = "Update"

                binding.inputLayoutComponent.isEnabled      = false
                binding.inputLayoutComponent.isClickable    = false

                addButtonFunctionality(index,it.ComponentsId.toString().toInt())
//                updateButtonFunctionality(it.PipeId.toString())
            }

            onDeleteItemClick = {
                binding.etBoxComponentQty.setText(it.ComponentsQty.toString())
                binding.inputLayoutComponent.setText(it.ComponentsName.toString())
                binding.addBtn.text                 = "Add"
                componentData.remove(it)
                updateList(componentData)
                if (componentData.isEmpty()){
                    binding.inputLayoutComponent.text = "Components"
                    binding.etBoxComponentQty.text?.clear()
                    binding.addBtn.text = "Add"
                    binding.inputLayoutComponent.isEnabled      = true
                    binding.inputLayoutComponent.isClickable    = true
                    val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                    productManufactureItemAdapter?.updateList(hostList)
                    binding.recyclerviewDetails.root.visibility = GONE
                }
                else{
                    binding.addBtn.text = "Add"
                    binding.inputLayoutComponent.isEnabled      = true
                    binding.inputLayoutComponent.isClickable    = true
                    binding.recyclerviewDetails.root.visibility = VISIBLE
                }
            }
        }
        recyclerviewItemList.adapter                            = productManufactureItemAdapter

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
        sharedViewModel.api_service(requireContext(),getDataByBarcodeUrl,map,{},{ getDataByBarcoderesponse ->
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

                            factorylist.forEach { factorylist ->
                                if (it.FactoryId.toString() == factorylist.FactoryId.toString()){
                                    binding.inputLayoutWorkshop.text = factorylist.FactoryName.toString()
                                }
                            }
                            factoryID = it.FactoryId.toString()
                            barcode =   it.Barcode.toString()
                        }

                        getDataByBarcodeResponse.pipes.first().Components.let {
                            componentData = it
                            binding.inputLayoutComponent.text = "Components"
                            binding.etBoxComponentQty.text?.clear()
                            binding.addBtn.text = "Add"
                            productManufactureItemAdapter?.updateList(it)

                            if (componentData.isEmpty()){
                                binding.recyclerviewDetails.root.visibility = GONE
                            }else{
                                binding.recyclerviewDetails.root.visibility = VISIBLE

                            }
                        }



                    }
                    else{
                        binding.etBoxBarcode.text?.clear()
                        binding.etBoxDesignName.text?.clear()
                        binding.recyclerviewDetails.root.visibility = GONE

                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        productManufactureItemAdapter?.updateList(hostList)
                    }

                    addButtonFunctionality(0,0)
                    if (barcodeScannedPipeID == "0"){
                        saveButtonfunctionality()

                        //Add
                    }else{
                        saveButtonfunctionality()

                        //update
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
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{},{ getWorkshopResponse ->
            println(getWorkshopResponse)
            val workshopListResponse: WorkshopListResponse? = AppHelper.convertJsonToModel(getWorkshopResponse)

            if (workshopListResponse != null) {
                println(workshopListResponse)
                factorylist = workshopListResponse.Factorylist
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

        sharedViewModel.api_service(requireContext(),updatePipeEntryUrl,requestMap,{},{ getUpdatePipeEntryResponse ->
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
                        componentData.clear()
                        binding.btnCancel.performClick()
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

        sharedViewModel.api_service(requireContext(),updatePipeEntryUrl,requestMap,{},{ getUpdatePipeEntryResponse ->
            println(getUpdatePipeEntryResponse)

            val gson = Gson()
            val responseJson = gson.fromJson(getUpdatePipeEntryResponse, JsonObject::class.java)

            if (responseJson["Status"].asString.equals("Success",true)){

                if (responseJson["ErrorMessage"].asString.equals("Pipe added successfully",true)){

                    DialogHelper.Alert_Selection(requireContext(),responseJson["ErrorMessage"].asString,"OK","", onPositiveButtonClick = {
                        binding.inputLayoutWorkshop.text    = "WorkShop"
                        binding.inputLayoutComponent.text   = "Components"
                        binding.etBoxBarcode.text?.clear()
                        binding.etBoxDesignName.text?.clear()
                        binding.etBoxComponentQty.text?.clear()
                        binding.addBtn.text = "Add"
                        val hostList = ArrayList<GetDataByBarcodeResponse.Components>()
                        productManufactureItemAdapter?.updateList(hostList)
                        binding.recyclerviewDetails.root.visibility = GONE
                        binding.btnCancel.performClick()
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