package com.tupleinfotech.productbarcodescanner.ui.activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.ActivityRfidscannerBinding
import com.tupleinfotech.productbarcodescanner.model.SaveRFIDBarcodeEntry
import com.tupleinfotech.productbarcodescanner.model.TagScan
import com.tupleinfotech.productbarcodescanner.model.WorkshopListResponse
import com.tupleinfotech.productbarcodescanner.ui.adapter.ScanListAdapterRv
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import com.tupleinfotech.productbarcodescanner.util.AppHelper
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.DialogHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userId
import com.tupleinfotech.productbarcodescanner.util.UrlEndPoints
import com.ubx.usdk.USDKManager
import com.ubx.usdk.rfid.RfidManager
import com.ubx.usdk.rfid.aidl.IRfidCallback
import com.ubx.usdk.util.QueryMode
import com.ubx.usdk.util.SoundTool
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n","HandlerLeak","NotifyDataSetChanged")
@AndroidEntryPoint
class RFIDScannerActivity : AppCompatActivity() {

    //region VARIABLES

    private var _binding: ActivityRfidscannerBinding? = null
    private val binding get() = _binding!!
    var readerType: Int = 0
    var RFID_INIT_STATUS: Boolean = false
    var mRfidManager: RfidManager? = null
    private val sharedViewModel: SharedViewModel by viewModels()

    private var data: ArrayList<TagScan> = arrayListOf()
    private var mapData: HashMap<String, TagScan> = hashMapOf()
    private var callback: ScanCallback? = null
    private var scanListAdapterRv: ScanListAdapterRv = ScanListAdapterRv()
    private var tagTotal = 0
    private val MSG_UPDATE_UI = 0
    private var time = 0L
    val TAG: String = "usdk-"
    private lateinit var prefs                      : SharedPreferences
    private var designNameList       : ArrayList<WorkshopListResponse.List> = arrayListOf()

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRfidscannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs       = PreferenceHelper.customPreference(this@RFIDScannerActivity, Constants.CUSTOM_PREF_NAME)

        binding.scanListRv.setLayoutManager(LinearLayoutManager(this,RecyclerView.VERTICAL,false))
        binding.scanListRv.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        scanListAdapterRv = ScanListAdapterRv()
        binding.scanListRv.setAdapter(scanListAdapterRv)

        SoundTool.getInstance(this@RFIDScannerActivity)
        initRfid()
        initStartScanButton()
        initCheckBox()
        initClearButton()
        initActionbar()
        getDesignNameList()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        if (visible) {
            setCallback()
        }
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            if (mRfidManager != null) {
//                Log.v(TagScanFragment.TAG, "--- getFirmwareVersion()   ----")
                RFID_INIT_STATUS = true
                val firmware = mRfidManager!!.firmwareVersion
                binding.textFirmware.text = getString(R.string.firmware) + firmware
            } else {
//                Log.v(TagScanFragment.TAG,"onStart()  --- getFirmwareVersion()   ----  mActivity.mRfidManager == null")
            }
        }, 5000)


    }

    override fun onDestroy() {
        super.onDestroy()

        SoundTool.getInstance(this@RFIDScannerActivity).release()
        RFID_INIT_STATUS = false
        if (mRfidManager != null) {
            mRfidManager!!.disConnect()
            mRfidManager!!.release()

//            Log.d(MainActivity.TAG, "onDestroyView: rfid close")
            //            System.exit(0);
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == 523 && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            //TODO set scanning functionality on system key event
            println(event.keyCode)
            println(event.action)

            if (binding.scanStartBtn.getText() == getString(R.string.btInventory)) {
                binding.scanStartBtn.performClick()
            }
            else if (binding.scanStartBtn.getText() == getString(R.string.btn_save_Inventory)){
                binding.scanClearBtn.performClick()
            }

            return true
        }
        else if (event.keyCode == 523 && event.action == KeyEvent.ACTION_UP && event.repeatCount == 0) {
            //TODO set scanning functionality on system key event
            println(event.keyCode)
            println(event.action)

            return true
        }
        return super.dispatchKeyEvent(event)
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    private fun initCheckBox(){
        binding.checkBox.setOnCheckedChangeListener { _, b ->
            if (mRfidManager != null) {
                if (b) {
                    mRfidManager!!.queryMode = QueryMode.EPC_TID
                } else {
                    mRfidManager!!.queryMode = QueryMode.EPC
                }
            }
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
                binding.scanStartBtn.text = getString(R.string.btInventory)
                binding.scanClearBtn.text = getString(R.string.btn_stop_Inventory)
                initClearData()
            }
        }
    }
    private fun initStartScanButton(){

        binding.scanStartBtn.setOnClickListener {
            if (RFID_INIT_STATUS) {
                if (binding.scanStartBtn.getText() == getString(R.string.btInventory)) {
                    setCallback()
                    binding.scanStartBtn.text = getString(R.string.btn_save_Inventory)
                    binding.scanClearBtn.text = getString(R.string.btn_stop_Inventory)
                    setScanStatus(true)
                }
                else if (binding.scanStartBtn.getText() == getString(R.string.btn_save_Inventory)){

                    if (binding.inputDesignName.text.toString().trim().isEmpty()){
                        DialogHelper.Alert_Selection(this@RFIDScannerActivity,"Please Select Design Name!!","OK","")
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
            } else {
//                Log.d(TagScanFragment.TAG,"scanStartBtn  RFID ")
                Toast.makeText(this@RFIDScannerActivity, "RFID Not initialized", Toast.LENGTH_SHORT).show()
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

    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

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

    private fun initActionbar(appbarTitle : String = "CTS", leftButton : Int = R.drawable.icon_back, rightButton : Int = 0, leftButtonClick : () -> Unit = {}, rightButtonClick : () -> Unit = {}, isVisible : Boolean = true){

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
            sharedViewModel.showDesignNameListingDialog(this@RFIDScannerActivity,designNameList,false){
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
            mRfidManager!!.startInventory(0.toByte())

        } else {
//            Log.v(TAG, "--- stopInventory()   ----")
            mRfidManager!!.stopInventory()
            handlerStopUI()
        }
    }

    private fun handlerUpdateUI() {
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI, 1000)
    }

    private fun handlerStopUI() {
        mHandler.removeCallbacksAndMessages(null)
    }

    internal inner class ScanCallback : IRfidCallback {

        override fun onInventoryTag(EPC: String, TID: String, strRSSI: String) {
            notiyDatas(EPC, TID, strRSSI)
            Log.d("EPC", EPC)
        }

        override fun onInventoryTagEnd() {
            Log.d(TAG, "onInventoryTag()")
        }
    }

    private fun notiyDatas(s2: String, TID: String, strRSSI: String) {
        val mapContainStr = if (!TextUtils.isEmpty(TID)) TID else s2

        runOnUiThread {
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

    /*private fun notiyDatas(s2: String, TID: String, strRSSI: String) {

    val mapContainStr = if (!TextUtils.isEmpty(TID)) TID else s2

//        Log.d(TAG, "onInventoryTag: EPC: $s2")
//        SoundTool.getInstance(BaseApplication.context).playBeep(1)
    runOnUiThread(object : Runnable {
        override fun run() {
            run {
                println(mapData)
                if (mapData.containsKey(mapContainStr)) {
                    val tagScan = mapData[mapContainStr]!!
                    tagScan.count   = 1
                    tagScan.tid     = TID
                    tagScan.rssi    = strRSSI
                    tagScan.epc     = s2
                    mapData[mapContainStr] = tagScan
                } else {

                    val tagScan = TagScan(strRSSI,s2, TID, 1)
                    mapData[mapContainStr] = tagScan
                    data.add(tagScan)

                }
                binding.scanTotalText.text = "0"
                scanListAdapterRv.updateList(data)
                binding.scanCountText.text = mapData.keys.size.toString()

/*                    val nowTime = System.currentTimeMillis()
                if ((nowTime - time) > 1000) {
//                        mapData.clear()
//                        data.clear()
//                        scanListAdapterRv.updateList(arrayListOf())

                    time = nowTime
                    data.clear()
                    data = ArrayList<TagScan>(mapData.values)
//                        Log.d(TAG,"onInventoryTag: data = " + data.toTypedArray().contentToString())
                    scanListAdapterRv.updateList(data)
                    binding.scanCountText.text = mapData.keys.size.toString() + ""
                }*/

            }
        }
    })
}*/
    private fun readTagOnce() {

        val data = mRfidManager!!.readTagOnce(0.toByte(), 0.toByte())
    }

    fun setCallback() {
        if (mRfidManager != null) {
            if (callback == null) {
                callback = ScanCallback()
            }
            mRfidManager!!.registerCallback(callback)
        }
    }

    private fun initRfid() {

        USDKManager.getInstance().init(this@RFIDScannerActivity) { status ->
            if (status == USDKManager.STATUS.SUCCESS) {
//                Log.d(MainActivity.TAG, "initRfid()  success.")
                mRfidManager = USDKManager.getInstance().rfidManager
                setCallback()
                mRfidManager?.setOutputPower(30.toByte())

//                Log.d(MainActivity.TAG,"initRfid: getDeviceId() = " + mRfidManager?.deviceId)

                readerType = mRfidManager?.readerType!!

                runOnUiThread {
                    Toast.makeText(this@RFIDScannerActivity,"module：$readerType", Toast.LENGTH_LONG).show()
                }
                mRfidManager!!.queryMode = QueryMode.EPC_TID

//                Log.d(MainActivity.TAG,"initRfid: GetReaderType() = $readerType")
            } else {
//                Log.d(MainActivity.TAG, "initRfid  fail.")
            }
        }
    }

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE

    //http://150.129.105.34/api/v1/productmanufacture/getWorkshopList
    private fun getDesignNameList(){
        val requestMap = mutableMapOf<String, Any>() // Empty mutable map

//        val getWorkshopListUrl = prefs.host + UrlEndPoints.GET_DESIGN_NAME_LIST
        val getWorkshopListUrl = "http://192.168.29.102:81/api/" + UrlEndPoints.GET_DESIGN_NAME_LIST
        sharedViewModel.api_service(this@RFIDScannerActivity,getWorkshopListUrl,requestMap,{},{ getWorkshopResponse ->
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
        val getWorkshopListUrl = "http://192.168.29.102:81/api/" + "v1/barcodeentry/savebarcodeentry"
        sharedViewModel.api_service(this@RFIDScannerActivity,getWorkshopListUrl,requestMap,{},{ entryResponse ->
            println(entryResponse)
            val saveRFIDBarcodeEntry: SaveRFIDBarcodeEntry? = AppHelper.convertJsonToModel(entryResponse)

            if (saveRFIDBarcodeEntry != null) {
                saveRFIDBarcodeEntry.let {
                    if (it.Status.equals("Success", true)){
                        DialogHelper.Alert_Selection(this@RFIDScannerActivity,it.ErrorMessage.toString(),"OK","")
                    }
                    else{
                        DialogHelper.Alert_Selection(this@RFIDScannerActivity,it.ErrorMessage.toString(),"OK","")
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

    //endregion API SERVICE

}