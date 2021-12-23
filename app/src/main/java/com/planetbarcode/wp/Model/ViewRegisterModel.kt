package com.planetbarcode.wp.Model

class ViewRegisterModel {
    var qr:String? = null
    var serial:String? = null
    var storetype:String? = null
    var storecode:String? = null
    var material:String? = null
    var master:String? = null
    var date:String? = null
    var inspector:String? = null
    var inspection_date:String? = null
    var fa:String? = null

    constructor(qr: String?, serial: String?, storetype: String?, storecode: String?, material: String?, master: String?, date: String?, inspector: String?, inspection_date: String?, fa: String?) {
        this.qr = qr
        this.serial = serial
        this.storetype = storetype
        this.storecode = storecode
        this.material = material
        this.master = master
        this.date = date
        this.inspector = inspector
        this.inspection_date = inspection_date
        this.fa = fa
    }
}