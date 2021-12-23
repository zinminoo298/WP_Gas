package com.planetbarcode.wp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.planetbarcode.wp.Adapter.ViewRegisterAdapter
import com.planetbarcode.wp.Adapter.ViewRegisterAdapter.Companion.rotatedBitmap
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditRegisterItem : AppCompatActivity() {
    companion object{
        lateinit var textViewCalendar: TextView
        lateinit var textViewQr: TextView
        lateinit var textViewInspectionDate: TextView
        lateinit var textViewStoreName: TextView
        lateinit var editTextSerial: EditText
        lateinit var editTextFa: EditText
        lateinit var editTextStoreCode: EditText
        lateinit var spinnerMaterial: Spinner
        lateinit var spinnerMaster: Spinner
        lateinit var spinnerStoreCode: Spinner
        lateinit var spinnerStoreType: Spinner
        lateinit var spinnerInspector: Spinner
        lateinit var imageViewCamera: ImageView
        lateinit var imageViewInspectionCalendar: ImageView
        lateinit var imageViewCalendar: ImageView
        lateinit var buttonSave: Button
        lateinit var cardView : CardView
        lateinit var linearLayoutStoreCode: LinearLayout
        lateinit var linearLayoutStoreCode1: LinearLayout
        lateinit var linearLayoutStoreName: LinearLayout
        lateinit var arrayAdapter3:ArrayAdapter<String>
        var currentPhotoPath: String? = null
        const val CAMERA_PERM_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
        var currentDate = ""
        internal lateinit var db: DatabaseHandler
        var StoreTypeList = ArrayList<String>()

        var sampleCompany = ArrayList<String>()
        var samplePlant = ArrayList<String>()
        var sampleCustomer = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_register_item)

        textViewCalendar = findViewById(R.id.txt_calendar)
        textViewQr = findViewById(R.id.txt_qr)
        textViewInspectionDate = findViewById(R.id.txt_inspection_date)
        textViewStoreName = findViewById(R.id.txt_store_name)
        editTextSerial = findViewById(R.id.edt_serial)
        editTextFa = findViewById(R.id.edt_fa)
        editTextStoreCode = findViewById(R.id.edt_store_code)
        spinnerMaster = findViewById(R.id.spinner_master)
        spinnerMaterial = findViewById(R.id.spinner_material)
        spinnerStoreCode = findViewById(R.id.spinner_store_code)
        spinnerStoreType = findViewById(R.id.spinner_store_type)
        spinnerInspector = findViewById(R.id.spinner_inspector)
        imageViewCalendar = findViewById(R.id.img_calendar)
        imageViewInspectionCalendar = findViewById(R.id.img_inspection_calendar)
        imageViewCamera = findViewById(R.id.img_camera)
        buttonSave = findViewById(R.id.btn_save_register)
        linearLayoutStoreCode1 = findViewById(R.id.layout_store_code1)
        linearLayoutStoreName = findViewById(R.id.layout_store_name)
        linearLayoutStoreCode = findViewById(R.id.layout_store_code)
        cardView = findViewById(R.id.con_layout)
        db = DatabaseHandler(this)

        StoreTypeList.clear()
        StoreTypeList.add("A-โรงซ่อม")
        StoreTypeList.add("B-โรงบรรจุ")
        StoreTypeList.add("C-ลูกค้า")
        var storenameList = DatabaseHandler.StoreNameArray


        textViewQr.text = ViewRegisterAdapter.editQRArray[0]
        editTextSerial.setText("${ViewRegisterAdapter.editQRArray[1]}")
        editTextFa.setText("${ViewRegisterAdapter.editQRArray[9]}")
        textViewCalendar.text = ViewRegisterAdapter.editQRArray[6]
        textViewInspectionDate.text = ViewRegisterAdapter.editQRArray[8]
        editTextStoreCode.setText(ViewRegisterAdapter.editQRArray[3])


        if(rotatedBitmap != null){
            imageViewCamera.setImageBitmap(rotatedBitmap)
        }
        else{
            Toast.makeText(this, "Image File Not Found", Toast.LENGTH_SHORT).show()
        }

        DatabaseHandler.MaterialArray.removeAt(0)
        DatabaseHandler.MasterArray.removeAt(0)
        DatabaseHandler.MaintenanceArray.removeAt(0)
        DatabaseHandler.MaintenanceList.removeAt(0)
        DatabaseHandler.FillingPlantArray.removeAt(0)
        DatabaseHandler.FillingPlantList.removeAt(0)
        DatabaseHandler.InspectorArray.removeAt(0)


        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.MaterialArray)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaterial.adapter = arrayAdapter
        val spinnerMaterialPosition = arrayAdapter.getPosition(ViewRegisterAdapter.editQRArray[4])
        spinnerMaterial .setSelection(spinnerMaterialPosition)


        val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.MasterArray)
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaster.adapter = arrayAdapter1
        val spinnerMasterPosition = arrayAdapter1.getPosition(ViewRegisterAdapter.editQRArray[5])
        spinnerMaster.setSelection(spinnerMasterPosition)

        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StoreTypeList)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStoreType.adapter = arrayAdapter2
        val spinnerStoreTypePosition = arrayAdapter2.getPosition(ViewRegisterAdapter.editQRArray[2])
        spinnerStoreType .setSelection(spinnerStoreTypePosition)

        println("TYPE"+ViewRegisterAdapter.editQRArray[2])

        linearLayoutStoreCode.visibility = View.VISIBLE
        linearLayoutStoreCode1.visibility = View.GONE

        if(ViewRegisterAdapter.editQRArray[2]=="C-ลูกค้า"){
            arrayAdapter3 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    sampleCompany)//Just initializing
            editTextStoreCode.setText(ViewRegisterAdapter.editQRArray[3])
            textViewStoreName.text = ViewRegisterAdapter.storeName
            linearLayoutStoreCode.visibility = View.GONE
            linearLayoutStoreCode1.visibility = View.VISIBLE
        }

        val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.InspectorArray)
        arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInspector.adapter = arrayAdapter4
        val spinnerInspectorPosition = arrayAdapter4.getPosition(ViewRegisterAdapter.editQRArray[7])
        spinnerInspector.setSelection(spinnerInspectorPosition)

        spinnerStoreType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
                if(spinnerStoreType.selectedItem == "A-โรงซ่อม"){
                    try{
                        arrayAdapter3.clear()
                    }
                    catch(e:Exception){
                        e.printStackTrace()
                    }
                    for(items in 0 until DatabaseHandler.MaintenanceArray.size){
                        arrayAdapter3.add(DatabaseHandler.MaintenanceArray[items].code)
                    }
                    arrayAdapter3.notifyDataSetChanged()
                    arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerStoreCode.adapter = arrayAdapter3
                    var spinnerStoreCodePosition = 0
                    try{
                        spinnerStoreCodePosition = arrayAdapter3.getPosition(ViewRegisterAdapter.editQRArray[3])
                        if(spinnerStoreCodePosition == -1){
                            spinnerStoreCodePosition = 0
                        }
                    }
                    catch (e:Exception){
                        spinnerStoreCodePosition = 0
                        e.printStackTrace()
                    }
                    println(spinnerStoreCodePosition)
                    spinnerStoreCode .setSelection(spinnerStoreCodePosition)
                    textViewStoreName.text = DatabaseHandler.MaintenanceArray[spinnerStoreCodePosition].maintenance_company

                    linearLayoutStoreCode.visibility = View.VISIBLE
                    linearLayoutStoreCode1.visibility = View.GONE
                }

                if(spinnerStoreType.selectedItem == "B-โรงบรรจุ"){
                    try{
                        arrayAdapter3.clear()
                    }
                    catch(e:Exception){
                        e.printStackTrace()
                    }
                    for(items in 0 until DatabaseHandler.FillingPlantArray.size){
                        arrayAdapter3.add(DatabaseHandler.FillingPlantArray[items].code)
                    }
                    arrayAdapter3.notifyDataSetChanged()
                    arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerStoreCode.adapter = arrayAdapter3
                    var spinnerStoreCodePosition = 0
                    try{
                        spinnerStoreCodePosition = arrayAdapter3.getPosition(ViewRegisterAdapter.editQRArray[3])
                        if(spinnerStoreCodePosition == -1){
                            spinnerStoreCodePosition = 0
                        }
                    }
                    catch (e:Exception){
                        spinnerStoreCodePosition = 0
                        e.printStackTrace()
                    }
                    spinnerStoreCode .setSelection(spinnerStoreCodePosition)
                    textViewStoreName.text = DatabaseHandler.FillingPlantArray[spinnerStoreCodePosition].filling_plant

                    linearLayoutStoreCode.visibility = View.VISIBLE
                    linearLayoutStoreCode1.visibility = View.GONE
                }

                if(spinnerStoreType.selectedItem == "C-ลูกค้า"){
                    linearLayoutStoreCode.visibility = View.GONE
                    linearLayoutStoreCode1.visibility = View.VISIBLE
                    if(ViewRegisterAdapter.editQRArray[2] == "C-ลูกค้า"){
                        editTextStoreCode.setText(ViewRegisterAdapter.editQRArray[3])
                        textViewStoreName.text = ViewRegisterAdapter.storeName
                    }
                    else{
                        editTextStoreCode.setText("")
                        textViewStoreName.text = "Not Found"
                    }

                }
            }
        }

        spinnerStoreCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
            ) {
                var index = spinnerStoreCode.selectedItemId.toInt()
                if(spinnerStoreType.selectedItem == "A-โรงซ่อม"){
                    textViewStoreName.text = DatabaseHandler.MaintenanceArray[index].maintenance_company
                }

                if(spinnerStoreType.selectedItem == "B-โรงบรรจุ"){
                    textViewStoreName.text = DatabaseHandler.FillingPlantArray[index].filling_plant
                }
            }
        }

        editTextStoreCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Thread {
                    try {
                        var storeName = ""
                        if (s != "") {
                            for (item in storenameList) {
                                if (item.code == "$s") {
                                    storeName = item.customer!!
                                    break
                                }
                                else{
                                    storeName = "Not found"
                                }
                            }
                            textViewStoreName.text = storeName
                        } else {
                            textViewStoreName.text = "Not found"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        })

        cardView.setOnClickListener {
            askCameraPermissions()
        }

        imageViewCalendar.setOnClickListener {
            datePicker(textViewCalendar)
        }

        imageViewInspectionCalendar.setOnClickListener {
            datePicker(textViewInspectionDate)
        }

        buttonSave.setOnClickListener {
            if(editTextFa.text.toString() == "" || editTextSerial.text.toString() == ""){
                Toast.makeText(this, "Please enter the required data", Toast.LENGTH_SHORT).show()
            }
            else {
                when {
                    editTextStoreCode.text.toString() == "" && spinnerStoreType.selectedItem.toString() == "C-ลูกค้า" -> {
                        Toast.makeText(this, "Please Enter Store Code", Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        val lowerStoreType = spinnerStoreType.selectedItem.toString().toLowerCase().take(1)
                        if (spinnerStoreType.selectedItem == "C-ลูกค้า") {
                            if (editTextStoreCode.text.toString() == "") {
                                Toast.makeText(this, "Please Enter Store Code", Toast.LENGTH_SHORT).show()
                            } else {
                                db.updateQR(
                                        textViewQr.text.toString(),
                                        editTextSerial.text.toString(),
                                        spinnerMaterial.selectedItem.toString(),
                                        spinnerMaster.selectedItem.toString(),
                                        textViewCalendar.text.toString(),
                                        editTextFa.text.toString(),
                                        lowerStoreType,
                                        editTextStoreCode.text.toString(),
                                        spinnerInspector.selectedItem.toString(),
                                        textViewInspectionDate.text.toString()
                                )
                            }
                        } else {
                            db.updateQR(
                                    textViewQr.text.toString(),
                                    editTextSerial.text.toString(),
                                    spinnerMaterial.selectedItem.toString(),
                                    spinnerMaster.selectedItem.toString(),
                                    textViewCalendar.text.toString(),
                                    editTextFa.text.toString(),
                                    lowerStoreType,
                                    spinnerStoreCode.selectedItem.toString(),
                                    spinnerInspector.selectedItem.toString(),
                                    textViewInspectionDate.text.toString()
                            )
                        }
                        val path = "/WP/Pictures"
                        if (currentPhotoPath == null) {
                            //DO Nothing
                        } else {
                            copyFile(
                                    File(currentPhotoPath), File(
                                    "${
                                        Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_DOWNLOADS
                                        ).path + path
                                    }/${textViewQr.text.toString()}.jpg"
                            )
                            )
                            File(currentPhotoPath).delete()
                        }

                    }
                }

            }
            Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()

        }

    }

    private fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERM_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(
                        this,
                        "Camera Permission is Required to Use camera.",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        System.out.println("OK")
        if (requestCode == Register.CAMERA_REQUEST_CODE) {
            System.out.println("OK")
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("OK")
                val f = File(currentPhotoPath)
                imageViewCamera!!.setImageURI(Uri.fromFile(f))
//                copyFile(File(currentPhotoPath),File("/sdcard/Pictures/photo.jpg"))
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f))
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                this.sendBroadcast(mediaScanIntent)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            System.out.println("C OK")
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            }catch (e: Exception){
                println(e)
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.planetbarcode.wp.fileprovider",
                        photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                        takePictureIntent,
                        CAMERA_REQUEST_CODE
                )
            }
        }
        else{
            println("NOT OK")
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val storageDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        if(destFile.exists()){
            destFile.delete()
            try {
                source = FileInputStream(sourceFile).getChannel()
                destination = FileOutputStream(destFile).getChannel()
                destination.transferFrom(source, 0, source.size())
            } finally {
                if (source != null) {
                    source.close()
                }
                if (destination != null) {
                    destination.close()
                }
            }
        }
        else{
            try {
                source = FileInputStream(sourceFile).getChannel()
                destination = FileOutputStream(destFile).getChannel()
                destination.transferFrom(source, 0, source.size())
            } finally {
                if (source != null) {
                    source.close()
                }
                if (destination != null) {
                    destination.close()
                }
            }
        }
    }

    fun datePicker(textView: TextView){

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
                    textView.text = currentDate

                },
                year,
                month,
                day
        )
        dpd.show()
    }

    override fun onBackPressed() {
        AsyncLoadRegisteredItems(this).execute()
        super.onBackPressed()

    }

    private class AsyncLoadRegisteredItems(val context: Context?) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db: DatabaseHandler

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context!!)
            db.getViewRegisterItems()
            db.getRegisteredItems()
            println("loading")
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
            super.onPostExecute(result)
        }
    }

}