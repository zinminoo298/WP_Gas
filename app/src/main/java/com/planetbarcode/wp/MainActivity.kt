package com.planetbarcode.wp

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ajts.androidmads.library.SQLiteToExcel
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import org.apache.commons.io.FileUtils
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var viewRegister:ImageView
        lateinit var db:DatabaseHandler
        lateinit var textViewItems: TextView
        lateinit var textViewDeliveryItems: TextView
        lateinit var imageViewExport: ImageView
        lateinit var imageViewDeliveryExport: ImageView
        lateinit var linearLayoutViewItems: LinearLayout
        lateinit var linearLayoutViewDeliveryItems : LinearLayout
        lateinit var imageViewPlanning: ImageView
        internal lateinit var dialog: AlertDialog
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var textViewMissingFiles: TextView
        var currentDate = ""
        var currentTime = ""
        var STORAGE_PERMISSION_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewRegister = findViewById(R.id.view_register)
        imageViewPlanning = findViewById(R.id.view_planning)
        textViewItems = findViewById(R.id.txt_register_items)
        textViewDeliveryItems = findViewById(R.id.txt_delivery_items)
        imageViewExport = findViewById(R.id.img_export)
        imageViewDeliveryExport = findViewById(R.id.img_delivery_export)
        linearLayoutViewItems = findViewById(R.id.view_items)
        linearLayoutViewDeliveryItems = findViewById(R.id.view_delivery_items)
        db = DatabaseHandler(this)
        db.openDatabase()
        db.getRegisteredItems()
        db.getDeliveryItems()

        textViewItems.text = DatabaseHandler.registeredItems.toString()
        textViewDeliveryItems.text = DatabaseHandler.deliveryItems.toString()

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }

        imageViewPlanning.setOnClickListener {
            val intent = Intent(this, DeliverySetup::class.java)
            startActivity(intent)
//            Toast.makeText(this,"Planning Phase Under Development",Toast.LENGTH_SHORT).show()
        }

        viewRegister.setOnClickListener{
            val intent = Intent(this, RegisterSetup::class.java)
            startActivity(intent)
        }

        imageViewExport.setOnClickListener {
            db.getRegisteredItems()
            if(DatabaseHandler.registeredItems == 0){
                com.planetbarcode.wp.AlertDialog(this,layoutInflater,"Warning","No Data to Export").errorDialog()
            }
            else{
                PlanningExportDialog()
            }
        }

        imageViewDeliveryExport.setOnClickListener{
            db.getDeliveryItems()
            if(DatabaseHandler.deliveryItems == 0){
                com.planetbarcode.wp.AlertDialog(this,layoutInflater,"Warning","No Data to Export").errorDialog()
            }
            else{
                DeliveryExportDialog()
            }
        }

        linearLayoutViewItems.setOnClickListener{
            AsyncLoadRegisteredItems(this).execute()
        }

        linearLayoutViewDeliveryItems.setOnClickListener{
            AsyncLoadDeliveyItems(this).execute()
        }
    }

    fun DeliveryExportDialog(){
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.confrim_export, null)
        builder.setView(view)
        dialog =builder.create()
        dialog.show()
        dialog.setCancelable(false)
        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener {
            dialog.dismiss()
            AsyncExport(this,layoutInflater).execute()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun PlanningExportDialog(){
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.confrim_export, null)
        builder.setView(view)
        dialog =builder.create()
        dialog.show()
        dialog.setCancelable(false)
        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener {
            dialog.dismiss()
            AsyncExportRegister(this,layoutInflater).execute()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )){
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }

        else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_import) {
            AsyncImport(this, this.layoutInflater).execute()
        }
        super.onOptionsItemSelected(item)
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private class AsyncImport(val context: Context, val inflater: LayoutInflater):AsyncTask<String, String, String>(){
        lateinit var pgd:ProgressDialog
        lateinit var db:DatabaseHandler
        var fileList = ArrayList<String>()
        var checkFileList = ArrayList<String>()
        override fun doInBackground(vararg params: String?): String {
            import()
            return "ok"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setTitle("Import Master")
            pgd.setMessage("Loading")
            pgd.show()
            pgd.setCancelable(false)
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            if(checkFileList.size!=0){
                WarningDialog()
            }
            else{
                com.planetbarcode.wp.AlertDialog(context,inflater,"Import Status", "Import Completed").errorDialog()
                Toast.makeText(context, "Import Completed", Toast.LENGTH_SHORT).show()
            }
            super.onPostExecute(result)
        }

        fun WarningDialog(){
            val builder= AlertDialog.Builder(context)
            val view=inflater.inflate(R.layout.missing_file_warning, null)
            builder.setView(view)
            dialog =builder.create()
            dialog.setCancelable(false)
            buttonYes = view.findViewById(R.id.btn_yes)
            textViewMissingFiles = view.findViewById(R.id.txt_missing_files)
            for(i in 0 until checkFileList.size){
                textViewMissingFiles.setText("${checkFileList[i]}.txt \n")
            }
            dialog.show()

            buttonYes.setOnClickListener {
                dialog.dismiss()
            }

        }

        fun import(){
            fileList.clear()
            fileList.add("material code")
            fileList.add("license plate")
            fileList.add("manufacturer")
            fileList.add("customer")
            fileList.add("inspector")
            fileList.add("maintenance company")
            fileList.add("filling plant")
            checkFileList = fileList

            val path = "/sdcard/Download"
            val f = File(path)
            if(f.exists()){
                println("FILE EXIStS")
                var masterFiles = ArrayList<String>()
                var files = f.listFiles()
                for(file in files){
                    if(file.isFile){
                        println("file")
                        masterFiles.add(file.toString())
                    }
                    else{
                        println("dir")

                    }
                }
                println(masterFiles.size)
                if(masterFiles.isNotEmpty()){
                    val db = context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                    db.execSQL("DELETE FROM [material code]")
                    db.execSQL("DELETE FROM [inspector]")
                    db.execSQL("DELETE FROM [license plate]")
                    db.execSQL("DELETE FROM [manufacturer]")
                    db.execSQL("DELETE FROM [customer]")
                    db.execSQL("DELETE FROM [maintenance company]")
                    db.execSQL("DELETE FROM [filling plant]")
                    for(element in masterFiles){
                        val cursor = context.contentResolver.openInputStream(File(element).toUri())
                        try{
                            val file = InputStreamReader(cursor)
                            var buffer = BufferedReader(file)
                            var path = element.toString()
                            val contentValues = ContentValues()
                            path  = path.substring(path.lastIndexOf("/") + 1)
                            if (path.indexOf(".") > 0){
                                path = path.substring(0, path.lastIndexOf("."))
                            }
                            for(i in 0 until fileList.size){
                                if(path == fileList[i]){
                                    checkFileList.removeAt(i)
                                    println("CHECK LIST" + checkFileList.size)
                                    while (true){
                                        val line = buffer.readLine()
                                        if(line == null) break

                                        when (path) {
                                            "customer" -> {
                                                val splitted = line.split(",").toTypedArray()
                                                contentValues.put("code", splitted[0])
                                                contentValues.put("customer", splitted[1])
                                                try {
                                                    db.insert("[$path]", null, contentValues)
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            "maintenance company" -> {
                                                val splitted = line.split(",").toTypedArray()
                                                contentValues.put("code", splitted[0])
                                                contentValues.put("[maintenance company]", splitted[1])
                                                try {
                                                    db.insert("[$path]", null, contentValues)
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            "filling plant" -> {
                                                val splitted = line.split(",").toTypedArray()
                                                contentValues.put("code", splitted[0])
                                                contentValues.put("[filling plant]", splitted[1])
                                                try {
                                                    db.insert("[$path]", null, contentValues)
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            else -> {
                                                contentValues.put("[$path]", line)
                                                try{
                                                    db.insert("[$path]", null, contentValues)
                                                }
                                                catch (e: IOException){
                                                    e.printStackTrace()
                                                }
                                            }

                                        }

                                    }
                                }
                            }
                        }catch (e: Exception){

                        }
                    }
                }
            }
            else{
                println("FILE NOT EXISTS")
            }

        }


    }

    private class AsyncLoadRegisteredItems(val context: Context?) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db: DatabaseHandler

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context!!)
            db.getViewRegisterItems()
            db.getRegisteredItems()
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Loading Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            val intent = Intent(context, ViewRegisteredItems::class.java)
            context!!.startActivity(intent)
            super.onPostExecute(result)
        }
    }

    private class AsyncLoadDeliveyItems(val context: Context?) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db: DatabaseHandler

        override fun doInBackground(vararg params: String?):String{
            db = DatabaseHandler(context!!)
            db.getDeliveryExportItems()
            db.getTotalDeliveryItems()
            return "OK"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Loading Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            val intent = Intent(context, ViewDeliveryItems::class.java)
            context!!.startActivity(intent)
            super.onPostExecute(result)
        }
    }


    private class AsyncExport(val context: Context, val inflater: LayoutInflater) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db: DatabaseHandler
        var currentdate = ""
        var currentTime = ""

        override fun doInBackground(vararg params: String?): String {
            val db1 = context!!.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            db1.execSQL("DELETE FROM delivery_export")
            db1.close()
            db = DatabaseHandler(context)
            db.getDeliveryExportItems()
            db.getDeliverySerialExport()

            testXLS("/WP/Export")
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Exporting Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            textViewDeliveryItems.text = "0"
            com.planetbarcode.wp.AlertDialog(context,inflater,"Export Status", "Export Completed").errorDialog()
            super.onPostExecute(result)
        }

        fun clearData(){
            val db = context!!.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            db.execSQL("DELETE FROM delivery")
            db.execSQL("DELETE FROM serial")
            db.execSQL("DELETE FROM delivery_export")
            db.execSQL("VACUUM")
            db.close()
        }

        fun testXLS(path: String){
            deleteFile()
            val file = File("/sdcard/Download/Delivery.xls")
            if(!file.exists()){
                val fw= FileWriter(File("/sdcard/Download/Delivery.xls"))
            }
            val sqliteToExcel = SQLiteToExcel(
                    context.applicationContext,
                    "database.db",
                    "/sdcard/Download/"
            )
            val tableList = ArrayList<String>()
            tableList.add("delivery_export")
            try{
                sqliteToExcel.exportSpecificTables(tableList, "Delivery.xls", object : SQLiteToExcel.ExportListener {
                    override fun onStart() {}
                    override fun onCompleted(filePath: String) {
                        val filePath = "/sdcard/Download/Delivery.xls"
                        val wb = HSSFWorkbook(FileInputStream(filePath))
                        val sheet = wb.getSheetAt(0)
                        removeRow(sheet,0)
                        deleteFile()
                        val outFilePath = File("/sdcard/Download/Delivery.xls")
                        val writeFile = FileWriter(outFilePath)
                        val out = FileOutputStream(outFilePath)
                        wb.write(out)
                        out.flush()
                        out.close()

                        try{
                            db.getDoc()
                            getDate()
                            if(DatabaseHandler.deliveryItems != 0){
                                do{
                                    copyFile(File("/sdcard/Download/Delivery.xls"), File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-delivery.xls"))
                                }while(File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-delivery.xls").length().toString() == "0")
                            }
                            else{
                                copyFile(File("/sdcard/Download/Delivery.xls"), File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-delivery.xls"))
                            }
                            clearData()
                        }
                        catch (e: Exception){
                            e.printStackTrace()
                        }
                    }

                    override fun onError(e: Exception) {}
                })
            }
            catch (e: java.lang.Exception){
                println(e)
            }

        }

        fun removeRow(sheet: HSSFSheet, rowIndex: Int) {
            val lastRowNum = sheet.lastRowNum
            println(lastRowNum)
            if (rowIndex in 0 until lastRowNum) {
                sheet.shiftRows(rowIndex + 1, lastRowNum, -1)
            }
            if (rowIndex == lastRowNum) {
                val removingRow = sheet.getRow(rowIndex)
                try{
                    if (removingRow != null) {
                        sheet.removeRow(removingRow)
                        println("OKOK")
                    }
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }

        fun  deleteFile(){
            try{
                val path = "/sdcard/Download/Delivery.xls"

                val root = Environment.getExternalStorageDirectory().toString()
                val file = File("/sdcard/Download/Delivery.xls")
                if(file.exists()){
                    println("EXists")
                    file.delete()
                    if(file.exists()){
                        println("File stil exists")
                    }
                }
                else{
                    println("Not Exists")
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }

        fun copyFile(sourceFile: File?, destFile: File) {
            if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()

            try {
                FileUtils.copyFile(sourceFile, destFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            println(destFile.length())
        }
        fun getDate(){
            val sdf = SimpleDateFormat("hh:mma")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val t = sdf.format(Date())

            val mth =  month + 1
            val date = "" + day + "/" + mth + "/" + year
            val date1 = "" + day + "/" + mth + "/" + year+"/"+t

            val date_format = SimpleDateFormat("yyyy-mm-dd")
            val time_format = SimpleDateFormat("HH:mm")
            val curFormater = SimpleDateFormat("dd/mm/yyyy")
            val curFormater1 = SimpleDateFormat("dd/MM/yyyy/hh:mma")

            val dateObj = curFormater.parse(date)
            val dateObj1 = curFormater1.parse(date1)

            currentDate = date_format.format(dateObj)
            currentTime = time_format.format(dateObj1)
        }

    }

    private class AsyncExportRegister(val context: Context?, val inflater: LayoutInflater) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {
            testXLS("/WP/Export")
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Clearing Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()

            textViewItems.text = "0"
//            Toast.makeText(context, "Data Cleared", Toast.LENGTH_SHORT).show()
            com.planetbarcode.wp.AlertDialog(context!!,inflater,"Export Status", "Export Completed").errorDialog()
            super.onPostExecute(result)
        }

        fun testXLS(path: String){
            deleteFile()
            val file = File("/sdcard/Download/Register.xls")
            if(!file.exists()){
                val fw= FileWriter(File("/sdcard/Download/Register.xls"))
            }
            val sqliteToExcel = SQLiteToExcel(
                    context!!.applicationContext,
                    "database.db",
                    "/sdcard/Download"
            )
            val tableList = ArrayList<String>()
            tableList.add("planning")

            try{
                sqliteToExcel.exportSpecificTables(tableList, "Register.xls", object :
                        SQLiteToExcel.ExportListener {
                    override fun onStart() {}
                    override fun onCompleted(filePath: String) {
                        val filePath = "/sdcard/Download/Register.xls"
                        val wb = HSSFWorkbook(FileInputStream(filePath))
                        val sheet = wb.getSheetAt(0)
                        removeRow(sheet,0)
                        deleteFile()
                        val outFilePath = File("/sdcard/Download/Register.xls")
                        val writeFile = FileWriter(outFilePath)
                        val out = FileOutputStream(outFilePath)
                        wb.write(out)
                        out.flush()
                        out.close()
                        try{
                            db.getRegisteredItems()
                            getDate()
                            if(DatabaseHandler.registeredItems != 0){
                                do{
                                    copyFile(File("/sdcard/Download/Register.xls"), File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-register.xls"))
                                }while(File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-register.xls").length().toString() == "0")
                            }
                            else{
                                copyFile(File("/sdcard/Download/Register.xls"), File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path}/${currentDate}-${currentTime}-register.xls"))
                            }
                        }
                        catch (e: Exception){
                            e.printStackTrace()
                        }
                        val db = context!!.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                        db.execSQL("DELETE FROM planning")
                        db.close()
                    }

                    override fun onError(e: Exception) {}
                })
            }
            catch (e: java.lang.Exception){
                println(e)
            }

        }

        fun removeRow(sheet: HSSFSheet, rowIndex: Int) {
            val lastRowNum = sheet.lastRowNum
            println(lastRowNum)
            if (rowIndex in 0 until lastRowNum) {
                sheet.shiftRows(rowIndex + 1, lastRowNum, -1)
            }
            if (rowIndex == lastRowNum) {
                val removingRow = sheet.getRow(rowIndex)
                try{
                    if (removingRow != null) {
                        sheet.removeRow(removingRow)
                        println("OKOK")
                    }
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }

        fun  deleteFile(){
            try{
                val path = "/sdcard/Download/Register.xls"

                val root = Environment.getExternalStorageDirectory().toString()
                val file = File("/sdcard/Download/Register.xls")
                if(file.exists()){
                    println("EXists")
                    file.delete()
                    if(file.exists()){
                        println("File stil exists")
                    }
                }
                else{
                    println("Not Exists")
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }

        fun copyFile(sourceFile: File?, destFile: File) {
            if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()

            try {
                FileUtils.copyFile(sourceFile, destFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            println(destFile.length())
        }

        fun getDate(){
            val sdf = SimpleDateFormat("hh:mma")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val t = sdf.format(Date())

            val mth =  month + 1
            val date = "" + day + "/" + mth + "/" + year
            val date1 = "" + day + "/" + mth + "/" + year+"/"+t

            val date_format = SimpleDateFormat("yyyy-mm-dd")
            val time_format = SimpleDateFormat("HH:mm")
            val curFormater = SimpleDateFormat("dd/mm/yyyy")
            val curFormater1 = SimpleDateFormat("dd/MM/yyyy/hh:mma")

            val dateObj = curFormater.parse(date)
            val dateObj1 = curFormater1.parse(date1)

            currentDate = date_format.format(dateObj)
            currentTime = time_format.format(dateObj1)
        }
    }

    override fun onResume() {
        super.onResume()
        textViewItems.text = DatabaseHandler.registeredItems.toString()
        textViewDeliveryItems.text = DatabaseHandler.deliveryItems.toString()
    }
}