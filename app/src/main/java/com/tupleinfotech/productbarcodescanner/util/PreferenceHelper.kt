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

    val USER_FULLNAME = "FULLNAME"
    val USER_EMAIL = "EMAIL"
    val USER_PHONE = "PHONE"
    val USER_ADDRESS = "ADDRESS"
    val USER_DOB = "DOB"
    val USER_GENDER = "GENDER"
    val USER_PROFILE_IMAGE = "PROFILE_IMAGE"

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }
    var SharedPreferences.userId
        get() = getString(USER_ID, "0")
        set(value) {
            editMe {
                it.putString(USER_ID, value)
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
    var SharedPreferences.userfullname
        get() = getString(USER_FULLNAME, "")
        set(value) {
            editMe {
                it.putString(USER_FULLNAME, value)
            }
        }

    var SharedPreferences.useremail
        get() = getString(USER_EMAIL, "")
        set(value) {
            editMe {
                it.putString(USER_EMAIL, value)
            }
        }
    var SharedPreferences.userphone
        get() = getString(USER_PHONE, "")
        set(value) {
            editMe {
                it.putString(USER_PHONE, value)
            }
        }
    var SharedPreferences.useraddress
        get() = getString(USER_ADDRESS, "")
        set(value) {
            editMe {
                it.putString(USER_ADDRESS, value)
            }
        }
    var SharedPreferences.userdob
        get() = getString(USER_DOB, "")
        set(value) {
            editMe {
                it.putString(USER_DOB, value)
            }
        }
    var SharedPreferences.usergender
        get() = getString(USER_GENDER, "")
        set(value) {
            editMe {
                it.putString(USER_GENDER, value)
            }
        }
    var SharedPreferences.userprofileimage
        get() = getString(USER_PROFILE_IMAGE, "")
        set(value) {
            editMe {
                it.putString(USER_PROFILE_IMAGE, value)
            }
        }
}