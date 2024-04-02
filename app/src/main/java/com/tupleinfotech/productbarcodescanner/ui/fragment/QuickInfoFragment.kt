package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentQuickInfoBinding
import com.tupleinfotech.productbarcodescanner.model.QuickInfoDataResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.QuickInfoAdapter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickInfoFragment : Fragment() {

    //region VARIABLES

    private var _binding                                    : FragmentQuickInfoBinding?             =  null
    private val binding                                     get()                                   =  _binding!!
    private lateinit var prefs                              : SharedPreferences
    private var quickInfoAdapter                            : QuickInfoAdapter?                     = QuickInfoAdapter()
    private var dialogShown                                 : Boolean                               = false
    private val sharedViewModel                             : SharedViewModel                       by viewModels()
    private var quickInfoDataCount                          : QuickInfoDataResponse.QuickInfo       = QuickInfoDataResponse.QuickInfo()
    private val bottomNavigationUnselectedItemImageList     : ArrayList<Int>                        = arrayListOf()
    private val bottomNavigationItemName                    : ArrayList<String>                     = arrayListOf()

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
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        onBackPressed()
//        initQuickInfoMenu()
        serviceApiQuickInfoData()
        //initBottomNavigation()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    /*    private fun initDashboardRights(){

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
        }*/

    private fun initQuickInfoMenu(quickInfoListData : ArrayList<Pair<String,Int>>, QuickInfoDataCount : QuickInfoDataResponse.QuickInfo){

        val gridLayoutManager : RecyclerView.LayoutManager      = GridLayoutManager(requireActivity(),2)
        val recyclerViewItemList                                = binding.quickInfoMenuRv
        recyclerViewItemList.layoutManager                      = gridLayoutManager
        recyclerViewItemList.itemAnimator                       = DefaultItemAnimator()
        quickInfoAdapter                                        = QuickInfoAdapter()

        quickInfoAdapter?.apply {
            updateList(quickInfoListData,QuickInfoDataCount)
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

    private fun initBottomNavigation(){

        bottomNavigationUnselectedItemImageList.add(R.drawable.style_bottom_nav_home)
        bottomNavigationUnselectedItemImageList.add(R.drawable.style_bottom_nav_inventory)
        bottomNavigationUnselectedItemImageList.add(R.drawable.style_bottom_nav_warehouse)
        bottomNavigationUnselectedItemImageList.add(R.drawable.style_bottom_nav_profile)

        bottomNavigationItemName.add("Quick Info")
        bottomNavigationItemName.add("Product")
        bottomNavigationItemName.add("Warehouse")
        bottomNavigationItemName.add("Profile")

        (requireActivity() as MainActivity).initBottomNavigation(true,4,bottomNavigationItemName,bottomNavigationUnselectedItemImageList)

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

    private fun serviceApiQuickInfoData(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

        val quickInfoDataUrl = prefs.host + UrlEndPoints.DASHBOARD_QUICK_INFO
        sharedViewModel.api_service(requireContext(),quickInfoDataUrl,requestMap,{ quickInfoDataResponse ->
            println(quickInfoDataResponse)

            val quickInfoData: QuickInfoDataResponse? = AppHelper.convertJsonToModel(quickInfoDataResponse)

            quickInfoData?.let { response ->

                if (response.Status.equals("Success",true)){

//                    quickResponseAdapterData.QuickInfoDetails   = response.QuickInfoDetails
                    quickInfoDataCount = response.QuickInfoDetails!!

                    val quickResponseAdapterData : ArrayList<Pair<String,Int>> = arrayListOf()
                    quickResponseAdapterData.add(Pair("Total Barcode"   ,R.drawable.icon_quick_info_category))
                    quickResponseAdapterData.add(Pair("Total Production",R.drawable.icon_quick_info_category))
                    quickResponseAdapterData.add(Pair("Inward"          ,R.drawable.icon_quick_info_items))
                    quickResponseAdapterData.add(Pair("Outward"         ,R.drawable.icon_quick_info_customers))
                    quickResponseAdapterData.add(Pair("Total Users"     ,R.drawable.icon_quick_info_totalusers))
                    quickResponseAdapterData.add(Pair("Warehouse"       ,R.drawable.icon_quick_info_deliveryboy))
                    quickResponseAdapterData.add(Pair("Workshop"        ,R.drawable.icon_quick_info_deliveryboy))

                    println(quickResponseAdapterData)
                    println(quickInfoDataCount)
                    //quickInfoAdapter?.updateList(quickResponseAdapterData,quickInfoDataCount)

                    initQuickInfoMenu(quickResponseAdapterData,quickInfoDataCount)
/*                    response.QuickInfoDetails?.TotalWarehouse
                    response.QuickInfoDetails?.ActiveUsers
                    response.QuickInfoDetails?.InActiveUsers
                    response.QuickInfoDetails?.TotalBarcodePrint
                    response.QuickInfoDetails?.TotalFactory
                    response.QuickInfoDetails?.TotalInWard
                    response.QuickInfoDetails?.TotalOutWard
                    response.QuickInfoDetails?.TotalProduction*/

                }
                else{
                    DialogHelper.Alert_Selection(requireContext(),response.ErrorMessage.toString(),resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
                }

            }

        },
            {
                DialogHelper.Alert_Selection(requireContext(),it.toString(),resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }
        )
    }
    //endregion API SERVICE

}