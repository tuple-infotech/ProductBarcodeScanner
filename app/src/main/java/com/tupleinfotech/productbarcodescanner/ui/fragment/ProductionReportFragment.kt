package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.DialogSelectListItemBinding
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProductionReportBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductionReportAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.WarehouseListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.WorkshopListingAdapter
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
    private var productsData                :   ArrayList<ProductionDetailsResponse.Products>          =       arrayListOf()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        onBackPressed()
        openFromDatePicker()
        openToDatePicker()
        getWorkshopList()
        getWarehouseList()
        filterList()
        initProductManufactureItem()
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
            else if (binding.selectWorkshopTv.text.toString().equals("Workshop",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select workshop !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else {
                getProductionReportDetails()
            }
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS
    private fun initWorkshopDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWorkshopTv.setOnClickListener {
            showWorkshopListingDialog(requireContext(),itemList,false) {
                binding.selectWorkshopTv.text = it.FactoryName.toString()
                factoryID = it.FactoryId.toString()
            }
        }

    }

    private fun initWarehouseDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWarehouseTv.setOnClickListener {
            showWarehouseListingDialog(requireContext(),itemList,false) {
                binding.selectWarehouseTv.text = it.WarehouseName.toString()
                warehouseID = it.WarehouseId.toString()
            }
        }

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

        if (isSearchVisible) _binding.searchbox.visibility = View.VISIBLE else _binding.searchbox.visibility =
            View.GONE

        _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(
            R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
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

    fun showWarehouseListingDialog(
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

        if (isSearchVisible) _binding.searchbox.visibility = View.VISIBLE else _binding.searchbox.visibility =
            View.GONE

        _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(
            R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select"
        _binding.customActionBar.notificationBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
        val recyclerViewPaymentList                     = _binding.itemListingRv
        recyclerViewPaymentList.layoutManager           = layoutManager
        recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
        val listingAdapter                     : WarehouseListingAdapter = WarehouseListingAdapter()
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

        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerviewItemList                                = binding.itemListRv
        recyclerviewItemList.layoutManager                      = linearLayoutManager
        recyclerviewItemList.itemAnimator                       = DefaultItemAnimator()

        productionReportAdapter                           = ProductionReportAdapter()

        productionReportAdapter?.apply {
            updateList(productsData)

        }
        recyclerviewItemList.adapter                            = productionReportAdapter

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
                binding.itemListRv.visibility = View.GONE

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
                binding.itemListRv.visibility = View.GONE

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
            }
            else {
                binding.itemListRv.visibility = View.GONE

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })
    }

    //endregion API SERVICE



}