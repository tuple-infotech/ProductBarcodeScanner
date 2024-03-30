package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentQuickInfoBinding
import com.tupleinfotech.productbarcodescanner.model.AccessRights
import com.tupleinfotech.productbarcodescanner.ui.adapter.QuickInfoAdapter
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickInfoFragment : Fragment() {

    //region VARIABLES

    private var _binding                                    : FragmentQuickInfoBinding?             =  null
    private val binding                                     get()                                   =  _binding!!
    private var quickInfoAdapter                            : QuickInfoAdapter?                     = QuickInfoAdapter()
    private var accessid                                    : String                                = ""
    private val sortedList                                  : ArrayList<AccessRights.Access>        = arrayListOf()
    private var dialogShown                                 : Boolean                               = false

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentQuickInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        onBackPressed()
        initDashboardRights()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS


    private fun initDashboardRights(){

        val jsonFileString = AppHelper.getJsonDataFromAsset(requireContext(), "dashboardAccessrights.json")
        if (jsonFileString != null) {
            Log.i("data", jsonFileString)
        }

        val gson = Gson()
        val response = gson.fromJson(jsonFileString, AccessRights::class.java)

        println(findChildRights(response.accessrightsArray,"Quick Info"))
        initQuickInfoMenu()

    }

    private fun findChildRights(rights: ArrayList<AccessRights.Access>, parentName : String) : ArrayList<AccessRights.Access> {

        for (access in rights){
            if (access.accessname.equals(parentName)){
                accessid = access.accessid.toString()
            }
            if (access.parentid == accessid && access.accessrights.equals("Y")){
                sortedList.addAll(rights.filter { item -> item == access })
            }
        }

        return sortedList
    }

    private fun initQuickInfoMenu(){

        val gridLayoutManager : RecyclerView.LayoutManager      = GridLayoutManager(requireActivity(),2)
        val recyclerViewItemList                                = binding.quickInfoMenuRv
        recyclerViewItemList.layoutManager                      = gridLayoutManager
        recyclerViewItemList.itemAnimator                       = DefaultItemAnimator()
        quickInfoAdapter                                        = QuickInfoAdapter()

        for (imageItem in sortedList){
            when(imageItem.accessname){
                "Categories"            -> imageItem.staticimage = R.drawable.ic_action_settings
                "Items"                 -> imageItem.staticimage = R.drawable.ic_action_settings
                "Customers"             -> imageItem.staticimage = R.drawable.ic_action_settings
                "Total Users"           -> imageItem.staticimage = R.drawable.ic_action_settings
                "Delivery Boy"          -> imageItem.staticimage = R.drawable.ic_action_settings
                "Total Orders"          -> imageItem.staticimage = R.drawable.ic_action_settings
                "Total Sales"           -> imageItem.staticimage = R.drawable.ic_action_settings
                "Low Stock Items"       -> imageItem.staticimage = R.drawable.ic_action_settings
                "Delivery Boy Wallet"   -> imageItem.staticimage = R.drawable.ic_action_settings
                "New Arrival Items"     -> imageItem.staticimage = R.drawable.ic_action_settings
                "Total Size"            -> imageItem.staticimage = R.drawable.ic_action_settings
                "Category Pending"      -> imageItem.staticimage = R.drawable.ic_action_settings
                "Item Pending"          -> imageItem.staticimage = R.drawable.ic_action_settings
                "Size Pending"          -> imageItem.staticimage = R.drawable.ic_action_settings
            }
        }
        
        quickInfoAdapter?.apply {
            updateList(sortedList)
        }

        recyclerViewItemList.adapter                            = quickInfoAdapter
    }

    //Show Exit Alert popup
    private fun showDialog() {
        DialogHelper.Alert_Selection(
            context = requireContext(),
            message = AlertMsgs.closeApp,
            positiveButtonTitle = resources.getString(R.string.positivebtntext),
            negativeButtonTitle = resources.getString(R.string.negetivebtntext),
            onPositiveButtonClick = {
                activity?.finishAffinity()
            },
            onNegativeButtonClick = {
                dialogShown = false
            },
            showNegativeButton = true,
            onDismiss = {
                dialogShown = false
            }
        )
    }
    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS

    private fun onBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!dialogShown) {
                    dialogShown = true
                    showDialog()
                    dialogShown = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    //endregion API SERVICE

}