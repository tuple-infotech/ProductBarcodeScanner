package com.tupleinfotech.productbarcodescanner.data.repository.barcode

import com.tupleinfotech.productbarcodescanner.data.api.Api
import com.tupleinfotech.productbarcodescanner.network.NetworkCall
import com.tupleinfotech.productbarcodescanner.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeRepositoryImpl @Inject constructor(private val api: Api) : BarcodeRepository {

    override fun api_url_repository(url: String, requestMap: Map<String, Any>): Flow<NetworkResult<Response<ResponseBody>>> = flow {
        emit(NetworkResult.Loading())
        val response = NetworkCall.SafeApiCall { api.api_url(url,requestMap) }
        emit(response)
    }

}