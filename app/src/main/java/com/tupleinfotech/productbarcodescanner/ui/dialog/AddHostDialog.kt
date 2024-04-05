package com.jmsc.postab.ui.dialogfragment.addhost

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmsc.postab.db.AddHost
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.AddHostDialogBinding
import com.tupleinfotech.productbarcodescanner.ui.adapter.AddHostAdepter
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.AddHostViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host_id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddHostDialog : DialogFragment() {

    private var _binding: AddHostDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var addHostAdepter: AddHostAdepter
    private lateinit var hostList: MutableList<AddHost>

    private val viewModel : AddHostViewModel by viewModels()

    private lateinit var selectedHost: AddHost

    var onSelect: ((AddHost) -> Unit)? = null

    lateinit var prefs : SharedPreferences
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = AddHostDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        prefs = AppHelper.Companion.PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        isCancelable = false

        bindRecyclerViews()

        bindButtons()

        subscribeToObservables()

        return dialog
    }
    private fun bindRecyclerViews(){
        hostList = mutableListOf()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

        val recyclerViewScanItemList =binding.recyclerViewAdhost
        // pass it to rvLists layoutManager
        recyclerViewScanItemList.layoutManager = layoutManager

        // initialize the adapter,
        // and pass the required argument
        addHostAdepter = AddHostAdepter(hostList)

        addHostAdepter.onItemClick = { host ->
            selectedHost = host
            binding.hostName.setText(host.host_name)
            binding.hostIp.setText(host.host_ip)
            binding.hostPort.setText(host.host_port)
        }

        // attach adapter to the recycler view
        recyclerViewScanItemList.adapter = addHostAdepter

    }

    private fun bindButtons(){

        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnAdd.setOnClickListener {
            val hostName = binding.hostName.text.toString()
            val hostIP = binding.hostIp.text.toString()
            val hostPort = binding.hostPort.text.toString()

            if(validateFields(hostName,hostIP,hostPort)){
                lifecycleScope.launch(Dispatchers.IO){
                    if(isHostExists(hostName,hostIP,hostPort)){
                        withContext(Dispatchers.Main) {
                            DialogHelper.Alert_Selection(requireContext(),"Host Already exits.",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
                        }
                    }else{
                        viewModel.addHost(hostName,hostIP,hostPort)
                        withContext(Dispatchers.Main) {
                            clearFragment()
                        }
                    }
                }
            }


        }

        binding.btnModify.setOnClickListener {
            val hostName = binding.hostName.text.toString()
            val hostIP = binding.hostIp.text.toString()
            val hostPort = binding.hostPort.text.toString()

            if(validateFields(hostName,hostIP,hostPort)){
                selectedHost.host_name = hostName
                selectedHost.host_ip = hostIP
                selectedHost.host_port = hostPort
                viewModel.updateHost(selectedHost)
                clearFragment()
            }
        }

        binding.btnDelete.setOnClickListener {
            val hostName = binding.hostName.text.toString()
            val hostIP = binding.hostIp.text.toString()
            val hostPort = binding.hostPort.text.toString()

            if(validateFields(hostName,hostIP,hostPort)){
                selectedHost.host_name = hostName
                selectedHost.host_ip = hostIP
                selectedHost.host_port = hostPort
                viewModel.deleteHost(selectedHost)
                clearFragment()
            }


        }

        binding.btnSelect.setOnClickListener {
            if(this::selectedHost.isInitialized){
                prefs.host_id = selectedHost.id
                onSelect?.invoke(selectedHost)
            }else{
                DialogHelper.Alert_Selection(requireContext(),"Please Select Host First !",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            }

        }
    }
    private fun subscribeToObservables(){
        lifecycleScope.launch {

            viewModel.getHosts().collectLatest {

                hostList.clear()
                hostList.addAll(it.toMutableList())

                addHostAdepter.notifyDataSetChanged()
            }
        }

    }

    private suspend fun isHostExists(host_name: String, host_ip: String, host_port: String): Boolean{
           return viewModel.checkHostExit(host_name,host_ip,host_port)
    }

    private fun validateFields(hostName: String, hostIP: String, hostPort: String): Boolean{
        if(hostName.isEmpty()){
            DialogHelper.Alert_Selection(requireContext(),"Enter Host Name !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            binding.hostName.requestFocus()
            return false
        }
        if(hostIP.isEmpty()){
            DialogHelper.Alert_Selection(requireContext(),"Enter Ip Address !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            binding.hostIp.requestFocus()
            return false
        }
        if(!AppHelper.isValidIPAddress(hostIP)){
            DialogHelper.Alert_Selection(requireContext(),"Enter Valid Ip Address !!",resources.getString(R.string.singlebtntext),"", showNegativeButton = false,)
            binding.hostIp.requestFocus()
            return false
        }
//        if(hostPort.isEmpty()){
//            DialogHelper.showErrorDialog(requireContext(),"Enter Port !!")
//            binding.hostPort.requestFocus()
//            return false
//        }

        return true
    }


    private fun clearFragment(){
        binding.hostName.setText("")
        binding.hostIp.setText("")
        binding.hostPort.setText("")
        binding.hostName.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.50).toInt()
        //val height = (resources.displayMetrics.heightPixels * 0.50).toInt()
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //THIS WILL MAKE WIDTH 90% OF SCREEN
        //HEIGHT WILL BE WRAP_CONTENT
       // dialog!!.window!!.setLayout(width, height)
    }

}
