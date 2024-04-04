package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class ProductionDetailsResponse(
    @SerializedName("products"          ) var products          : ArrayList<Products> = arrayListOf(),
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?             = null,
    @SerializedName("Status"            ) var Status            : String?             = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?             = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?             = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String>   = arrayListOf()
){
    data class Products (

        @SerializedName("LocationId"        ) var LocationId        : Int?              = null,
        @SerializedName("PipeId"            ) var PipeId            : Int?              = null,
        @SerializedName("Barcode"           ) var Barcode           : String?           = null,
        @SerializedName("DesignName"        ) var DesignName        : String?           = null,
        @SerializedName("WarehouseId"       ) var WarehouseId       : Int?              = null,
        @SerializedName("WarehouseRowNo"    ) var WarehouseRowNo    : String?           = null,
        @SerializedName("WarehouseCellNo"   ) var WarehouseCellNo   : String?           = null,
        @SerializedName("WarehouseInNotes"  ) var WarehouseInNotes  : String?           = null,
        @SerializedName("WarehouseOutNotes" ) var WarehouseOutNotes : String?           = null,
        @SerializedName("CreatedDate"       ) var CreatedDate       : String?           = null,
        @SerializedName("WarehouseInTime"   ) var WarehouseInTime   : String?           = null,
        @SerializedName("WarehouseOutTime"  ) var WarehouseOutTime  : String?           = null,
        @SerializedName("FactoryName"       ) var FactoryName       : String?           = null,
        @SerializedName("WarehouseName"     ) var WarehouseName     : String?           = null,
        @SerializedName("Isdispatched"      ) var Isdispatched      : Int?              = null,
        @SerializedName("VendorName"        ) var VendorName        : String?           = null,
        @SerializedName("VendorId"          ) var VendorId          : Int?              = null,
        @SerializedName("components"        ) var components        : ArrayList<Components> = arrayListOf()

    )
    data class Components (

        @SerializedName("ComponentsId"   ) var ComponentsId   : Int?    = null,
        @SerializedName("PipeId"         ) var PipeId         : Int?    = null,
        @SerializedName("Barcode"        ) var Barcode        : String? = null,
        @SerializedName("ComponentsName" ) var ComponentsName : String? = null,
        @SerializedName("ComponentsQty"  ) var ComponentsQty  : String? = null

    )
}
