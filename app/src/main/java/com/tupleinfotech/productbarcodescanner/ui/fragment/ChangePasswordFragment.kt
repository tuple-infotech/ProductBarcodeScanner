package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tupleinfotech.productbarcodescanner.databinding.FragmentChangePasswordBinding
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.conPassMissing
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.oldPassMissing
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.oldPassNotMatch
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passMissing
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passNotMatch
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passnotSimlar
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passwordcontaindigit
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passwordcontainletter
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passwordcontainlowercase
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passwordcontainspecialcharacter
import com.tupleinfotech.productbarcodescanner.util.AlertMsgs.passwordcontainuppercase
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.isvalidpassword
import com.tupleinfotech.productbarcodescanner.util.AppHelper.Companion.validcoPassword
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    //region VARIABLES

    private var _binding                    : FragmentChangePasswordBinding?                        =  null
    private val binding                     get()                                                   =  _binding!!
    private var isfromProfile               : Boolean?                                              = false
    private val sharedViewModel             : SharedViewModel by viewModels()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isfromProfile = it.getBoolean("isFromProfile")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)
        screenWiseFunctionality()
        onBackPressed()
//        initBack()
    }

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    //Change Password Page field empty validations and service function

    private fun changePasswordButtonClick() {
        binding.btnChangePass.setOnClickListener {
            if (isfromProfile == true) {
                if (binding.etBoxOldPass.text.toString().trim().isEmpty()){
                    binding.layOldPass.helperText = oldPassMissing
                }
                else if (binding.etBoxOldPass.text.toString().trim() != "Abc@123"){
                    binding.layOldPass.helperText = oldPassNotMatch
                }
                else if (allfieldRequired()){
                    println("Hello")
                    //TODO: Password Change Service
                }
            }
            else {
                if (allfieldRequired()) {
                    println("Hello")
                    //TODO: Password Change Service
                }
            }
        }
    }

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    //Screen wise display data and functionality

    private fun screenWiseFunctionality(){
        //TODO: Set Password length dynamically

        val maxLength                           = 30 // Replace with your desired maximum length
        val filterArray                         = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        binding.etBoxOldPass.filters            = filterArray
        binding.etBoxPassword.filters           = filterArray
        binding.etBoxConfirmPassword.filters    = filterArray
        passwordFocusListener()
        confirmPasswordFocusListener()
        oldpasswordFocusListener()
        changePasswordButtonClick()

    }

    //Change Password Page field empty validations

    private fun allfieldRequired(): Boolean {
        //TODO: Set Old Password Dynamically

        val oldpasswordtext     = "Abc@123"
        val passwordText        = binding.etBoxPassword.text.toString().trim()
        val confirmpasswordText = binding.etBoxConfirmPassword.text.toString().trim()

        if (passwordText.isEmpty()){
            binding.layNewPass.helperText = passMissing
            return false
        }
        else{
            if (!passwordText.matches(".*[0-9].*".toRegex())) {
                binding.layNewPass.helperText = passwordcontaindigit
                return false
            }
            else if (!passwordText.matches(".*[a-z].*".toRegex())) {
                binding.layNewPass.helperText = passwordcontainlowercase
                return false
            }
            else if (!passwordText.matches(".*[A-Z].*".toRegex())) {
                binding.layNewPass.helperText = passwordcontainuppercase
                return false
            }
            else if (!passwordText.matches(".*[a-zA-Z].*".toRegex())) {
                binding.layNewPass.helperText =  passwordcontainletter
                return false
            }
            else if(!passwordText.matches(".*[!@#$%^&*+=/?].*".toRegex())){
                binding.layNewPass.helperText = passwordcontainspecialcharacter
                return false
            }
            else if (!passwordText.matches(".{4,30}".toRegex())) {
                binding.layNewPass.helperText = "Password length should be in between 4 to 30 characters"
                return false
            }
            else if (passwordText == oldpasswordtext) {
                binding.layNewPass.helperText = passnotSimlar
                return false
            }
        }
        if (confirmpasswordText.isEmpty()){
            binding.layConfirmPass.helperText = conPassMissing
            return false
        }
        if (!passwordText.matches(".{4,30}".toRegex())) {
            binding.layNewPass.helperText = "Password length should be in between 4 to 30 characters"
            return false
        }
        if (passwordText != confirmpasswordText) {
            binding.layConfirmPass.helperText = passNotMatch
            return false
        }
        return true
    }

    //ENTER NEW PASSWORD field validations

    private fun passwordFocusListener() {

        //TODO: Set Old Password Dynamically

        binding.etBoxPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.layNewPass.helperText = isvalidpassword(binding.etBoxPassword.text.toString().trim(),"Abc@123")
            }else{
                binding.layNewPass.helperText = ""
            }
        }
    }

    //ENTER CONFIRM PASSWORD field validations

    private fun confirmPasswordFocusListener() {
        binding.etBoxConfirmPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.layConfirmPass.helperText = validcoPassword(binding.etBoxPassword.text.toString().trim(),binding.etBoxConfirmPassword.text.toString().trim())
            }else{
                binding.layConfirmPass.helperText = ""
            }
        }
    }

    //ENTER OLD PASSWORD field validations

    private fun oldpasswordFocusListener() {
        binding.etBoxOldPass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.layOldPass.helperText = oldvalidPassword()
            }else{
                binding.layOldPass.helperText = ""
            }
        }
    }

    //TODO: Set Old Password Dynamically

    private fun oldvalidPassword(): String? = if (binding.etBoxOldPass.text.toString().trim().isBlank()) oldPassMissing
                                                else if (binding.etBoxOldPass.text.toString().trim() != "Abc@123") oldPassNotMatch
                                                else null

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS

    private fun onBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isfromProfile == true) {
                    findNavController().popBackStack()
                }else {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,onBackPressedCallback)

    }

//    private fun initBack(){
//        binding.arrowBnt.setOnClickListener{
//            println("BUTTON CLICK")
//            if (isfromProfile == true) {
//                findNavController().popBackStack()
//            }else {
//                findNavController().popBackStack()
//                findNavController().popBackStack()
//            }
//        }
//    }

    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    //endregion API SERVICE

}