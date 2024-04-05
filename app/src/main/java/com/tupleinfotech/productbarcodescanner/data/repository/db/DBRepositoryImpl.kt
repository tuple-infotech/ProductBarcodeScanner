package com.tupleinfotech.productbarcodescanner.data.repository.db

import android.util.Log
import androidx.annotation.WorkerThread
import com.jmsc.postab.db.AddHost
import com.jmsc.postab.db.AddHostDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBRepositoryImpl @Inject constructor(private val addHostDao: AddHostDao): DBRepository {
    override fun getAllHost(): Flow<List<AddHost>> {
        return addHostDao.getAllHost()
    }

    @WorkerThread
    override suspend fun insertHost(host: AddHost) {
        addHostDao.insertHost(host)
    }

    @WorkerThread
    override suspend fun updateHost(host: AddHost) {
            addHostDao.updateHost(host)
    }

    @WorkerThread
    override suspend fun deleteHost(host: AddHost) {
        addHostDao.deleteHost(host)
    }

    override suspend fun selectHostById(id: Int): AddHost {
        return addHostDao.selectHostById(id)
    }

    override suspend fun checkHostExit(host_name: String,host_ip: String,host_port: String): Boolean {
        Log.i("==check", "$host_name $host_ip")
        return addHostDao.checkHostExit(host_name,host_ip,host_port)
    }

    override suspend fun selectHost(host_ip: String, host_port: String): AddHost {
        return addHostDao.selectHost(host_ip,host_port)
    }

}