package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class WorkshopListResponse (
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?                   = null,
    @SerializedName("Status"            ) var Status            : String?                   = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?                   = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?                   = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String>         = arrayListOf(),

    @SerializedName("WarehouseList"     ) var WarehouseList     : ArrayList<List>           = arrayListOf(),
    @SerializedName("VendorList"        ) var VendorList        : ArrayList<List>           = arrayListOf(),
    @SerializedName("FactoryList"       ) var Factorylist       : ArrayList<List>           = arrayListOf(),
    @SerializedName("designMasterList"  ) var designMasterList  : ArrayList<List>           = arrayListOf(),

){

    data class List (

        @SerializedName("FactoryId"     ) var FactoryId         : Int?                      = null,
        @SerializedName("FactoryName"   ) var FactoryName       : String?                   = null,
        @SerializedName("WarehouseId"   ) var WarehouseId       : Int?                      = null,
        @SerializedName("WarehouseName" ) var WarehouseName     : String?                   = null,
        @SerializedName("VendorId"      ) var VendorId          : Int?                      = null,
        @SerializedName("VendorName"    ) var VendorName        : String?                   = null,
        @SerializedName("DesignId"      ) var DesignId          : Int?                      = null,
        @SerializedName("DesignName"    ) var DesignName        : String?                   = null



    )
}
