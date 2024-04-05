package com.tupleinfotech.productbarcodescanner.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("User"                      ) var USER                  : User?                 = User(),
    @SerializedName("ErrorMessage"              ) var ErrorMessage          : String?               = null,
    @SerializedName("Status"                    ) var Status                : String?               = null,
    @SerializedName("StatusCode"                ) var StatusCode            : String?               = null,
    @SerializedName("RedirectUrl"               ) var RedirectUrl           : String?               = null,
    @SerializedName("ValidationMessage"         ) var ValidationMessage     : ArrayList<String>     = arrayListOf()

){
    data class Menu (

        @SerializedName("MenuId"                ) var MenuId                : String?               = null,
        @SerializedName("MenuName"              ) var MenuName              : String?               = null,
        @SerializedName("MenuLink"              ) var MenuLink              : String?               = null,
        @SerializedName("FileName"              ) var FileName              : String?               = null,
        @SerializedName("MenuIcon"              ) var MenuIcon              : String?               = null,
        @SerializedName("ParentId"              ) var ParentId              : String?               = null,
        @SerializedName("IsParent"              ) var IsParent              : String?               = null,
        @SerializedName("MenuAccess"            ) var MenuAccess            : String?               = null,
        @SerializedName("OrderBy"               ) var OrderBy               : String?               = null,
        @SerializedName("SubMenus"              ) var SubMenus              : ArrayList<Menu>       = arrayListOf()

    )
    data class User (

        @SerializedName("UserId"                ) var UserId                : String?               = null,
        @SerializedName("FirstName"             ) var FirstName             : String?               = null,
        @SerializedName("MiddleName"            ) var MiddleName            : String?               = null,
        @SerializedName("LastName"              ) var LastName              : String?               = null,
        @SerializedName("UserName"              ) var UserName              : String?               = null,
        @SerializedName("Email"                 ) var Email                 : String?               = null,
        @SerializedName("MobileNumber"          ) var MobileNumber          : String?               = null,
        @SerializedName("IsTwoStepVerification" ) var IsTwoStepVerification : String?               = null,
        @SerializedName("UserType"              ) var UserType              : String?               = null,
        @SerializedName("ProfileImage"          ) var ProfileImage          : String?               = null,
        @SerializedName("ProfileImagePath"      ) var ProfileImagePath      : String?               = null,
        @SerializedName("CompanyId"             ) var CompanyId             : String?               = null,
        @SerializedName("LocationId"            ) var LocationId            : String?               = null,
        @SerializedName("Address"               ) var Address               : String?               = null,
        @SerializedName("Dob"                   ) var Dob                   : String?               = null,
        @SerializedName("Gender"                ) var Gender                : String?               = null,
        @SerializedName("menu"                  ) var menu                  : ArrayList<Menu>       = arrayListOf(),
        @SerializedName("accessRights"          ) var accessRights          : ArrayList<String>     = arrayListOf(),

    )
}
