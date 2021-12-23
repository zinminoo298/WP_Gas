package com.planetbarcode.wp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.ajts.androidmads.library.SQLiteToExcel
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.MainActivity.Companion.textViewDeliveryItems
import org.apache.commons.io.FileUtils
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DeliverySetup : AppCompatActivity() {
    companion object{
        internal lateinit var editTextUser: EditText
        internal lateinit var textViewDate: TextView
        internal lateinit var imageViewView: ConstraintLayout
        internal lateinit var imageViewExport: ConstraintLayout
        internal lateinit var imageViewClear: ConstraintLayout
        internal lateinit var buttonNext: Button
        internal lateinit var dialog: AlertDialog
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        var currentDate = ""
        var inspector = ""
        lateinit var db:DatabaseHandler
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_setup)

        editTextUser = findViewById(R.id.edt_user)
        textViewDate = findViewById(R.id.txt_date)
        buttonNext = findViewById(R.id.btn_next_register)
        imageViewClear = findViewById(R.id.img_clear)
        imageViewExport = findViewById(R.id.img_export)
        imageViewView = findViewById(R.id.img_view)
        db  = DatabaseHandler(this)

        getDate()

        buttonNext.setOnClickListener {
            if(editTextUser.text.toString() == "" || editTextUser.text.toString() == null){
                Toast.makeText(this, "Please enter user", Toast.LENGTH_SHORT).show()
            }else{
                db.getCarCode("Select Vehicle Code")
                db.getFillingPlant("Select Filling Plant","-")
                db.getMaintenanceCompany("Select Maintenance Code","-")
                db.getStoreName()
                db.getMaterail("Select Material Code")
                val intent = Intent(this, DeliveryTest::class.java)
                startActivity(intent)
                inspector = editTextUser.text.toString()
            }
        }

        textViewDate.setOnClickListener {
            datePicker()
        }

        imageViewExport.setOnClickListener {
            db.getDeliveryItems()
            if(DatabaseHandler.deliveryItems == 0){
                com.planetbarcode.wp.AlertDialog(this,layoutInflater,"Warning","No Data to Export").errorDialog()
            }
            else{
                ExportDialog()
            }
        }

        imageViewClear.setOnClickListener {
            ClearDialog()
        }

        imageViewView.setOnClickListener {
            AsyncLoadDeliveyItems(this).execute()
        }

    }

    fun ExportDialog(){
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

    fun ClearDialog(){
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.clear_data, null)
        builder.setView(view)
        dialog =builder.create()
        dialog.show()
        dialog.setCancelable(false)
        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener {
            dialog.dismiss()
            AsyncClearData(this).execute()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun getDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val mth =  month + 1
        val date = "" + day + "/" + mth + "/" + year

        val date_format = SimpleDateFormat("yyyy-mm-dd")
        val curFormater = SimpleDateFormat("dd/mm/yyyy")
        val dateObj = curFormater.parse(date)

        currentDate = date_format.format(dateObj)
        textViewDate.text = currentDate
    }

    fun datePicker(){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val mth = monthOfYear + 1
                val date = "" + dayOfMonth + "/" + mth + "/" + year

                val date_format = SimpleDateFormat("yyyy-mm-dd")
                val curFormater = SimpleDateFormat("dd/mm/yyyy")
                val dateObj = curFormater.parse(date)

                currentDate = date_format.format(dateObj)
                textViewDate.text = currentDate

            },
            year,
            month,
            day
        )
        dpd.show()
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

    private class AsyncExport(val context: Context,val inflater: LayoutInflater) : AsyncTask<String, String, String>() {
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

    private class AsyncClearData(val context: Context?) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {
            val db = context!!.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            db.execSQL("DELETE FROM delivery")
            db.execSQL("DELETE FROM serial")
            db.execSQL("DELETE FROM delivery_export")
            db.execSQL("VACUUM")
            db.close()
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
            Toast.makeText(context, "Data Cleared", Toast.LENGTH_SHORT).show()
            super.onPostExecute(result)
        }
    }

    private class AsyncTaskRunner(val context: Context) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db:DatabaseHandler

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context)
            db.getDeliveryItems()

            return DatabaseHandler.deliveryItems.toString()
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setTitle("Loading")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            val myIntent = Intent(context, MainActivity::class.java)
            myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(myIntent)
            super.onPostExecute(result)
        }
    }

    override fun onBackPressed() {
        AsyncTaskRunner(this).execute()
//        this.finish()
        super.onBackPressed()
    }
}