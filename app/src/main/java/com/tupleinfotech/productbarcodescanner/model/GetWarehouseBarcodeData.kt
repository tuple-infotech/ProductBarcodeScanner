package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class GetWarehouseBarcodeData(
    @SerializedName("WarehouseList"     ) var warehouseList     : ArrayList<WarehouseList> = arrayListOf(),
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?                  = null,
    @SerializedName("Status"            ) var Status            : String?                  = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?                  = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?                  = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String>        = arrayListOf()
){
    data class WarehouseList (

        @SerializedName("LocationId"        ) var LocationId        : Int?    = null,
        @SerializedName("PipeId"            ) var PipeId            : Int?    = null,
        @SerializedName("Barcode"           ) var Barcode           : String? = null,
        @SerializedName("WarehouseId"       ) var WarehouseId       : Int?    = null,
        @SerializedName("WarehouseRowNo"    ) var WarehouseRowNo    : String? = null,
        @SerializedName("WarehouseCellNo"   ) var WarehouseCellNo   : String? = null,
        @SerializedName("WarehouseInNotes"  ) var WarehouseInNotes  : String? = null,
        @SerializedName("WarehouseOutNotes" ) var WarehouseOutNotes : String? = null,
        @SerializedName("VendorName"        ) var VendorName        : String? = null,
        @SerializedName("VendorId"          ) var VendorId          : Int?    = null,
        @SerializedName("IsDispatched"      ) var IsDispatched      : Int?    = null

    )
}
