package com.jmsc.postab.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AddHost(
    var host_name   : String    = "",
    var host_ip     : String    = "",
    var host_port   : String    = "",
    var username    : String    = "",
    var password    : String    = "",
    val isSelected  : Boolean   = false,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}



