package com.tupleinfotech.productbarcodescanner.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object NetworkCall {

    suspend fun <T> SafeApiCall(dispatcher  : CoroutineDispatcher = Dispatchers.IO,apiCall : suspend ()->T) : NetworkResult<T> {
        return withContext(dispatcher){
            try{

                NetworkResult.Success(apiCall.invoke())

            }
            catch (throwable:Throwable){

                when(throwable){

                    is IOException      -> NetworkResult.Error(
                        "IO Exception =>" + throwable.message,
                        null
                    )
                    is HttpException    -> NetworkResult.Error(
                        "HTTP Exception =>" + throwable.message,
                        null
                    )
                    else                -> NetworkResult.Error(
                        "Something Went Wrong =>" + throwable.message,
                        null
                    )

                }

            }
        }
    }
}