package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class AccessRights(
    @SerializedName("accessrightslist" ) var accessrightsArray : ArrayList<Access> = arrayListOf()
){
    data class Access (

        @SerializedName("accessid"     ) var accessid     : String? = null,
        @SerializedName("accessname"   ) var accessname   : String? = null,
        @SerializedName("accessrights" ) var accessrights : String? = null,
        @SerializedName("parentid"     ) var parentid     : String? = null,
        @SerializedName("isparent"     ) var isparent     : String? = null,
        var staticimage                                   : Int?    = null

    )
}
