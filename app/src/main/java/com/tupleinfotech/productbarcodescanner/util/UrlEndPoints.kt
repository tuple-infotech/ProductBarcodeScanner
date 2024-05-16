package com.tupleinfotech.productbarcodescanner.util

class UrlEndPoints {
    companion object {

        const val GET_DATA_BY_BARCODE               = "v1/product/getdatabybarcode"
        const val GET_DATA_BY_BARCODE_MANUFACTURE   = "v1/productmanufacture/getdatabybarcode"
        const val ACCOUNT_LOGIN                     = "v1/account/login"
        const val DASHBOARD_QUICK_INFO              = "v1/dashboardapi/getQuickInfoData"
        const val GET_WORKSHOP_LIST                 = "v1/productmanufacture/getWorkshopList"
        const val GET_WAREHOUSE_LIST                = "v1/warehouseApi/getWarehouseList"
        const val GET_PRODUCTION_DETAILS            = "v1/productionreportrequest/getproductiondetails"
        const val UPDATE_PIPE_ENTRY                 = "v1/productmanufacture/updatePipeEntry"
        const val UPDATE_ADD_ENTRY                  = "v1/productmanufacture/addPipeEntry"
        const val SAVE_WAREHOUSE_IN_DATA            = "v1/warehouseApi/saveWarehouseInData"
        const val UPDATE_WAREHOUSE_IN_DATA          = "v1/warehouseApi/updateWarehouseOutData"
        const val GET_PRODUCT_WAREHOUSE_DATA        = "v1/warehouseApi/getProductWarehouseData"
        const val GET_WAREHOUSE_BARCODE_DATA        = "v1/warehouseApi/getBarcodeDetails"
        const val GET_VENDOR_LIST        = "v1/warehouseApi/getVendorList"
        const val GET_DESIGN_NAME_LIST        = "v1/designmaster/getdesignmaster"
    }
}