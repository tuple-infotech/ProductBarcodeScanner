package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProductionReportBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.model.getProductWarehouseDataResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductionReportAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductionWarehouseListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ProductionReportFragment : Fragment() {

    //region VARIABLES
    private var _binding                    : FragmentProductionReportBinding?                                  = null
    private val binding                     get()                                                   = _binding!!
    lateinit var prefs : SharedPreferences
    private val c                                           : Calendar                              = Calendar.getInstance()
    private var factoryID = "0"
    private var warehouseID = "0"
    private var fromDate = "0"
    private var toDate = "0"
    private val sharedViewModel             : SharedViewModel by viewModels()
    private var productionReportAdapter               : ProductionReportAdapter?        = null
    private var productionWarehouseListingAdapter               : ProductionWarehouseListingAdapter?        = null
    private var productsData                :   ArrayList<ProductionDetailsResponse.Products>          =       arrayListOf()
    private var productWarehouseData                :   ArrayList<getProductWarehouseDataResponse.Products>          =       arrayListOf()
    private var isFromWarehouse             : Boolean  = false

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFromWarehouse = it.getBoolean("isfromwarehouse")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentProductionReportBinding.inflate(inflater, container, false)

        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()
        return binding.root
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)

        if (isFromWarehouse){
            binding.selectWorkshopTv.visibility = GONE
            binding.recyclerviewDetails.srNo.text        = "Ds. Name"
            binding.recyclerviewDetails.compoName.text   = "Barcode"
            binding.recyclerviewDetails.compoQty.text    = "F. Name"
            binding.recyclerviewDetails.action.visibility = GONE
            initProductWarehouseItem()

        }else{
            binding.selectWorkshopTv.visibility = VISIBLE
            binding.recyclerviewDetails.srNo.text        = "Ds. Name"
            binding.recyclerviewDetails.compoName.text   = "Barcode"
            binding.recyclerviewDetails.compoQty.text    = "F. Name"
            binding.recyclerviewDetails.action.text      = "Wh. Name"
            binding.recyclerviewDetails.action.visibility = VISIBLE
            initProductManufactureItem()

        }

        onBackPressed()
        openFromDatePicker()
        openToDatePicker()
        getWorkshopList()
        getWarehouseList()
        filterList()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun filterList(){
        binding.filterBtn.setOnClickListener {

            if (binding.startDateTv.text.toString().equals("-Start Date-",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select start date !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else if (binding.endDateTv.text.toString().equals("-End Date-",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select end date !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else if (binding.selectWarehouseTv.text.toString().equals("Warehouse",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select warehouse !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else {
                if (isFromWarehouse){
                    getProductWarehouseData()
                }else{
                    if (binding.selectWorkshopTv.text.toString().equals("Workshop",true)){
                        DialogHelper.Alert_Selection(requireContext(),"Please select workshop !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
                    }else {
                        getProductionReportDetails()
                    }
                }
            }
        }
    }

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun initWorkshopDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWorkshopTv.setOnClickListener {
            sharedViewModel.showWorkshopListingDialog(requireContext(),itemList,false) {
                binding.selectWorkshopTv.text = it.FactoryName.toString()
                factoryID = it.FactoryId.toString()
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

    private fun openFromDatePicker(){
        var cDay    = c.get(Calendar.DAY_OF_MONTH)
        var cMonth  = c.get(Calendar.MONTH)
        var cYear   = c.get(Calendar.YEAR)

        binding.startDateTv.setOnClickListener {
            val calendarDialog = DatePickerDialog(requireContext(),{ _, year, month, dayOfMonth ->
                cDay            = dayOfMonth
                cMonth          = month
                cYear           = year

//                fromDateDay            = dayOfMonth
//                fromDateMonth          = month
//                fromDateYear           = year

                fromDate = String.format("%04d-%02d-%02d",year, month + 1, dayOfMonth,)
                binding.startDateTv.text = String.format("%02d/%02d/%02d",month + 1, dayOfMonth, year)
            }, cYear, cMonth, cDay )
            calendarDialog.show()
        }
    }

    private fun openToDatePicker(){
        var cDay    = c.get(Calendar.DAY_OF_MONTH)
        var cMonth  = c.get(Calendar.MONTH)
        var cYear   = c.get(Calendar.YEAR)

        binding.endDateTv.setOnClickListener {
            val calendarDialog = DatePickerDialog(requireContext(),{ _, year, month, dayOfMonth ->
                cDay            = dayOfMonth
                cMonth          = month
                cYear           = year
                toDate = String.format("%04d-%02d-%02d",year, month + 1, dayOfMonth,)
                binding.endDateTv.text = String.format("%02d/%02d/%02d",month + 1, dayOfMonth, year)
            }, cYear, cMonth, cDay )
            /*            val c2                              = Calendar.getInstance()
                        c2.add(Calendar.DAY_OF_MONTH,fromDateDay)
                        c2.add(Calendar.MONTH,fromDateMonth)
                        c2.add(Calendar.YEAR,fromDateYear)
                        calendarDialog.datePicker.minDate   = c2.timeInMillis*/
            calendarDialog.show()
        }

    }

    private fun initProductManufactureItem(){
        if (productsData.isEmpty()){
            binding.recyclerviewDetails.root.visibility = GONE
        }else{
            binding.recyclerviewDetails.root.visibility = VISIBLE
        }
        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerviewItemList                                = binding.recyclerviewDetails.itemListRv
        recyclerviewItemList.layoutManager                      = linearLayoutManager
        recyclerviewItemList.itemAnimator                       = DefaultItemAnimator()

        productionReportAdapter                           = ProductionReportAdapter()

        productionReportAdapter?.apply {
            updateList(productsData)
            onItemClick = {
                val args = Bundle()
                args.getString("barcode",it.Barcode)
                args.getBoolean("isEditable",false)
                findNavController().navigate(R.id.BarcodeProductDetailsFragment,args)
            }
        }
        recyclerviewItemList.adapter                            = productionReportAdapter

    }

    private fun initProductWarehouseItem(){
        if (productWarehouseData.isEmpty()){
            binding.recyclerviewDetails.root.visibility = GONE
        }else{
            binding.recyclerviewDetails.root.visibility = VISIBLE
        }
        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerviewItemList                                = binding.recyclerviewDetails.itemListRv
        recyclerviewItemList.layoutManager                      = linearLayoutManager
        recyclerviewItemList.itemAnimator                       = DefaultItemAnimator()

        productionWarehouseListingAdapter                           = ProductionWarehouseListingAdapter()

        productionWarehouseListingAdapter?.apply {
            updateList(productWarehouseData)
            onItemClick = {
                val args = Bundle()
                args.getString("barcode",it.Barcode)
                args.getBoolean("isEditable",false)
                findNavController().navigate(R.id.BarcodeProductDetailsFragment,args)
            }

        }
        recyclerviewItemList.adapter                            = productionWarehouseListingAdapter

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
                binding.recyclerviewDetails.itemListRv.visibility = View.GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

   //http://150.129.105.34/api/v1/warehouseApi/getWarehouseList
    private fun getWarehouseList(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_WAREHOUSE_LIST
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{ getWorkshopResponse ->
            println(getWorkshopResponse)
            val workshopListResponse: WorkshopListResponse? = AppHelper.convertJsonToModel(getWorkshopResponse)

            if (workshopListResponse != null) {
                println(workshopListResponse)
                initWarehouseDropDown(workshopListResponse.WarehouseList)
            }
            else {
                binding.recyclerviewDetails.itemListRv.visibility = View.GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    //http://150.129.105.34/api/v1/productionreportrequest/getproductiondetails
    private fun getProductionReportDetails(){
        val requestMap = mapOf<String, Any>(
            "WarehouseId"   to   warehouseID,
            "FactoryId"     to   factoryID,
            "StartDate"     to   fromDate,
            "EndDate"       to   toDate
        )

        val getProductionDetailsurl = prefs.host + UrlEndPoints.GET_PRODUCTION_DETAILS
        sharedViewModel.api_service(requireContext(),getProductionDetailsurl,requestMap,{ getProductionDetailsResponse ->
            println(getProductionDetailsResponse)
            val productionDettailsResponse: ProductionDetailsResponse? = AppHelper.convertJsonToModel(getProductionDetailsResponse)

            if (productionDettailsResponse != null) {
                println(productionDettailsResponse)

                productsData = productionDettailsResponse.products
                productionReportAdapter?.updateList(productsData)

                if (productsData.isEmpty()){
                    binding.recyclerviewDetails.root.visibility = GONE
                }else{
                    binding.recyclerviewDetails.root.visibility = VISIBLE
                }
            }
            else {
                binding.recyclerviewDetails.itemListRv.visibility = View.GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })
    }

    //http://150.129.105.34/api/v1/warehouseApi/getProductWarehouseData
    private fun getProductWarehouseData(){
        val requestMap = mapOf<String, Any>(
            "WarehouseId"   to   warehouseID,
            "StartDate"     to   fromDate,
            "EndDate"       to   toDate
        )
        val getProductionDetailsurl = prefs.host + UrlEndPoints.GET_PRODUCT_WAREHOUSE_DATA
        sharedViewModel.api_service(requireContext(),getProductionDetailsurl,requestMap,{ getProductionDetailsResponse ->
            println(getProductionDetailsResponse)
            val productionDettailsResponse: getProductWarehouseDataResponse? = AppHelper.convertJsonToModel(getProductionDetailsResponse)

            if (productionDettailsResponse != null) {
                println(productionDettailsResponse.products)
                productWarehouseData = productionDettailsResponse.products
                productionWarehouseListingAdapter?.updateList(productWarehouseData)
                if (productWarehouseData.isEmpty()){
                    binding.recyclerviewDetails.root.visibility = GONE
                }else{
                    binding.recyclerviewDetails.root.visibility = VISIBLE
                }
            }
            else {
                binding.recyclerviewDetails.itemListRv.visibility = View.GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })
    }

    //endregion API SERVICE



}