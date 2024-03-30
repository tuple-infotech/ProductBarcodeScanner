package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jmsc.postab.ui.dialogfragment.addhost.AddHostDialog
import com.tupleinfotech.productbarcodescanner.databinding.FragmentLoginBinding
import com.tupleinfotech.productbarcodescanner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    //region VARIABLES
    private var _binding                                    : FragmentLoginBinding?           =  null
    private val binding                                     get()                                   =  _binding!!

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){
        initAddHost()
        onBackPressed()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun initAddHost(){

        binding.addHostTv.setOnClickListener {
            val fragment = AddHostDialog()

            fragment.show(parentFragmentManager,"dialog")
            fragment.onSelect = {
                    host ->
                Constants.BASE_URL= "http://"+host.host_ip+":"+host.host_port+"/api/"

//                prefs.ipAddress = host.host_ip
//                prefs.port = host.host_port
//
//                prefs.host = Constants.BASE_URL

//                Log.i("==>1",""+prefs.host)

                /*  val preferences = requireActivity()
                      .getSharedPreferences(Constants.CUSTOM_PREF_NAME, 0)
                  val editor: SharedPreferences.Editor = preferences.edit()
                  editor.putString("SELECTED_HOST", prefs.selectedHost)
                  editor.apply()
  */
                fragment.dismiss()
                /*Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }, 1000) //millis*/

                //(requireActivity() as MainActivity).changeFragment(LoginFragment())


            }
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
    //endregion API SERVICE

}