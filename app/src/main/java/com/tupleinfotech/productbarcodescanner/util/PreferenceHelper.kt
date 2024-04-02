package com.tupleinfotech.productbarcodescanner.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    val HOST = "HOST"

    val IP_ADDRESS = "IPADDRESS"
    val PORT = "PORT"
    val LAST_SELECTED_HOST_ID = "HOST_ID"
    val USER_ID = "USER_ID"
    val USER_NAME = "USER_NAME"
    val USER_PASSWORD = "PASSWORD"

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }
    var SharedPreferences.userId
        get() = getInt(USER_ID, 0)
        set(value) {
            editMe {
                it.putInt(USER_ID, value)
            }
        }

    var SharedPreferences.host
        get() = getString(HOST, "")
        set(value) {
            editMe {
                it.putString(HOST, value)
            }
        }

    var SharedPreferences.ipAddress
        get() = getString(IP_ADDRESS, "")
        set(value) {
            editMe {
                it.putString(IP_ADDRESS, value)
            }
        }

    var SharedPreferences.port
        get() = getString(PORT, "")
        set(value) {
            editMe {
                it.putString(PORT, value)
            }
        }


    var SharedPreferences.host_id
        get() = getInt(LAST_SELECTED_HOST_ID, 0)
        set(value) {
            editMe {
                it.putInt(LAST_SELECTED_HOST_ID, value)
            }
        }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }

    var SharedPreferences.password
        get() = getString(USER_PASSWORD, "")
        set(value) {
            editMe {
                it.putString(USER_PASSWORD, value)
            }
        }

    var SharedPreferences.username
        get() = getString(USER_NAME, "")
        set(value) {
            editMe {
                it.putString(USER_NAME, value)
            }
        }
}