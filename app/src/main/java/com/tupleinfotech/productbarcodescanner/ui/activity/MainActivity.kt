package com.tupleinfotech.productbarcodescanner.ui.activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.ActivityMainBinding
import com.tupleinfotech.productbarcodescanner.ui.fragment.RfidReaderFragment
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.clearValues
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.imageurl
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.ipAddress
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.password
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.port
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userfirstname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userfullname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userlastname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.username
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userprofileimage
import com.ubx.usdk.USDKManager
import com.ubx.usdk.rfid.RfidManager
import com.ubx.usdk.util.QueryMode
import com.ubx.usdk.util.SoundTool
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //region VARIABLES

    private var _binding                    : ActivityMainBinding?                          =       null
    private val binding                     get()                                           =       _binding!!
    private lateinit var navController      : NavController
    private lateinit var prefs             : SharedPreferences
    var RFID_INIT_STATUS: Boolean = false
    var mRfidManager: RfidManager? = null
    var readerType: Int = 0
    private var fragments: List<Fragment> = listOf()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        // Keep the splash screen visible for this Activity.
        splashScreen.setKeepOnScreenCondition { false }

        splashScreen.setOnExitAnimationListener { splashScreenProvider ->
            // Remove the splash screen after a delay
            Handler().postDelayed({
                splashScreenProvider.remove()
            }, 1000L)
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        prefs       = PreferenceHelper.customPreference(this, Constants.CUSTOM_PREF_NAME)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragments) as NavHostFragment
        navController = navHostFragment.navController // Initialize navController

        SoundTool.getInstance(this@MainActivity)
        fragments = listOf(
            RfidReaderFragment.newInstance(this@MainActivity),
//            TagManageFragment.newInstance(this@MainActivity),
//            SettingFragment.newInstance(this@MainActivity)
        )
        initRfid()

        init()
    }

    /*    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            if (event.keyCode == 523 && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                //TODO set scanning functionality on system key event
                println(event.keyCode)
                println(event.action)

                return true
            }
            else if (event.keyCode == 523 && event.action == KeyEvent.ACTION_UP && event.repeatCount == 0) {
                //TODO set scanning functionality on system key event
                println(event.keyCode)
                println(event.action)

                return true
            }
            return super.dispatchKeyEvent(event)
        }*/

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        initSideMenu()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS
    fun initRfid() {

        USDKManager.getInstance().init(this@MainActivity) { status ->
            if (status == USDKManager.STATUS.SUCCESS) {
                Log.d(TAG, "initRfid()  success.")
                mRfidManager = USDKManager.getInstance().rfidManager
                (fragments[0] as RfidReaderFragment).setCallback()
                mRfidManager?.setOutputPower(10.toByte())

                Log.d(TAG,"initRfid: getDeviceId() = " + mRfidManager?.deviceId)

                readerType = mRfidManager?.readerType!!

                runOnUiThread {
                    Toast.makeText(this@MainActivity,"module：$readerType", Toast.LENGTH_LONG).show()
                }
                mRfidManager!!.queryMode = QueryMode.EPC_TID

                Log.d(TAG,"initRfid: GetReaderType() = $readerType")
            } else {
                Log.d(TAG, "initRfid  fail.")
            }
        }
    }

    companion object {
        const val TAG: String = "usdk"
    }

    fun menuitemselection(navView: BottomNavigationView, id: Int, visibility: Boolean) {

        when (visibility) {
            true -> navView.visibility  = View.VISIBLE
            false -> navView.visibility = View.GONE
        }
        navView.menu.getItem(id).isChecked = true
    }

    fun setDrawerLockMode(lockMode: Int)                    =       findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(lockMode)

    //Custom Bottom Navigation Functionality

    fun initBottomNavigation(isVisible: Boolean, bottomMenuItemCount: Int, bottomNavigationItemName : ArrayList<String>, bottomNavSelectedImageItemList : ArrayList<Int>) {

        if (isVisible) binding.bottomNavigationView.visibility = View.VISIBLE else binding.bottomNavigationView.visibility = View.GONE

        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.menu.clear() // Clear existing menu items

        if (bottomMenuItemCount in 1..4) {
            for (i in 0 until bottomMenuItemCount) {
                binding.bottomNavigationView.menu.add(Menu.NONE, i, Menu.NONE, bottomNavigationItemName[i]).setIcon(ResourcesCompat.getDrawable(this.resources, bottomNavSelectedImageItemList[i], this.theme))
            }
        }
        else {
            Log.e("initBottomNavigation", "Cannot add more than 4 items to BottomNavigationView")
            return
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                0 -> {
                    navController.navigate(R.id.quickInfoFragment)
                    item.setIcon(ResourcesCompat.getDrawable(this.resources,bottomNavSelectedImageItemList[item.itemId],this.theme))
                }
                1 -> {
                    navController.navigate(R.id.productManufactureFragment)
                    item.setIcon(ResourcesCompat.getDrawable(this.resources,bottomNavSelectedImageItemList[item.itemId],this.theme))
                }
                2 -> {
                    navController.navigate(R.id.warehouseEntryFragment)
                    item.setIcon(ResourcesCompat.getDrawable(this.resources,bottomNavSelectedImageItemList[item.itemId],this.theme))
                }
                3 -> {
                    navController.navigate(R.id.profileFragment)
                    item.setIcon(ResourcesCompat.getDrawable(this.resources,bottomNavSelectedImageItemList[item.itemId],this.theme))
                }
                else -> {
                }
            }
            true
        }
    }

    fun initActionbar(appbarTitle : String, leftButton : Int = R.drawable.icon_side_menu, rightButton : Int = 0, leftButtonClick : () -> Unit = {},  rightButtonClick : () -> Unit = {}, isVisible : Boolean = true){

        if (isVisible) binding.customActionBar.root.visibility = View.VISIBLE else binding.customActionBar.root.visibility = View.GONE

        binding.customActionBar.setOurText.text = appbarTitle

        binding.customActionBar.arrowBnt.setImageResource(leftButton)

        if (rightButton == 0){
            binding.customActionBar.notificationBtn.visibility = View.INVISIBLE
        }
        else{
            binding.customActionBar.notificationBtn.visibility = View.VISIBLE
            binding.customActionBar.notificationBtn.setImageResource(rightButton)
        }

        binding.customActionBar.arrowBnt.setOnClickListener {
            leftButtonClick()
        }

        binding.customActionBar.notificationBtn.setOnClickListener {
            rightButtonClick()
        }

    }

    private fun initSideMenu(){
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.side_menu_home                         ->      {
                    navController.navigate(R.id.quickInfoFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_menu_product_manufacture          ->      {
                    navController.navigate(R.id.productManufactureFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_menu_warehouse_entry              ->      {
                    navController.navigate(R.id.warehouseEntryFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_product_details                   ->      {
                    navController.navigate(R.id.BarcodeProductDetailsFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_rfid_reader                   ->      {
                    val args = Bundle()
                    args.putBoolean("isRFIDFinder",false)
                    navController.navigate(R.id.rfidReaderFragment,args)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_rfid_finder                   ->      {
                    val args = Bundle()
                    args.putBoolean("isRFIDFinder",true)
                    navController.navigate(R.id.rfidReaderFragment,args)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
/*                R.id.side_rfid_reader                   ->      {
                    val intent = Intent(this@MainActivity, RFIDScannerActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }*/
                R.id.side_production_report                 ->      {
                    navController.navigate(R.id.productionReportFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_menu_logout                       ->      {
                    DialogHelper.Alert_Selection(
                        context                     =           this@MainActivity,
                        message                     =           AlertMsgs.SignOut,
                        positiveButtonTitle         =           "YES",
                        negativeButtonTitle         =           "NO",
                        onPositiveButtonClick       =           {
                            val userId      = prefs.username
                            val password    = prefs.password
                            val baseUrl     = Constants.BASE_URL
                            val ipAddress   = prefs.ipAddress
                            val ipPort      = prefs.port
                            val ipHost      = prefs.host
                            val imageUrl    = prefs.imageurl

                            prefs.clearValues
                            prefs.username      = userId
                            prefs.password      = password
                            Constants.BASE_URL  = baseUrl
                            prefs.ipAddress     = ipAddress
                            prefs.port          = ipPort
                            prefs.host          = ipHost
                            prefs.imageurl      = imageUrl
                            navController.navigate(R.id.loginFragment)
                            binding.drawerLayout.closeDrawers()
                            binding.bottomNavigationView.menu.clear()
                            binding.bottomNavigationView.visibility = GONE
                            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        },
                        onNegativeButtonClick       = {},
                        showNegativeButton          = true,
                        onDismiss                   = {}
                    )
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Add more cases for other menu items
                else                                        ->      false
            }
        }

    }

    fun sideMenuSetHeader(){
        val headerView          : View          = binding.navView.getHeaderView(0)
        val sideMenuFirstName   : TextView  = headerView.findViewById(R.id.side_menu_header_firstName_txt)
        val sideMenuLastName    : TextView  = headerView.findViewById(R.id.side_menu_header_lastName_txt)
        val sideMenuImageText   : TextView  = headerView.findViewById(R.id.side_menu_header_image_text)
        val sideMenuImageView   : ImageView = headerView.findViewById(R.id.side_menu_header_image)

        sideMenuFirstName.text  = prefs.userfirstname
        sideMenuLastName.text   = prefs.userlastname

        if (prefs.userfullname.toString()[0].uppercaseChar().toString().trim().isNotEmpty() && prefs.userfullname.toString()[0].uppercaseChar().toString().trim().isNotBlank()){
            sideMenuImageText.text = prefs.userfullname.toString()[0].uppercaseChar().toString().trim()
        }
        if (prefs.userfirstname.toString()[0].uppercaseChar().toString().trim().isEmpty() && prefs.userfirstname.toString()[0].uppercaseChar().toString().trim().isBlank()){
            sideMenuImageText.text = prefs.userlastname.toString()[0].uppercaseChar().toString().trim()
        }else if (prefs.userlastname.toString()[0].uppercaseChar().toString().trim().isEmpty() && prefs.userlastname.toString()[0].uppercaseChar().toString().trim().isBlank()){
            sideMenuImageText.text = prefs.userfirstname.toString()[0].uppercaseChar().toString().trim()
        } else if (prefs.userfirstname.toString()[0].uppercaseChar().toString().trim().isNotEmpty() && prefs.userfirstname.toString()[0].uppercaseChar().toString().trim().isNotBlank() && prefs.userlastname.toString()[0].uppercaseChar().toString().trim().isNotEmpty() && prefs.userlastname.toString()[0].uppercaseChar().toString().trim().isNotBlank()) {
            sideMenuImageText.text = prefs.userfirstname.toString()[0].uppercaseChar().toString().trim() + prefs.userlastname.toString()[0].uppercaseChar().toString().trim()
        }

        Glide.with(sideMenuImageView)
            .load("http://"+prefs.ipAddress+prefs.userprofileimage)
            .fitCenter()
            .listener(object : RequestListener<Drawable?> {

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    sideMenuImageText.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    sideMenuImageText.visibility = View.VISIBLE
                    return false
                }
            })
            .into(sideMenuImageView)
    }

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE
    //endregion API SERVICE
}