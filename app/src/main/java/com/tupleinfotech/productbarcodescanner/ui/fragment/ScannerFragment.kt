package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tupleinfotech.productbarcodescanner.databinding.FragmentScannerBinding
import com.tupleinfotech.productbarcodescanner.ui.activity.MainActivity
import com.tupleinfotech.productbarcodescanner.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

@AndroidEntryPoint
@SuppressLint("UnsafeOptInUsageError")
class ScannerFragment : Fragment() {

    //region VARIABLES

    private var _binding                : FragmentScannerBinding?                   = null
    private val binding                 get()                                       = _binding!!
    private val sharedViewModel         : SharedViewModel                           by viewModels()
    private var cameraProviderFuture    : ListenableFuture<ProcessCameraProvider>?  = null
    private var imageAnalysis           : ImageAnalysis?                            = null
    private var cameraProvider          : ProcessCameraProvider?                    = null
    private var previewView             : PreviewView?                              = null
    private var barcodeText             : String?                                   = ""

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD

    private fun init(){

        sharedViewModel.initActionbarWithSideMenu(requireActivity() as MainActivity)

        cameraPermission()
        onBackPressed()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun cameraPermission(){

        if (!hasCameraPermission()) requestPermission()

        previewView = binding.scannerPreview

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture?.addListener({
            try {
                cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider!!)
            }
            catch (_: ExecutionException) {}
            catch (_: InterruptedException) {}

        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun hasCameraPermission(): Boolean = ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.CAMERA),10)
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview                 = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()

        val cameraSelector          = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView?.surfaceProvider)

        imageAnalysis               = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //keep the latest
            .build()

        val barcodeScannerOptions   = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS, Barcode.FORMAT_ALL_FORMATS).build()

        val scanner                 = BarcodeScanning.getClient(barcodeScannerOptions)
        imageAnalysis!!.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image   = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                scanner.process(image)
                    .addOnSuccessListener { barcodes -> processBarcode(barcodes) }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(),"Could not detect barcode!",Toast.LENGTH_SHORT).show()}
                    .addOnCompleteListener {
                        mediaImage.close()
                        imageProxy.close()
                    }
            }
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    private fun processBarcode(barcodes: List<Barcode>) {
        Log.e("barcodes:", barcodes.toString())

        for (barcode in barcodes) {
            barcodeText = barcode.rawValue

            if (barcodeText?.isNotEmpty() == true) {
                cameraProvider?.unbindAll()

                val bundle = Bundle()
                bundle.putString("Scanner", barcodeText)
                //bundle.putString("Scanner", "4154545")

                findNavController().previousBackStackEntry?.savedStateHandle?.set("ScannedResult", bundle)
                findNavController().popBackStack()

                break
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