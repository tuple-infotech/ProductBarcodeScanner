package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmsc.postab.ui.dialogfragment.addhost.AddHostDialog
import com.jmsc.postab.ui.dialogfragment.addhost.AddHostViewModel
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentLoginBinding
import com.tupleinfotech.productbarcodescanner.model.LoginResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.convertJsonToModel
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host_id
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.imageurl
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.ipAddress
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.password
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.port
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userId
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.useraddress
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userdob
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.useremail
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userfullname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.usergender
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.username
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userphone
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userprofileimage
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import com.tupleinfotech.treeview.TreeNode
import com.tupleinfotech.treeview.TreeViewAdapter
import com.tupleinfotech.treeview.TreeViewHolder
import com.tupleinfotech.treeview.TreeViewHolderFactory
import com.tupleinfotech.treeview.file.FileViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    //region VARIABLES
    private var _binding                                    : FragmentLoginBinding?           =  null
    private val binding                                     get()                                   =  _binding!!
    lateinit var prefs : SharedPreferences
    private val sharedViewModel             : SharedViewModel by viewModels()
    private val viewModelDB : AddHostViewModel by viewModels()
    private lateinit var treeViewAdapter    : TreeViewAdapter

    var defaultmenuid : String = ""
    var menuid: String = ""
    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("HardwareIds")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        lifecycleScope.launch {
            val selectedHost = viewModelDB.selectHost(prefs.host_id)
            if(selectedHost!=null){
                binding.etBoxUsername.setText(selectedHost.username)
                binding.etBoxPassword.setText(selectedHost.password)

                Constants.BASE_URL= "http://"+selectedHost.host_ip+":"+selectedHost.host_port+"/api/"
            }
        }
        sharedViewModel.initActionbarWithoutSideMenu(requireActivity() as MainActivity)
        initAddHost()
        onBackPressed()
        btn_login()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun btn_login(){
        binding.btnLogin.setOnClickListener {
            validateLogin()
        }

        binding.etBoxPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Call onDone result here
                validateLogin()
            }
            false
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun initAddHost(){

        binding.addHostTv.setOnClickListener {
            val fragment = AddHostDialog()

            fragment.show(parentFragmentManager,"dialog")
            fragment.onSelect = {
                    host ->

                if (host.host_port.isEmpty()){
                    Constants.BASE_URL= "http://"+host.host_ip+"/api/"
                }else{
                    Constants.BASE_URL= "http://"+host.host_ip+":"+host.host_port+"/api/"
                }
//                Constants.BASE_URL= "http://"+host.host_ip+":"+host.host_port+"/api/"

                prefs.ipAddress = host.host_ip
                prefs.port = host.host_port

                prefs.host = Constants.BASE_URL
                prefs.imageurl = "http://"+prefs.ipAddress

                Log.i("==>1",""+Constants.BASE_URL)
                Log.i("==>1",""+prefs.all)

                fragment.dismiss()
            }
        }

    }

    private fun validateLogin(){
        val userId      = binding.etBoxUsername.text.toString()
        val password    = binding.etBoxPassword.text.toString()

        if(Constants.BASE_URL.isEmpty()){
            DialogHelper.Alert_Selection(requireContext(),"Select Or Add New Host !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            return
        }

        if(Constants.BASE_URL.equals("http://1.1.1.1:1/api/",true)){
            DialogHelper.Alert_Selection(requireContext(),"Select Or Add New Host !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            return
        }

        if(userId.isEmpty()){
            DialogHelper.Alert_Selection(requireContext(),"Enter User Name !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            binding.etBoxUsername.requestFocus()
            return
        }

        if(password.isEmpty()){
            DialogHelper.Alert_Selection(requireContext(),"Enter Password !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            binding.etBoxPassword.requestFocus()
            return
        }
        service_api_login(userId,password)

    }

    private fun initBottomNavigation(){
        val bottomNavigationUnselectedItemImageList     : ArrayList<Int>                        = arrayListOf()
        val bottomNavigationItemName                    : ArrayList<String>                     = arrayListOf()

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

    fun setSideMenuItems(){

        val recyclerView = (requireActivity() as MainActivity).findViewById<RecyclerView>(R.id.menu_rv)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false

        val factory = object : TreeViewHolderFactory {
            override fun getTreeViewHolder(view: View, layout: Int): TreeViewHolder {
                return FileViewHolder(view)
            }
        }

        treeViewAdapter = TreeViewAdapter(factory)
        val click = object : TreeViewAdapter.OnTreeNodeClickListener{
            override fun onTreeNodeClick(treeNode: TreeNode, view: View) {
                val menu = treeNode.value as LoginResponse.Menu
                //TODO: Set Navigation Click on the basis of Menu Id
                menuid = menu.MenuId ?:"0"
                println(menuid)

            }
        }

        treeViewAdapter.setTreeNodeClickListener(click)
        recyclerView.adapter = treeViewAdapter

    }

    fun initJsonSerMenuTree(MenuRights : List<LoginResponse.Menu>) {
//        val jsonFileString = getJsonDataFromAsset(this, "accessrights.json")
//        if (jsonFileString != null) {
//            Log.i("data", jsonFileString)
//        }
//
//        val gson = Gson()
//        val response = gson.fromJson(jsonFileString, MenuRights::class.java)
//
        val fileRoots: MutableList<TreeNode> = ArrayList()

        MenuRights.forEach { menu ->
            val imageUrl = "http://"+prefs.ipAddress+menu.MenuIcon.toString()
            val newNode = TreeNode(menu.MenuName.toString(), menu, com.tupleinfotech.treeview.R.layout.list_item_file,imageUrl)

            // If this category is default, set menuid accordingly
            if (menu.MenuId == defaultmenuid) {
                menuid = menu.MenuId ?: "0"
            }

            findSubCategory(menu.SubMenus, newNode)
            fileRoots.add(newNode)
        }

        treeViewAdapter.updateTreeNodes(fileRoots)
//        treeViewAdapter.expandNodesAtLevel(0)
    }

    private fun findSubCategory(submenus: List<LoginResponse.Menu>, parent: TreeNode) {
        submenus.forEach { submenu ->
            val imageUrl = ""
            val newNode = TreeNode(submenu.MenuName.toString(), submenu, com.tupleinfotech.treeview.R.layout.list_item_file,imageUrl)
            findSubCategory(submenu.SubMenus, newNode)
            parent.addChild(newNode)
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

    //TODO: Device Token need to configure with Firebase and then need to implement here
    @SuppressLint("HardwareIds")
    private fun service_api_login(userId: String, password: String){
        val requestMap: Map<String,String> = mapOf<String, String>(
            "UserName" to userId,
            "Password" to password,
            "RequestSource" to "Android",
            "DeviceToken" to "TEST",
            "DeviceType" to "ANDROID",
            "DeviceOSVersion" to Build.VERSION.RELEASE,
            "DeviceModel" to Build.MODEL.toString(),
            "DeviceId" to Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID),
            "DeviceIpAddress" to AppHelper.getIPV4address(),
        )

        val loginUrl = prefs.host + UrlEndPoints.ACCOUNT_LOGIN
        sharedViewModel.api_service(requireContext(),loginUrl,requestMap,{ accountLoginResponse ->
            println(accountLoginResponse)

            val loginResponse: LoginResponse? = convertJsonToModel(accountLoginResponse)

            loginResponse?.let {response ->
                if (response.Status.equals("Success",true)){
                    lifecycleScope.launch {
                        val selectedHost = viewModelDB.selectHost(prefs.host_id)
                        if(selectedHost!=null){
                            selectedHost.username = userId
                            selectedHost.password = password
                            viewModelDB.updateHost(selectedHost)
                        }
                    }
                    prefs.username = userId
                    prefs.password = password
                    prefs.userId   = loginResponse.USER?.UserId.toString()

                    prefs.userfullname          = loginResponse.USER?.FirstName + loginResponse.USER?.MiddleName + loginResponse.USER?.LastName
                    prefs.useremail             = loginResponse.USER?.Email
                    prefs.userphone             = loginResponse.USER?.MobileNumber
                    prefs.userprofileimage      = loginResponse.USER?.ProfileImagePath
                    prefs.useraddress           = loginResponse.USER?.Address
                    prefs.userdob               = loginResponse.USER?.Dob
                    prefs.usergender            = loginResponse.USER?.Gender
                    prefs.userprofileimage      = loginResponse.USER?.ProfileImagePath + loginResponse.USER?.ProfileImage

                    loginResponse.USER?.menu?.forEach { item ->
                        println(item.MenuName)
                    }

                    loginResponse.USER?.menu?.let {

                        println(it)
                        setSideMenuItems()
                        initJsonSerMenuTree(it)
                    }

                    findNavController().navigate(R.id.quickInfoFragment)
                    initBottomNavigation()

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