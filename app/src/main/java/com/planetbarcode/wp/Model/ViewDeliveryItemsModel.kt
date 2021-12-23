package com.planetbarcode.wp.Model

class ViewDeliveryItemsModel {
    var doc:String? = null
    var items:String? = null
    var store_type:String? = null
    var store_code:String? = null

    constructor(doc: String?, items: String?, store_type: String?, store_code: String?) {
        this.doc = doc
        this.items = items
        this.store_type = store_type
        this.store_code = store_code
    }
}