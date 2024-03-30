package com.tupleinfotech.productbarcodescanner.repository

import com.tupleinfotech.productbarcodescanner.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface BarcodeRepository {

    fun api_url_repository(url: String, requestMap: Map<String, Any>) : Flow<NetworkResult<Response<ResponseBody>>>

}