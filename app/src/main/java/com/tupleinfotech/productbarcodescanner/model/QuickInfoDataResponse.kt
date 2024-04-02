package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class QuickInfoDataResponse (

    @SerializedName("QuickInfo"         ) var QuickInfoDetails  : QuickInfo?        = QuickInfo(),
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?           = null,
    @SerializedName("Status"            ) var Status            : String?           = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?           = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?           = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String> = arrayListOf()
){

    data class QuickInfo (

        @SerializedName("ActiveUsers"       ) var ActiveUsers       : String? = null,
        @SerializedName("InActiveUsers"     ) var InActiveUsers     : String? = null,
        @SerializedName("TotalBarcodePrint" ) var TotalBarcodePrint : String? = null,
        @SerializedName("TotalProduction"   ) var TotalProduction   : String? = null,
        @SerializedName("TotalInWard"       ) var TotalInWard       : String? = null,
        @SerializedName("TotalOutWard"      ) var TotalOutWard      : String? = null,
        @SerializedName("TotalFactory"      ) var TotalFactory      : String? = null,
        @SerializedName("TotalWarehouse"    ) var TotalWarehouse    : String? = null

    )
}


