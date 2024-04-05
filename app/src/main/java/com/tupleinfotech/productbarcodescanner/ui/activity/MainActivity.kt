package com.tupleinfotech.productbarcodescanner.ui.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //region VARIABLES

    private var _binding                    : ActivityMainBinding?                          =       null
    private val binding                     get()                                           =       _binding!!
    private lateinit var navController      : NavController

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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragments) as NavHostFragment
        navController = navHostFragment.navController // Initialize navController

    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

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
            binding.customActionBar.notificationBtn.visibility = View.GONE
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

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE
    //endregion API SERVICE
}