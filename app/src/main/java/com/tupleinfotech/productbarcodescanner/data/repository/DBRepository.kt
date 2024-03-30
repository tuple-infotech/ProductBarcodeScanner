package com.jmsc.postab.data.repository.db

import com.jmsc.postab.db.AddHost
import kotlinx.coroutines.flow.Flow

interface DBRepository {
    fun getAllHost(): Flow<List<AddHost>>

    suspend fun insertHost(host: AddHost)

    suspend fun updateHost(host: AddHost)

    suspend fun deleteHost(host: AddHost)

    suspend fun selectHostById(id: Int): AddHost

    suspend fun checkHostExit(host_name: String,host_ip: String,host_port: String): Boolean

    suspend fun selectHost(host_ip: String,host_port: String) : AddHost

}