package com.planetbarcode.wp.Model

class DeliveryExportModel {
    var header:String? = null
    var doc:String? = null
    var route:String? = null
    var deliverydate:String? = null
    var materiallist:String? = null
    var round:String? = null
    var carcode:String? = null
    var shippingdate:String? = null
    var locationcode:String? = null
    var destinationtype:String? = null
    var destinationcode:String? = null
    var gastank:String? = null

    constructor(header: String?, doc: String?, route: String?, planningdate: String?, materiallist: String?, round: String?, carcode: String?, deliverycode: String?, storecode: String?, storetype: String?, locationcode: String?, gastank: String?) {
        this.header = header
        this.doc = doc
        this.route = route
        this.deliverydate = planningdate
        this.materiallist = materiallist
        this.round = round
        this.carcode = carcode
        this.shippingdate = deliverycode
        this.locationcode = storecode
        this.destinationtype = storetype
        this.destinationcode = locationcode
        this.gastank = gastank
    }
}