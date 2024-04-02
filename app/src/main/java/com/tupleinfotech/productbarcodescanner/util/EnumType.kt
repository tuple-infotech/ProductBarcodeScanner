package com.tupleinfotech.productbarcodescanner.util

enum class CHECK_SCAN {
    CHK_IS_ACTIVE,
    CHK_DOB,
    CHK_SALE_PRICE_ZERO,
    CHK_SUFFICIENT_STOCK,
    CHK_SALEPRICE_WITH_COSTPRICE,
    CHK_GIFTCARD,
    CHK_CHARGE_DEPOSIT,
    CHK_SCAN_SUCCESS
}

enum class TEXT_COLOR {
    DEFAULT,
    SELECTED,
    BLUE,
    PURPLE,
    BLACK,
}

enum class TAX_CODE {
    CATEGORY,
    OFFER,
    EBT
}

enum class RESERVATION {
    NOTHING,
    CHARGE_DEPOSIT,
    BUTTON_DEPOSIT,
    LOOK_RESERVED,
    RETURN_DEPOSIT,
    DO_RESERVATION,
}


enum class SER_STATUS(val value: String) {
    SUCCESS("Success"),
    FAILED("Failed"),
}
