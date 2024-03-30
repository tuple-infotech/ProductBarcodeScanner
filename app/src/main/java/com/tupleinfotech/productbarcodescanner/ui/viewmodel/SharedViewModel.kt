package com.tupleinfotech.productbarcodescanner.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tupleinfotech.productbarcodescanner.network.NetworkResult
import com.tupleinfotech.productbarcodescanner.repository.BarcodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor (private val barcodeRepository: BarcodeRepository) : ViewModel() {

    //region Common Api Calling Method

    fun api_service(
        context: Context,
        apiUrl : String,
        requestMap: Map<String, Any>,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        viewModelScope.launch {
            try {
                barcodeRepository.api_url_repository(apiUrl,requestMap).collectLatest { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {

                            is NetworkResult.Error      -> {
                                Log.i("==>", "ERROR" + response.message)
                                onFailure(response.message.toString())
                            }
                            is NetworkResult.Loading    -> {
                                Log.i("==>", "LOADING" + response.message)
                            }
                            is NetworkResult.Success    -> {
                                if (response.data?.isSuccessful == true) {
                                    val responseBodyString = response.data.body()?.string()
                                    onSuccess(responseBodyString.toString())
                                    Log.i("==>", "SUCCESS")
                                    // Handle the JSON string as needed
                                } else {
                                    Log.i("==>", "ERROR")
                                    onFailure(response.message.toString())
                                    // Handle error
                                }
                            }

                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("==>", "Exception: ${e.message}")
            }
        }

    }

    //endregion Common Api Calling Method

}