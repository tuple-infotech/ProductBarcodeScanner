package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
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
import com.tupleinfotech.productbarcodescanner.model.getProductWarehouseDataResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductionReportAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProductionWarehouseListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.dialog.FilterDialog
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.isWithinBounds
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("SetTextI18n","ClickableViewAccessibility")
class ProductionReportFragment : Fragment() {

    //region VARIABLES
    private var _binding                                : FragmentProductionReportBinding?                          = null
    private val binding                                 get()                                                       = _binding!!
    lateinit var prefs                                  : SharedPreferences
    private val sharedViewModel                         : SharedViewModel                                           by viewModels()
    private var factoryID                               : String                                                    = "0"
    private var warehouseID                             : String                                                    = "0"
    private var factoryName                             : String                                                    = ""
    private var wareHouseName                           : String                                                    = ""
    private var fromDate                                : String                                                    = "0"
    private var toDate                                  : String                                                    = "0"
    private var productionReportAdapter                 : ProductionReportAdapter?                                  = ProductionReportAdapter()
    private var productionWarehouseListingAdapter       : ProductionWarehouseListingAdapter?                        = ProductionWarehouseListingAdapter()
    private var productsData                            : ArrayList<ProductionDetailsResponse.Products>             = arrayListOf()
    private var productWarehouseData                    : ArrayList<getProductWarehouseDataResponse.Products>       = arrayListOf()
    private var isFromWarehouse                         : Boolean                                                   = false
    private var isDragging                              : Boolean                                                   = false
    private var dX                                      : Float                                                     = 0f
    private var dY                                      : Float                                                     = 0f

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFromWarehouse = it.getBoolean("isfromwarehouse")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding    = FragmentProductionReportBinding.inflate(inflater, container, false)
        prefs       = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()
        return binding.root
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)
        fromDate    = AppHelper.getCurrentDate1()
        toDate      = AppHelper.getCurrentDate1()

        println(isFromWarehouse)

        if (isFromWarehouse){
//            binding.selectWorkshopTv.visibility = GONE
            binding.recyclerviewDetails.tvFirst.text           =   "Design Name"
            binding.recyclerviewDetails.tvThird.text          =   "Barcode"
            binding.recyclerviewDetails.tvSecond.text            =   "Wh. Name"

            binding.recyclerviewDetails.firstview.visibility    =   VISIBLE
            binding.recyclerviewDetails.tvFourth.visibility     =   GONE
            binding.recyclerviewDetails.thirdview.visibility    =   VISIBLE
            binding.recyclerviewDetails.fourthview.visibility   =   GONE


            if (productWarehouseData.isEmpty()){
                getProductWarehouseData()
            }
            else {
                sharedViewModel.setItemWareHouseDetails(productWarehouseData)
            }

            initProductWarehouseItem()

        }
        else{
//            binding.selectWorkshopTv.visibility = VISIBLE

            binding.recyclerviewDetails.tvFirst.text           =   "Ds Name"
            binding.recyclerviewDetails.tvThird.text          =   "Barcode"
            binding.recyclerviewDetails.tvFourth.text            =   "Fh Name"
            binding.recyclerviewDetails.tvSecond.text            =   "Wh Name"

            binding.recyclerviewDetails.firstview.visibility    =   VISIBLE
            binding.recyclerviewDetails.tvFourth.visibility     =   VISIBLE
            binding.recyclerviewDetails.thirdview.visibility    =   VISIBLE
            binding.recyclerviewDetails.fourthview.visibility   =   VISIBLE


            if (productsData.isEmpty()){
                getProductionReportDetails()
            }
            else {
                sharedViewModel.setItemProductionReportProductData(productsData)
            }

            initProductManufactureItem()

        }

        onBackPressed()
        filterbtnclick()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    //Filter button click and movable functionality
    private fun filterbtnclick(){

        binding.filter.setImageResource(R.drawable.icon_filter)

        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val filterDialog = FilterDialog()
                filterDialog.showsDialog
                val args            = Bundle()
                args.putBoolean("isfromwarehouse"   ,isFromWarehouse)
                args.putString("FromDate"           ,fromDate)
                args.putString("ToDate"             ,toDate)
                args.putString("FactoryID"          ,factoryID)
                args.putString("WareHouseID"        ,warehouseID)
                args.putString("WareHouseName"      ,wareHouseName)
                args.putString("FactoryName"        ,factoryName)

                filterDialog.arguments  = args
                filterDialog.show(childFragmentManager, "dialog")
                filterDialog.isFromWarehouseFilterClick = { fromdate,todate,warehousename,warehouseid ->
                    filterDialog.dismiss()
                    fromDate        = fromdate.toString()
                    toDate          = todate.toString()
                    wareHouseName   = warehousename.toString()
                    warehouseID     = warehouseid.toString()

                    getProductWarehouseData()

                }
                filterDialog.isFromWarehouseAndWorkShopFilterClick = { fromdate,todate,warehousename,warehouseid,factoryname,factoryid ->
                    filterDialog.dismiss()
                    fromDate        = fromdate.toString()
                    toDate          = todate.toString()
                    wareHouseName   = warehousename.toString()
                    warehouseID     = warehouseid.toString()
                    factoryName     = factoryname.toString()
                    factoryID       = factoryid.toString()
                    getProductionReportDetails()

                }
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
        })

        binding.filter.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            val touchX          = event.rawX
            val touchY          = event.rawY
            val screenWidth     = resources.displayMetrics.widthPixels
            val screenHeight    = resources.displayMetrics.heightPixels
            val bottomMargin    = 100 // Adjust the margin as needed
            val topMargin       = 230

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    isDragging  = false
                    dX          = binding.filter.x - touchX
                    dY          = binding.filter.y - touchY
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = touchX + dX
                    val newY = touchY + dY
                    if (isWithinBounds(newX, newY, screenWidth, screenHeight, topMargin, bottomMargin, binding.filter.width, binding.filter.height)) {
                        binding.filter.animate().x(newX).y(newY).setDuration(0).start()
                        isDragging = true
                    }
                }
                MotionEvent.ACTION_UP   -> {
                    if (!isDragging) {}
                }

            }
            true
        }
    }

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

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
                args.putString("barcode",it)
                args.putBoolean("isEditable",false)
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
                args.putString("barcode",it)
                args.putBoolean("isEditable",false)
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

    //http://150.129.105.34/api/v1/productionreportrequest/getproductiondetails
    private fun getProductionReportDetails(){
        val requestMap = mapOf<String, Any>(
            "WarehouseId"   to   warehouseID,
            "FactoryId"     to   factoryID,
            "StartDate"     to   fromDate,
            "EndDate"       to   toDate
        )

        val getProductionDetailsurl = prefs.host + UrlEndPoints.GET_PRODUCTION_DETAILS
        sharedViewModel.api_service(requireContext(),getProductionDetailsurl,requestMap,{},{ getProductionDetailsResponse ->
            println(getProductionDetailsResponse)
            val productionDettailsResponse: ProductionDetailsResponse? = AppHelper.convertJsonToModel(getProductionDetailsResponse)

            if (productionDettailsResponse != null) {
                println(productionDettailsResponse)

                productsData = productionDettailsResponse.products
                sharedViewModel.setItemProductionReportProductData(productsData)
                productionReportAdapter?.updateList(productsData)

                if (productsData.isEmpty()){
                    Toast.makeText(requireContext(),productionDettailsResponse.ErrorMessage.toString(),Toast.LENGTH_SHORT).show()
                    binding.recyclerviewDetails.root.visibility = GONE
                }
                else{
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
        sharedViewModel.api_service(requireContext(),getProductionDetailsurl,requestMap,{},{ getProductionDetailsResponse ->
            println(getProductionDetailsResponse)
            val productionDettailsResponse: getProductWarehouseDataResponse? = AppHelper.convertJsonToModel(getProductionDetailsResponse)

            if (productionDettailsResponse != null) {
                println(productionDettailsResponse.products)
                productWarehouseData = productionDettailsResponse.products
                productionWarehouseListingAdapter?.updateList(productWarehouseData)
                sharedViewModel.setItemWareHouseDetails(productWarehouseData)

                if (productWarehouseData.isEmpty()){
                    Toast.makeText(requireContext(),productionDettailsResponse.ErrorMessage.toString(),Toast.LENGTH_SHORT).show()
                    binding.recyclerviewDetails.root.visibility = GONE
                }
                else{
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