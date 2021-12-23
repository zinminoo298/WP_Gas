package com.planetbarcode.wp.Model

class MaintenanceCompanyModel {
    var code:String? = null
    var maintenance_company:String? = null

    constructor(code: String?, maintenance_company: String?) {
        this.code = code
        this.maintenance_company = maintenance_company
    }
}