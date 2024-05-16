package com.tupleinfotech.productbarcodescanner.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.data.repository.barcode.BarcodeRepository
import com.tupleinfotech.productbarcodescanner.databinding.DialogSelectListItemBinding
import com.tupleinfotech.productbarcodescanner.model.ProductionDetailsResponse
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.model.getProductWarehouseDataResponse
import com.tupleinfotech.productbarcodescanner.network.NetworkResult
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ComponentsListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.DesignNameListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.VendorListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.WarehouseListingAdapter
import com.tupleinfotech.productbarcodescanner.ui.adapter.WorkshopListingAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor (private val barcodeRepository: BarcodeRepository) : ViewModel() {

    private val _productlist    = MutableLiveData<ArrayList<getProductWarehouseDataResponse.Products>>(ArrayList<getProductWarehouseDataResponse.Products>())
    // The UI collects from this StateFlow to get its state updates
    fun setItemWareHouseDetails(item : ArrayList<getProductWarehouseDataResponse.Products>){
        _productlist.value = item
    }

    private val _categorylist   = MutableStateFlow<ArrayList<ProductionDetailsResponse.Products>>(ArrayList<ProductionDetailsResponse.Products>())
    // The UI collects from this StateFlow to get its state updates
    fun setItemProductionReportProductData(item : ArrayList<ProductionDetailsResponse.Products>){
        _categorylist.value = item
    }

    //region Common Api Calling Method

    fun api_service(
        context: Context,
        apiUrl : String,
        requestMap: Map<String, Any>,
        onLoading: (String) -> Unit = {},
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        viewModelScope.launch {
            try {
                barcodeRepository.api_url_repository(apiUrl,requestMap).collectLatest { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {

                            is NetworkResult.Error      -> {
                                Log.i("==>", "ERROR" + response.message)
                                onFailure(response.message.toString())
                            }
                            is NetworkResult.Loading    -> {
                                Log.i("==>", "LOADING" + response.message)
                                onLoading("LOADING")
                            }
                            is NetworkResult.Success    -> {
                                if (response.data?.isSuccessful == true) {
                                    val responseBodyString = response.data.body()?.string()
                                    onSuccess(responseBodyString.toString())
                                    Log.i("==>", "SUCCESS")
                                    // Handle the JSON string as needed
                                } else {
                                    Log.i("==>", "ERROR")
                                    onFailure(response.message.toString())
                                    // Handle error
                                }
                            }

                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("==>", "Exception: ${e.message}")
            }
        }

    }

    //endregion Common Api Calling Method

    //Action bar with side menu
    fun initActionbarWithSideMenu(requireActivity: Activity){
        val drawerLayout = (requireActivity as MainActivity).findViewById<DrawerLayout>(R.id.drawer_layout)

        (requireActivity as MainActivity).initActionbar("CTS",leftButton = R.drawable.icon_side_menu, rightButton = 0,  leftButtonClick = {
            drawerLayout.openDrawer(GravityCompat.START)
        })
    }

    //Action bar without side menu
    fun initActionbarWithoutSideMenu(activity: Activity,rightButton : Int = 0){
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.drawer_layout)

        (activity as MainActivity).initActionbar("CTS",leftButton = 0, rightButton = rightButton,  leftButtonClick = {
            drawerLayout.openDrawer(GravityCompat.START)
        })
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
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select Workshop"
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
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select Warehouse"
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

    fun showVendorListingDialog(
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
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select Vendor"
        _binding.customActionBar.notificationBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
        val recyclerViewPaymentList                     = _binding.itemListingRv
        recyclerViewPaymentList.layoutManager           = layoutManager
        recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
        val listingAdapter                     : VendorListingAdapter = VendorListingAdapter()
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

        if (isSearchVisible) _binding.searchbox.visibility = View.VISIBLE else _binding.searchbox.visibility =
            View.GONE

        _binding.customActionBar.notificationBtn.setImageResource(R.drawable.ic_close_square)
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select Components"
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

    fun showDesignNameListingDialog(
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
        _binding.customActionBar.notificationBtn.imageTintList = context.resources.getColorStateList(R.color.orange)
        _binding.customActionBar.arrowBnt.visibility = View.GONE
        _binding.customActionBar.setOurText.text = "Select Design Name"
        _binding.customActionBar.notificationBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        val layoutManager : RecyclerView.LayoutManager  = LinearLayoutManager(context)
        val recyclerViewPaymentList                     = _binding.itemListingRv
        recyclerViewPaymentList.layoutManager           = layoutManager
        recyclerViewPaymentList.itemAnimator            = DefaultItemAnimator()
        val listingAdapter                     : DesignNameListingAdapter = DesignNameListingAdapter()
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
}