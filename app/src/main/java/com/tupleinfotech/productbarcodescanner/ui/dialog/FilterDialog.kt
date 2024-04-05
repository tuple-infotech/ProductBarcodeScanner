package com.tupleinfotech.productbarcodescanner.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.DialogFilterBinding
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
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
class FilterDialog : DialogFragment() {

    private var _binding                    : DialogFilterBinding?      = null
    private val binding                     get()                               = _binding!!
    private val c                                           : Calendar                              = Calendar.getInstance()
    private var fromDate = "0"
    private var toDate = "0"
    private val sharedViewModel             : SharedViewModel by viewModels()
    private var factoryID = "0"
    private var warehouseID = "0"
    private var wareHouseName = ""
    private var factoryName = ""
    private var isFromWarehouse             : Boolean  = false
    lateinit var prefs : SharedPreferences
    private var args = Bundle()
    var isFromWarehouseFilterClick : ((String,String,String,String) -> Unit)?           = null
    var isFromWarehouseAndWorkShopFilterClick : ((String,String,String,String,String,String) -> Unit)?           = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isFromWarehouse     = it.getBoolean("isfromwarehouse")
            fromDate            = it.getString("FromDate",fromDate)
            toDate              = it.getString("ToDate",toDate)
            factoryID           = it.getString("FactoryID",factoryID)
            warehouseID         = it.getString("WareHouseID",warehouseID)
            wareHouseName       = it.getString("WareHouseName",wareHouseName)
            factoryName         = it.getString("FactoryName",factoryName)
        }
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding    = DialogFilterBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)


        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCanceledOnTouchOutside(false)

        init()

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        //val height = (resources.displayMetrics.heightPixels * 0.30).toInt()
        dialog!!.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        //THIS WILL MAKE WIDTH 90% OF SCREEN
        //HEIGHT WILL BE WRAP_CONTENT
        // dialog!!.window!!.setLayout(width, height);
    }

    private fun init(){

        binding.headingConstraint.arrowBnt.visibility = View.GONE
        binding.headingConstraint.notificationBtn.visibility = View.VISIBLE
//        binding.headingConstraint.notificationBtn.setImageResource(R.drawable.ic_close_square)
        // Get the drawable from resources
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_square)
        drawable?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.orange), PorterDuff.Mode.SRC_IN)
        binding.headingConstraint.notificationBtn.setImageDrawable(drawable)

        binding.headingConstraint.notificationBtn.setOnClickListener{
            dismiss()
        }

        binding.headingConstraint.setOurText.text = "Filter"

        if (isFromWarehouse){
            binding.selectWorkshopTv.visibility = View.GONE
            binding.startDateTv.text        = fromDate.trim()
            binding.endDateTv.text          = toDate.trim()

            if (wareHouseName.toString().isNotEmpty() && wareHouseName.toString().isNotBlank()){
                binding.selectWarehouseTv.text  = wareHouseName.trim()
            }else{
                binding.selectWarehouseTv.text  = "Warehouse"
            }

            getWarehouseList()
        }
        else{
            binding.selectWorkshopTv.visibility = View.VISIBLE
            binding.startDateTv.text        = fromDate.trim()
            binding.endDateTv.text          = toDate.trim()

            if (wareHouseName.toString().isNotEmpty() && wareHouseName.toString().isNotBlank()){
                binding.selectWarehouseTv.text  = wareHouseName.trim()
            }else{
                binding.selectWarehouseTv.text  = "Warehouse"
            }

            if (factoryName.toString().isNotEmpty() && factoryName.toString().isNotBlank()){
                binding.selectWorkshopTv.text  = factoryName.trim()
            }else{
                binding.selectWorkshopTv.text  = "Workshop"
            }
            getWarehouseList()
            getWorkshopList()
        }
        filterList()
        openFromDatePicker()
        openToDatePicker()
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
                binding.startDateTv.text = String.format("%04d-%02d-%02d",month + 1, dayOfMonth, year)
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
                binding.endDateTv.text = String.format("%04d-%02d-%02d",month + 1, dayOfMonth, year)
            }, cYear, cMonth, cDay )

            calendarDialog.show()
        }

    }

    private fun initWorkshopDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWorkshopTv.setOnClickListener {
            sharedViewModel.showWorkshopListingDialog(requireContext(),itemList,false) {
                binding.selectWorkshopTv.text = it.FactoryName.toString()
                factoryID       = it.FactoryId.toString()
                factoryName     = it.FactoryName.toString()
            }
        }

    }

    private fun initWarehouseDropDown(itemList: ArrayList<WorkshopListResponse.List>){
        binding.selectWarehouseTv.setOnClickListener {
            sharedViewModel.showWarehouseListingDialog(requireContext(),itemList,false) {
                binding.selectWarehouseTv.text = it.WarehouseName.toString()
                warehouseID     = it.WarehouseId.toString()
                wareHouseName   = it.WarehouseName.toString()
            }
        }

    }

    private fun filterList(){
        binding.filterBtn.setOnClickListener {

            if (binding.startDateTv.text.toString().equals("-Start Date-",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select start date !!",resources.getString(
                    R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else if (binding.endDateTv.text.toString().equals("-End Date-",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select end date !!",resources.getString(
                    R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else if (binding.selectWarehouseTv.text.toString().equals("Warehouse",true)){
                DialogHelper.Alert_Selection(requireContext(),"Please select warehouse !!",resources.getString(
                    R.string.singlebtntext),"", showNegativeButton = false,)
            }
            else {
                if (isFromWarehouse){

                    isFromWarehouseFilterClick?.invoke(fromDate.trim(),toDate.trim(),wareHouseName.trim(),warehouseID.trim())
//                    args.putString("FromDate",fromDate.trim())
//                    args.putString("ToDate",toDate.trim())
//                    args.putString("WareHouseName",wareHouseName.trim())
//                    args.putString("WareHouseID",warehouseID.trim())
//                    findNavController().previousBackStackEntry?.savedStateHandle?.set("WarehouseResult", args)
//                    findNavController().popBackStack()
                }
                else{
                    if (binding.selectWorkshopTv.text.toString().equals("Workshop",true)){
                        DialogHelper.Alert_Selection(requireContext(),"Please select workshop !!",resources.getString(
                            R.string.singlebtntext),"", showNegativeButton = false,)
                    }
                    else {
                        isFromWarehouseAndWorkShopFilterClick?.invoke(fromDate.trim(),toDate.trim(),wareHouseName.trim(),warehouseID.trim(),factoryName.trim(),factoryID.trim())
//                        args.putString("FromDate",fromDate.trim())
//                        args.putString("ToDate",toDate.trim())
//                        args.putString("WareHouseName",wareHouseName.trim())
//                        args.putString("WareHouseID",warehouseID.trim())
//                        args.putString("FactoryName",factoryName.trim())
//                        args.putString("FactoryID",factoryID.trim())
//                        findNavController().previousBackStackEntry?.savedStateHandle?.set("WarehouseWorkshopResult", args)
//                        findNavController().popBackStack()
                    }
                }
            }
        }
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
                initWorkshopDropDown(workshopListResponse.Factorylist)
            }
            else {

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

}