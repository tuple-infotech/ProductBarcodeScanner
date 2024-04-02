package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProductManufactureBinding
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper

class ProductManufactureFragment : Fragment() {

    //region VARIABLES

    private var _binding                                    : FragmentProductManufactureBinding?             =  null
    private val binding                                     get()                                   =  _binding!!
    private lateinit var prefs                              : SharedPreferences

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentProductManufactureBinding.inflate(inflater, container, false)
        val view = binding.root
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        return view
    }
    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS
    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE
    //endregion API SERVICE


}