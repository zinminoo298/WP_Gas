package com.planetbarcode.wp.DatabaseHandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.planetbarcode.wp.Delivery
import com.planetbarcode.wp.DeliveryTest
import com.planetbarcode.wp.Model.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHandler(private val context: Context) {

    companion object{
        val database = "database.db"
        var qrExists = true //if exists
        var docExists = true
        var registeredItems = 0
        var deliveryItems = 0
        var ViewRegisterItems = ArrayList<ViewRegisterModel>()
        var ViewDeleveryItems = ArrayList<ViewDeliveryModel>()
        var DeliveryExportItems = ArrayList<DeliveryExportModel>()
        var DeliveryItems = ArrayList<ViewDeliveryItemsModel>()
        var DeliverySerialItems = ArrayList<String>()
        var MaterialArray = ArrayList<String>()
        var MasterArray = ArrayList<String>()
        var StoreTypeArray = ArrayList<String>()
        var StoreCodeArray = ArrayList<String>()
        var InspectorArray = ArrayList<String>()
        var StoreNameArray = ArrayList<CustomerModel>()
        var MaintenanceArray = ArrayList<MaintenanceCompanyModel>()
        var MaintenanceList = ArrayList<MaintenanceCompanyModel>()
        var FillingPlantArray = ArrayList<FillingPlantModel>()
        var FillingPlantList = ArrayList<FillingPlantModel>()
        var CarCodeList = ArrayList<String>()
        var MaterialList = ArrayList<String>()
        var tank:String? = null
        var d_date:String? = null
        var s_date:String? = null
        var v_code:String? = null
        var round:String? = null
        var route:String? = null
        var lc_type:String? = null
        var lc_code:String? = null
        var des_type:String? = null
        var des_code:String? = null



    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile=context.getDatabasePath(database)
        if (!dbFile.exists()) {
            try {
                val checkDB=context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private fun copyDatabase(dbFile: File?) {
        val `is`=context.assets.open(database)
        val os= FileOutputStream(dbFile)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }

    fun checkQR(qr:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM planning WHERE qr='$qr'"
        val cursor = db.rawQuery(query,null)
        qrExists = cursor.moveToFirst()
        cursor.close()
        db.close()
    }

    fun addData(qr:String, serial:String, material:String, manufacturer:String, manufacture_date:String, fa:String, storetype:String, storecode:String, inspector: String, inspectoin_date:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("qr",qr)
        values.put("serial",serial)
        values.put("material",material)
        values.put("manufacturer",manufacturer)
        values.put("date",manufacture_date)
        values.put("inspector",inspector)
        values.put("inspection_date",inspectoin_date)
        values.put("fa",fa)
        values.put("store_type",storetype)
        values.put("store_code",storecode)
        db.insertWithOnConflict("planning", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        getRegisteredItems()
        db.close()
    }

    fun getRegisteredItems(){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) FROM planning"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            registeredItems = cursor.getInt(0)
        }
        else{
            registeredItems = 0
        }
        cursor.close()
        db.close()
    }

    fun getDoc(){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) FROM delivery"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            deliveryItems = cursor.getInt(0)
        }
        cursor.close()
        db.close()
    }

    fun getViewRegisterItems(){
        ViewRegisterItems.clear()
        val db = context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null)
        val query = "SELECT qr,serial,store_type,store_code,material, manufacturer,date,inspector,inspection_date,fa FROM planning "
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            do{
                val qr = cursor.getString(0)
                val serial = cursor.getString(1)
                val type = cursor.getString(2)
                val code = cursor.getString(3)
                val material = cursor.getString(4)
                val master = cursor.getString(5)
                val date = cursor.getString(6)
                val inspector = cursor.getString(7)
                val inspection_date = cursor.getString(8)
                val fa = cursor.getString(9)

                val data = ViewRegisterModel(qr, serial, type, code, material, master, date, inspector, inspection_date, fa)
                ViewRegisterItems.add(data)
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun checkDoc(doc:String){
        println("DOC $doc")
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT round,route,license,location_code,location_type,tank_type,delivery_date,shipping_date,destination_code,destination_type,material_list FROM delivery WHERE doc = '$doc'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            docExists = true
            Delivery.RouteList.clear()
            Delivery.RoundList.clear()
            Delivery.CarList.clear()
            Delivery.StoreCodeList.clear()
            Delivery.StoreTypeList.clear()
            Delivery.TankList.clear()

            round = cursor.getString(0)
            route = cursor.getString(1)
            v_code = cursor.getString(2)
            lc_code = cursor.getString(3)
            lc_type = cursor.getString(4)
            tank = cursor.getString(5)
            d_date = cursor.getString(6)
            s_date = cursor.getString(7)
            des_code = cursor.getString(8)
            des_type = cursor.getString(9)
            val Listseparated = cursor.getString(10).split("/").toTypedArray()
            try{
                if(Listseparated.size > 0){
                    DeliveryTest.MaterialListArray.clear()
                    for(element in Listseparated){
                        var split = element.split("-").toTypedArray()
                        DeliveryTest.MaterialListArray.add(MaterialCodeModel(split[0],Integer.parseInt(split[1]),split[2]))
                    }
                }
            }catch (e:Exception){
                DeliveryTest.MaterialListArray.clear()
            }


            if(lc_type == "a"){
                lc_type = "A-โรงซ่อม"
            }
            if(lc_type == "b"){
                lc_type = "B-โรงบรรจุ"
            }
            if(lc_type == "c"){
                lc_type = "C-ลูกค้า"
            }

            if(des_type == "a"){
                des_type = "A-โรงซ่อม"
            }
            if(des_type == "b"){
                des_type = "B-โรงบรรจุ"
            }
            if(des_type == "c"){
                des_type = "C-ลูกค้า"
            }

            if(tank == "0"){
                tank = "0=ถังหมุนเวียน"
            }
            if(tank == "1"){
                tank = "1=ถังมัดจำ"
            }
            if(tank == "2"){
                tank = "2=ถังยืม"
            }
            if(tank == "3"){
                tank = "3=ถังคืน"
            }
            if(tank == "4"){
                tank = "4=ถังซ่อม"
            }
            if(tank == "5"){
                tank = "5=ถังเคลม"
            }

        }
        else{
            round = ""
            route = ""
            v_code = ""
            lc_code = ""
            lc_type = "A-โรงซ่อม"
            tank = ""
            d_date = ""
            s_date = ""
            des_code = ""
            des_type = "A-โรงซ่อม"
            DeliveryTest.MaterialListArray.clear()
            docExists = false
        }
        cursor.close()
        db.close()
    }

    fun addDoc(doc:String, route:String, planning_date:String, material: String, round:String, license:String, delivery_date:String, location_code:String, location_type:String, destination_code:String, destination_type:String,tank:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("doc",doc)
        values.put("route",route)
        values.put("delivery_date",planning_date)
        values.put("material_list",material)
        values.put("round",round)
        values.put("license",license)
        values.put("shipping_date",delivery_date)
        values.put("location_code",location_code)
        values.put("location_type",location_type)
        values.put("destination_code",destination_code)
        values.put("destination_type",destination_type)
        values.put("tank_type", tank)

        val insert = db.insertWithOnConflict("delivery", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        println("OK")
        if (insert == -1L) {
            println("UPDATED")
            db.update("delivery",values,"doc=?", arrayOf(doc))
        }
        getDeliveryItems()
        db.close()
    }

    fun getDeliveryItems(){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) FROM delivery"
        val cursor = db.rawQuery(query,null)
        try{
            deliveryItems = if(cursor.moveToFirst()){
                cursor.getInt(0)
            } else{
                0
            }
        }
        catch(e:Exception){
            deliveryItems = 0
            e.printStackTrace()
        }
        cursor.close()
        db.close()
    }

    fun getSerial(doc:String){
        ViewDeleveryItems.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM serial WHERE doc = '$doc'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val doc = cursor.getString(0)
                val serial = cursor .getString(1)
                val data = ViewDeliveryModel(doc,serial)
                ViewDeleveryItems.add(data)
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun addSerial(doc:String, serial:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("doc",doc)
        values.put("serial",serial)
        db.insertWithOnConflict("serial",null,values,SQLiteDatabase.CONFLICT_IGNORE)
        ViewDeleveryItems.add(ViewDeliveryModel(doc,serial))
        db.close()
    }

    fun deleteSerial(doc:String, serial:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        db.delete("serial","doc='$doc' AND serial='$serial'",null)
        db.close()
    }

    fun getDeliveryExportItems(){
        DeliveryExportItems.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM delivery"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val header = "1"
                val doc = cursor.getString(0)
                val route = cursor.getString(1)
                val delivery_date = cursor.getString(2)
                val material = cursor.getString(3)
                val round = cursor.getString(4)
                val car = cursor.getString(5)
                val shipping_date = cursor.getString(6)
                val location_code = cursor.getString(8)
                val destination_type = cursor.getString(9)
                val destination_code = cursor.getString(10)
                val tank = cursor.getString(11)
                val data =DeliveryExportModel(header,doc,route,delivery_date,material,round,car,shipping_date,location_code,destination_type,destination_code,tank)
                DeliveryExportItems.add(data)
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun getDeliverySerialExport(){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        println("SIZE ${DeliveryExportItems.size}")
        for(i in 0..DeliveryExportItems.size-1){
            DeliverySerialItems.clear()
            val query = "SELECT serial FROM serial WHERE doc='${DeliveryExportItems[i].doc}'"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                val values = ContentValues()
                values.put("header","1")
                values.put("doc",DeliveryExportItems[i].doc)
                values.put("route",DeliveryExportItems[i].route)
                values.put("delivery_date",DeliveryExportItems[i].deliverydate)
                values.put("material_list",DeliveryExportItems[i].materiallist)
                values.put("round",DeliveryExportItems[i].round)
                values.put("car_code",DeliveryExportItems[i].carcode)
                values.put("shipping_date",DeliveryExportItems[i].shippingdate)
                values.put("location_code",DeliveryExportItems[i].locationcode)
                values.put("destination_type",DeliveryExportItems[i].destinationtype)
                values.put("destination_code",DeliveryExportItems[i].destinationcode)
                values.put("gas_tank", DeliveryExportItems[i].gastank)
                db.insertWithOnConflict("delivery_export", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                do{
                    DeliverySerialItems.add(cursor.getString(0))
                }
                while (cursor.moveToNext())
                for(j in 0..DeliverySerialItems.size-1){
                    val values1 = ContentValues()
                    values1.put("header","0")
                    values1.put("doc", DeliverySerialItems[j])
                    db.insertWithOnConflict("delivery_export", null, values1, SQLiteDatabase.CONFLICT_IGNORE)
                }
            }
            cursor.close()
        }
        db.close()
    }

    fun getCarCode(code: String){
        CarCodeList.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [license plate]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            CarCodeList.add(code)
            do{
                CarCodeList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
            CarCodeList.add(code)
        }
        cursor.close()
        db.close()
    }

    fun getMaterail(material:String){
        MaterialArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [material code]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            MaterialArray.add(material)
            do{
                MaterialArray.add(cursor.getString(0))
            }
            while(cursor.moveToNext())
        }
        else{
            MaterialArray.add(material)
        }
        cursor.close()
        db.close()
    }

    fun getManufacturer(master:String){
        MasterArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [manufacturer]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            MasterArray.add(master)
            do{
                MasterArray.add(cursor.getString(0))
            }
            while (cursor.moveToNext())
        }
        else{
            MasterArray.add(master)
        }
        cursor.close()
        db.close()
    }

    fun getMaintenanceCompany(code:String,name:String){
        MaintenanceArray.clear()
        MaintenanceList.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [maintenance company]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            MaintenanceArray.add(MaintenanceCompanyModel(code,name))
            MaintenanceList.add(MaintenanceCompanyModel(code,name))
            do{
                MaintenanceArray.add(MaintenanceCompanyModel(cursor.getString(0),cursor.getString(1)))
                MaintenanceList.add(MaintenanceCompanyModel(cursor.getString(0),cursor.getString(1)))
            }
            while (cursor.moveToNext())
        }
        else{
            MaintenanceArray.add(MaintenanceCompanyModel(code,name))
            MaintenanceList.add(MaintenanceCompanyModel(code,name))
        }
        cursor.close()
        db.close()
    }

    fun getFillingPlant(code:String,plant:String){
        FillingPlantArray.clear()
        FillingPlantList.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [filling plant]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            FillingPlantArray.add(FillingPlantModel(code,plant))
            FillingPlantList.add(FillingPlantModel(code,plant))
            do{
                FillingPlantArray.add(FillingPlantModel(cursor.getString(0),cursor.getString(1)))
                FillingPlantList.add(FillingPlantModel(cursor.getString(0),cursor.getString(1)))
            }
            while (cursor.moveToNext())
        }
        else{
            FillingPlantArray.add(FillingPlantModel(code,plant))
            FillingPlantList.add(FillingPlantModel(code,plant))
        }
        cursor.close()
        db.close()
    }

    fun getInspector(inspector:String){
        InspectorArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [inspector]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            InspectorArray.add(inspector)
            do{
                InspectorArray.add(cursor.getString(0))
            }
            while (cursor.moveToNext())
        }
        else{
            InspectorArray.add(inspector)
        }
        cursor.close()
        db.close()
    }

    fun getStoreType(storetype: String){
        StoreTypeArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [store type]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            StoreTypeArray.add(storetype)
            do{
                StoreTypeArray.add(cursor.getString(0))
            }
            while (cursor.moveToNext())
        }
        else{
            StoreTypeArray.add(storetype)
        }
        cursor.close()
        db.close()
    }

    fun getStoreName(){
        StoreNameArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [customer]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val code = cursor.getString(0)
                val name = cursor.getString(1)

                StoreNameArray.add(CustomerModel(code,name))
            }
            while (cursor.moveToNext())
        }
        else{
            StoreNameArray.clear()
        }
        cursor.close()
        db.close()
    }

    fun getStoreCode(storecode: String){
        StoreCodeArray.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM [store code]"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            StoreCodeArray.add(storecode)
            do{
                StoreCodeArray.add(cursor.getString(0))
            }
            while(cursor.moveToNext())
        }
        else{
            StoreCodeArray.add(storecode)
        }
        cursor.close()
        db.close()
    }

    fun getTotalDeliveryItems(){
        DeliveryItems.clear()
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        for(i in 0..DeliveryExportItems.size-1){
            val query = "SELECT count(*) FROM serial WHERE doc='${DeliveryExportItems[i].doc}'"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                val query1 = "SELECT destination_type,destination_code FROM delivery WHERE doc='${DeliveryExportItems[i].doc}'"
                val cursor1 = db.rawQuery(query1,null)
                if(cursor1.moveToFirst()){
                    DeliveryItems.add(ViewDeliveryItemsModel(DeliveryExportItems[i].doc,cursor.getString(0),cursor1.getString(0),cursor1.getString(1)))
                }
                else{
                    println("NO SDNSDUSDBDUBD")
                }
            }
            cursor.close()
        }
        db.close()
    }

    fun deleteRegisterQR(qr:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        db.execSQL("DELETE FROM planning WHERE qr='$qr'")
        db.close()
    }

    fun deleteDocument(doc:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        db.execSQL("DELETE FROM delivery WHERE doc='$doc'")
        db.execSQL("DELETE FROM serial WHERE doc='$doc'")
        db.close()
    }

    fun updateQR(qr:String, serial:String, material:String, manufacturer:String, manufacture_date:String, fa:String, storetype:String, storecode:String, inspector: String, inspection_date:String){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("serial",serial)
        values.put("material",material)
        values.put("manufacturer",manufacturer)
        values.put("date",manufacture_date)
        values.put("fa",fa)
        values.put("store_type",storetype)
        values.put("store_code",storecode)
        values.put("inspector",inspector)
        values.put("inspection_date",inspection_date)
        db.update("planning",values,"qr=?", arrayOf(qr))
        db.close()
    }

}