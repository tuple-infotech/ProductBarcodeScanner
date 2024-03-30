package com.tupleinfotech.productbarcodescanner.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

@JvmSuppressWildcards
interface Api {

    @POST()
    suspend fun api_url(@Url url: String, @Body requestMap: Map<String, Any>) : Response<ResponseBody>

}