@file:Suppress("DEPRECATION")

package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentRfidReaderBinding
import com.tupleinfotech.productbarcodescanner.model.SaveRFIDBarcodeEntry
import com.tupleinfotech.productbarcodescanner.model.TagScan
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.adapter.ScanListAdapterRv
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userId
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import com.ubx.usdk.rfid.aidl.IRfidCallback
import com.ubx.usdk.util.QueryMode
import com.ubx.usdk.util.SoundTool
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@SuppressLint("NotifyDataSetChanged","HandlerLeak","SetTextI18n")
@AndroidEntryPoint
class RfidReaderFragment : Fragment() {

    private var _binding: FragmentRfidReaderBinding? = null
    private val binding get() = _binding!!
    private var callback: ScanCallback? = null
    private var scanListAdapterRv: ScanListAdapterRv = ScanListAdapterRv()
    private var tagTotal = 0
    private val MSG_UPDATE_UI = 0
    private var time = 0L
    private var mapData: HashMap<String, TagScan> = hashMapOf()
    private var data: ArrayList<TagScan> = arrayListOf()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var prefs                      : SharedPreferences
    private var designNameList       : ArrayList<WorkshopListResponse.List> = arrayListOf()
    var readerType: Int = 0
    private var isRFIDFinder: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isRFIDFinder = it.getBoolean("isRFIDFinder")
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentRfidReaderBinding.inflate(LayoutInflater.from(context))
        val view = binding.root
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)
        if (isRFIDFinder) {
/*            binding.inputDesignName.visibility = View.GONE
            binding.inputLayoutRfid.visibility = View.VISIBLE*/
            binding.inputDesignName.visibility = View.VISIBLE
            binding.inputLayoutRfid.visibility = View.GONE

            binding.inputDesignName.hint = "Select RFID"
            initSelectRFID()

        }
        else {
/*            binding.inputDesignName.visibility = View.VISIBLE
            binding.inputLayoutRfid.visibility = View.GONE*/
            binding.inputDesignName.visibility = View.VISIBLE
            binding.inputLayoutRfid.visibility = View.GONE
            binding.inputDesignName.hint = "Design Name"
            getDesignNameList()

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scanListRv.setLayoutManager(LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false))
        binding.scanListRv.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))

        scanListAdapterRv = ScanListAdapterRv()
        binding.scanListRv.setAdapter(scanListAdapterRv)
        scanListAdapterRv.onItemClick = {
            val args = Bundle()
            args.putString("barcode",it.tid)
            args.putBoolean("isEditable",false)
            findNavController().navigate(R.id.BarcodeProductDetailsFragment,args)
        }

        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)

        initStartScanButton()
        initCheckBox()
        initClearButton()
        onBackPressed()

    }

    private fun initSelectRFID(){

        val RFIDList : ArrayList<String> = arrayListOf()
        RFIDList.add("E280119120008C1E4AEF0325")
        RFIDList.add("E280119120006997A8130342")
        RFIDList.add("E28011912000919E4AF90325")
        RFIDList.add("E2801191200096DE4AFB0325")
        RFIDList.add("E280119120007827A81B0342")

        binding.inputDesignName.setOnClickListener {
            sharedViewModel.showComponentsListingDialog(requireContext(),RFIDList,false){
                binding.inputDesignName.text = it.toString()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setCallback()
        }
    }

    private fun handlerUpdateUI() {
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI, 1000)
    }

    private fun handlerStopUI() {
        mHandler.removeCallbacksAndMessages(null)
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_UPDATE_UI -> {
                    scanListAdapterRv.notifyDataSetChanged()
                    handlerUpdateUI()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            if (mActivity.mRfidManager != null) {
//                Log.v(TagScanFragment.TAG, "--- getFirmwareVersion()   ----")
                mActivity.RFID_INIT_STATUS = true
                val firmware = mActivity.mRfidManager!!.firmwareVersion
                binding.textFirmware.text = getString(R.string.firmware) + firmware
            } else {
//                Log.v(TagScanFragment.TAG,"onStart()  --- getFirmwareVersion()   ----  mActivity.mRfidManager == null")
            }
        }, 5000)


    }

    override fun onResume() {
        super.onResume()
        mActivity.RFID_INIT_STATUS = true
        mActivity.initRfid()
    }
    override fun onDestroy() {
        super.onDestroy()

        SoundTool.getInstance(requireActivity()).release()
        mActivity.RFID_INIT_STATUS = false
        if (mActivity.mRfidManager != null) {
            mActivity.mRfidManager!!.disConnect()
            mActivity.mRfidManager!!.release()

//            Log.d(MainActivity.TAG, "onDestroyView: rfid close")
            //            System.exit(0);
        }
    }

    override fun onPause() {
        super.onPause()

        SoundTool.getInstance(requireActivity()).release()
        mActivity.RFID_INIT_STATUS = false
        if (mActivity.mRfidManager != null) {
            mActivity.mRfidManager!!.disConnect()
            mActivity.mRfidManager!!.release()

//            Log.d(MainActivity.TAG, "onDestroyView: rfid close")
            //            System.exit(0);
        }
    }

    private fun onBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,onBackPressedCallback)

    }

    private fun initCheckBox(){
        binding.checkBox.setOnCheckedChangeListener { _, b ->
            if (mActivity.mRfidManager != null) {
                if (b) {
                    mActivity.mRfidManager!!.queryMode = QueryMode.EPC_TID
                } else {
                    mActivity.mRfidManager!!.queryMode = QueryMode.EPC
                }
            }
        }
    }

    private fun notiyDatas(s2: String, TID: String, strRSSI: String) {
        val mapContainStr = if (!TextUtils.isEmpty(TID)) TID else s2
        SoundTool.getInstance(requireActivity()).playBeep(1)
        requireActivity().runOnUiThread {
            if (mapData.containsKey(mapContainStr)) {
                val tagScan = mapData[mapContainStr]!!
                tagScan.count++
                tagScan.tid     = TID
                tagScan.rssi    = strRSSI
                tagScan.epc     = s2
            } else {
                val tagScan = TagScan(strRSSI, s2, TID, 1)
                mapData[mapContainStr] = tagScan
            }

            // Update the RecyclerView adapter with the new data
            data.clear()
            data.addAll(mapData.values)
            scanListAdapterRv.updateList(data)
            binding.scanCountText.text = mapData.size.toString()
        }
    }

    private fun initClearButton(){
        binding.scanClearBtn.setOnClickListener {
            if (binding.scanClearBtn.getText() == getString(R.string.btn_stop_Inventory)) {
                binding.scanStartBtn.text = getString(R.string.btInventory)
                binding.scanClearBtn.text = getString(R.string.btn_stop_Inventory)
                setScanStatus(false)
            }
            else if (binding.scanClearBtn.getText() == getString(R.string.btn_clear_Inventory)){
                if (isRFIDFinder){
                    binding.scanStartBtn.text = getString(R.string.btInventory)
                    binding.scanClearBtn.text = getString(R.string.btn_clear_Inventory)
                }
                else{
                    binding.scanStartBtn.text = getString(R.string.btInventory)
                    binding.scanClearBtn.text = getString(R.string.btn_stop_Inventory)
                }
                initClearData()
            }
            initClearData()
        }
    }

    private fun initStartScanButton(){

        binding.scanStartBtn.setOnClickListener {
            if (mActivity.RFID_INIT_STATUS) {

                if (isRFIDFinder){
                    if (binding.scanStartBtn.getText() == getString(R.string.btInventory)) {
                        setCallback()
                        binding.scanStartBtn.text = getString(R.string.btn_stop_Inventory)
                        setScanStatus(true)
                    } else {
                        binding.scanStartBtn.text = getString(R.string.btInventory)
                        setScanStatus(false)
                    }
                }
                else{

                    if (binding.scanStartBtn.getText() == getString(R.string.btInventory)) {
                        setCallback()
                        binding.scanStartBtn.text = getString(R.string.btn_save_Inventory)
                        binding.scanClearBtn.text = getString(R.string.btn_stop_Inventory)
                        setScanStatus(true)
                    }
                    else if (binding.scanStartBtn.getText() == getString(R.string.btn_save_Inventory)){

                        if (binding.inputDesignName.text.toString().trim().isEmpty()){
                            DialogHelper.Alert_Selection(requireContext(),"Please Select Design Name!!","OK","")
                        }
                        else{
                            var barcodePrintList : Map<String,String> = mapOf()
                            val tagScanData : ArrayList<TagScan> = arrayListOf()
                            val barcodePrintArray = arrayListOf(barcodePrintList)

                            tagScanData.addAll(mapData.values)
                            tagScanData.forEach {
                                barcodePrintList = mapOf(
                                    "Barcode"           to it.tid.toString(),
                                    "BarCodePrefix"     to "RFID",
                                    "DesignName"        to binding.inputDesignName.text.toString().trim(),
                                    "TotalBarcodePrint" to "1",
                                    "UserId"            to prefs.userId.toString(),
                                    "IsUsed"            to "0"
                                )
                                barcodePrintArray.add(barcodePrintList)
                                barcodePrintArray.remove(emptyMap())
                            }

                            val map = mapOf<String, Any>(
                                "BarcodePrintList"    to barcodePrintArray
                            )

                            saveBarcodeEntry(map)
                            setCallback()
                            binding.scanStartBtn.text = getString(R.string.btInventory)
                            binding.scanClearBtn.text = getString(R.string.btn_clear_Inventory)
                            setScanStatus(false)
                        }

                    }
                    else {
                        binding.scanStartBtn.text = getString(R.string.btInventory)
                        setScanStatus(false)
                    }

                }
            }
            else {
//                Log.d(TagScanFragment.TAG,"scanStartBtn  RFID ")
                Toast.makeText(requireContext(), "RFID Not initialized", Toast.LENGTH_SHORT).show()
            }
        }

        /*        binding.scanStartBtn.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        run {
                            if (mActivity.RFID_INIT_STATUS) {
                                if (binding.scanStartBtn.getText() == getString(R.string.btInventory)) {
                                    setCallback()
                                    binding.scanStartBtn.text = getString(R.string.btn_stop_Inventory)
                                    setScanStatus(true)
                                } else {
                                    binding.scanStartBtn.text = getString(R.string.btInventory)
                                    setScanStatus(false)
                                }
                            } else {
                                Log.d(TAG,"scanStartBtn  RFID ")
                                Toast.makeText(requireContext(), "RFID Not initialized", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })*/

    }

    private fun initClearData(){
        tagTotal = 0
        binding.scanCountText.text = "0"
        binding.scanTotalText.text = "0"
        mapData.clear()
        data.clear()
        scanListAdapterRv.updateList(data)
    }

    private fun initDesignNameDropDown(designNameList : ArrayList<WorkshopListResponse.List>){

        binding.inputDesignName.setOnClickListener {
            sharedViewModel.showDesignNameListingDialog(requireContext(),designNameList,false){
                binding.inputDesignName.text = it.DesignName.toString()
            }
        }
    }

    private fun setScanStatus(isScan: Boolean) {
        if (isScan) {
//            tagTotal = 0
//            if (mapData.isNotEmpty()) {
//                mapData.clear()
//            }
//            if (data.isNotEmpty()) {
//                data.clear()
//                scanListAdapterRv?.setData(data)
//                println("RFID data$data")
//            }
//            Log.v(TAG, "--- startInventory()   ----")
            handlerUpdateUI()
            mActivity.mRfidManager!!.startInventory(0.toByte())

        } else {
//            Log.v(TAG, "--- stopInventory()   ----")
            mActivity.mRfidManager!!.stopInventory()
            handlerStopUI()
        }
    }

    internal inner class ScanCallback : IRfidCallback {
        override fun onInventoryTag(EPC: String, TID: String, strRSSI: String) {

            if (isRFIDFinder){
                if (binding.inputDesignName.text.toString().trim().equals(TID,true)){
                    notiyDatas(EPC, TID, strRSSI)
                }
            }
            else {
                notiyDatas(EPC, TID, strRSSI)
            }
            Log.d("EPC", EPC)
        }

        override fun onInventoryTagEnd() {
            Log.d(TAG, "onInventoryTag()")
        }
    }

    fun setCallback() {
        if (mActivity.mRfidManager != null) {
            if (callback == null) {
                callback = ScanCallback()
            }
            mActivity.mRfidManager!!.registerCallback(callback)
        }
    }

    companion object {
        val TAG: String = "usdk-" + RfidReaderFragment::class.java.simpleName
        private var mActivity: MainActivity = MainActivity()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ScanFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(activity: MainActivity): RfidReaderFragment {
            mActivity = activity
            val fragment = RfidReaderFragment()
            return fragment
        }

        fun hexStringToBytes(hexString: String): ByteArray {
            var hexString = hexString
            hexString = hexString.lowercase(Locale.getDefault())
            val byteArray = ByteArray(hexString.length shr 1)
            var index = 0
            for (i in hexString.indices) {
                if (index > hexString.length - 1) {
                    return byteArray
                }
                val highDit = ((hexString[index].digitToIntOrNull(16) ?: (-1 and 0xFF))).toByte()
                val lowDit = ((hexString[index + 1].digitToIntOrNull(16) ?: (-1 and 0xFF))).toByte()
                byteArray[i] = (highDit.toInt() shl 4 or lowDit.toInt()).toByte()
                index += 2
            }
            return byteArray
        }
    }

    //http://150.129.105.34/api/v1/barcodeentry/savebarcodeentry
    private fun getDesignNameList(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

//        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_DESIGN_NAME_LIST
        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_DESIGN_NAME_LIST
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{},{ getWorkshopResponse ->
            println(getWorkshopResponse)
            val workshopListResponse: WorkshopListResponse? = AppHelper.convertJsonToModel(getWorkshopResponse)

            if (workshopListResponse != null) {
                println(workshopListResponse)
                designNameList = workshopListResponse.designMasterList
                initDesignNameDropDown(workshopListResponse.designMasterList)

            }
            else {

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }

    //http://150.129.105.34/api/v1/barcodeentry/savebarcodeentry
    private fun saveBarcodeEntry(requestMap : Map<String, Any>){

//        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_DESIGN_NAME_LIST
        val getWorkshopListUrl = prefs.host + "v1/barcodeentry/savebarcodeentry"
        sharedViewModel.api_service(requireContext(),getWorkshopListUrl,requestMap,{},{ entryResponse ->
            println(entryResponse)
            val saveRFIDBarcodeEntry: SaveRFIDBarcodeEntry? = AppHelper.convertJsonToModel(entryResponse)

            if (saveRFIDBarcodeEntry != null) {
                saveRFIDBarcodeEntry.let {
                    if (it.Status.equals("Success", true)){
                        DialogHelper.Alert_Selection(requireContext(),it.ErrorMessage.toString(),"OK","")
                    }
                    else{
                        DialogHelper.Alert_Selection(requireContext(),it.ErrorMessage.toString(),"OK","")
                    }
                }
            }
            else {

                Log.i("==>", "ERROR: Unable to parse JSON into model")
            }

        },
            {
                println(it)
            })

    }
}