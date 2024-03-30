package com.jmsc.postab.ui.dialogfragment.addhost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmsc.postab.data.repository.db.DBRepository
import com.jmsc.postab.db.AddHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHostViewModel @Inject constructor(private val dbRepository: DBRepository) : ViewModel() {

    fun addHost(host_name: String, ip: String, port: String) {
        val host = AddHost(host_name, ip, port, "", "", )
        viewModelScope.launch {
            dbRepository.insertHost(host)
        }
    }

    fun updateHost(host: AddHost) {
        viewModelScope.launch {
            dbRepository.updateHost(host)
        }
    }

    fun deleteHost(host: AddHost) {
        viewModelScope.launch {
            dbRepository.deleteHost(host)
        }
    }

    fun getHosts(): Flow<List<AddHost>> {
        return dbRepository.getAllHost()
    }

    suspend fun checkHostExit(host_name: String, host_ip: String, host_port: String): Boolean {
        return dbRepository.checkHostExit(host_name, host_ip, host_port)
    }

    suspend fun selectHost(host_ip: String, host_port: String) : AddHost{
        return dbRepository.selectHost(host_ip,host_port)
    }

    suspend fun selectHost(id: Int) : AddHost{
        return dbRepository.selectHostById(id)
    }
}
