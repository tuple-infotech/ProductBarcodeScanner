package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

/**
 * @Author: athulyatech
 * @Date: 5/16/24
 */
data class SaveRFIDBarcodeEntry(

    @SerializedName("BarcodePrints"     ) var BarcodePrints     : ArrayList<String> = arrayListOf(),
    @SerializedName("ErrorMessage"      ) var ErrorMessage      : String?           = null,
    @SerializedName("Status"            ) var Status            : String?           = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?           = null,
    @SerializedName("RedirectUrl"       ) var RedirectUrl       : String?           = null,
    @SerializedName("ValidationMessage" ) var ValidationMessage : ArrayList<String> = arrayListOf()
)
