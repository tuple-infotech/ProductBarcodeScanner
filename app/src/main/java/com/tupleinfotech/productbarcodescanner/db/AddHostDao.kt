package com.jmsc.postab.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddHostDao {

    @Query("SELECT * FROM AddHost")
    fun getAllHost(): Flow<List<AddHost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHost(host: AddHost)

    @Update
    suspend fun updateHost(host: AddHost)

    @Query("DELETE FROM AddHost WHERE id = :id")
    suspend fun deleteHost(id: Int)

    @Query("SELECT * FROM AddHost WHERE id = :id")
    suspend fun selectHostById(id: Int): AddHost

    @Delete
    suspend fun deleteHost(host: AddHost)

    @Query("SELECT EXISTS(SELECT * FROM AddHost WHERE host_name = :host_name OR (host_ip = :host_ip and host_port = :host_port))")
    suspend fun checkHostExit(host_name: String,host_ip: String,host_port: String) : Boolean

    @Query("SELECT * FROM AddHost WHERE host_ip = :host_ip and host_port = :host_port")
    suspend fun selectHost(host_ip: String,host_port: String) : AddHost
}