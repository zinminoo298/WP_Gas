package com.planetbarcode.wp.Model

class MaterialCodeModel {
    var material_code:String? = null
    var qty:Int? = null
    var unit:String? = null

    constructor(material_code: String?, qty: Int?, unit: String?) {
        this.material_code = material_code
        this.qty = qty
        this.unit = unit
    }
}