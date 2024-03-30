package com.tupleinfotech.productbarcodescanner.network

sealed class NetworkResult<T>(val data: T?=null,val message:String?=null) {

    class Success<T>(data : T)                              : NetworkResult<T>(data)

    class Loading<T>                                        : NetworkResult<T>()

    class Error<T>(message : String?, data : T? = null)     : NetworkResult<T>(data, message)
}