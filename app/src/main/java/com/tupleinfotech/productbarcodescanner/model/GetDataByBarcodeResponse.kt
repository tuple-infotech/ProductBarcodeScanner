package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class GetDataByBarcodeResponse(
    @SerializedName("products"          ) var products          : Products?         = Products(),
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?           = null,
    @SerializedName("Status"            ) var Status            : String?           = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?           = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?           = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String> = arrayListOf(),
    @SerializedName("pipes"             ) var pipes             : ArrayList<Pipes>  = arrayListOf()
){

    data class Pipes (

        @SerializedName("PipeId"       ) var PipeId       : Int?                  = null,
        @SerializedName("Barcode"      ) var Barcode      : String?               = null,
        @SerializedName("DesignName"   ) var DesignName   : String?               = null,
        @SerializedName("CreatedBy"    ) var CreatedBy    : Int?                  = null,
        @SerializedName("FactoryId"    ) var FactoryId    : Int?                  = null,
        @SerializedName("WarehouseId"  ) var WarehouseId  : Int?                  = null,
        @SerializedName("CreatedDate"  ) var CreatedDate  : String?               = null,
        @SerializedName("WhInTime"     ) var WhInTime     : String?               = null,
        @SerializedName("WhOutTime"    ) var WhOutTime    : String?               = null,
        @SerializedName("WhInNotes"    ) var WhInNotes    : String?               = null,
        @SerializedName("WhOutNotes"   ) var WhOutNotes   : String?               = null,
        @SerializedName("Isdispatched" ) var Isdispatched : Int?                  = null,
        @SerializedName("VendorId"     ) var VendorId     : Int?                  = null,
        @SerializedName("VendorName"   ) var VendorName   : String?               = null,
        @SerializedName("Components"   ) var Components   : ArrayList<Components> = arrayListOf()

    )

    data class Products (

        @SerializedName("LocationId"        ) var LocationId        : Int?                  = null,
        @SerializedName("PipeId"            ) var PipeId            : Int?                  = null,
        @SerializedName("Barcode"           ) var Barcode           : String?               = null,
        @SerializedName("DesignName"        ) var DesignName        : String?               = null,
        @SerializedName("WarehouseId"       ) var WarehouseId       : Int?                  = null,
        @SerializedName("WarehouseRowNo"    ) var WarehouseRowNo    : String?               = null,
        @SerializedName("WarehouseCellNo"   ) var WarehouseCellNo   : String?               = null,
        @SerializedName("WarehouseInNotes"  ) var WarehouseInNotes  : String?               = null,
        @SerializedName("WarehouseOutNotes" ) var WarehouseOutNotes : String?               = null,
        @SerializedName("CreatedDate"       ) var CreatedDate       : String?               = null,
        @SerializedName("WarehouseInTime"   ) var WarehouseInTime   : String?               = null,
        @SerializedName("WarehouseOutTime"  ) var WarehouseOutTime  : String?               = null,
        @SerializedName("FactoryName"       ) var FactoryName       : String?               = null,
        @SerializedName("WarehouseName"     ) var WarehouseName     : String?               = null,
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
