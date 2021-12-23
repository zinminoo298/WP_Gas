package com.planetbarcode.wp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Register : AppCompatActivity() {

    companion object{
        lateinit var textViewSavedItem: TextView
        lateinit var textViewCalendar: TextView
        lateinit var textViewInspectionDate: TextView
        lateinit var textViewItems: TextView
        lateinit var textViewStoreName: TextView
        lateinit var editTextQr: EditText
        lateinit var editTextSerial: EditText
        lateinit var editTextFa: EditText
        lateinit var editTextStoreCode: EditText
        lateinit var spinnerMaterial: Spinner
        lateinit var spinnerMaster: Spinner
        lateinit var spinnerStoreCode: Spinner
        lateinit var spinnerStoreType: Spinner
        lateinit var spinnerInspector: Spinner
        lateinit var imageViewCamera: ImageView
        lateinit var imageViewCalendar: ImageView
        lateinit var imageViewInspectionCalendar: ImageView
        lateinit var buttonSave: Button
        lateinit var cardView : CardView
        lateinit var linearLayoutStoreCode: LinearLayout
        lateinit var linearLayoutStoreCode1: LinearLayout
        lateinit var linearLayoutStoreName: LinearLayout
        lateinit var linearLayout1: LinearLayout
        lateinit var linearLayout2: LinearLayout
        lateinit var linearLayout3: LinearLayout
        lateinit var linearLayout4: LinearLayout
        lateinit var linearLayout5: LinearLayout
        lateinit var linearLayout6: LinearLayout
        lateinit var linearLayout7: LinearLayout
        lateinit var linearLayout8: LinearLayout
        lateinit var linearLayout9: LinearLayout
        var currentPhotoPath: String? = null
        const val CAMERA_PERM_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
        var currentDate = ""
        internal lateinit var db:DatabaseHandler
        lateinit var bmp:Bitmap

        var StoreTypeList = ArrayList<String>()
        var arrayAdapter3List = ArrayList<String>()
        lateinit var arrayAdapter3: ArrayAdapter<String>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        textViewSavedItem = findViewById(R.id.txt_saved_item)
        textViewCalendar = findViewById(R.id.txt_calendar)
        textViewItems = findViewById(R.id.txt_items)
        textViewStoreName = findViewById(R.id.txt_store_name)
        textViewInspectionDate = findViewById(R.id.txt_inspection_date)
        editTextQr = findViewById(R.id.edt_qr)
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
        cardView = findViewById(R.id.con_layout)
        linearLayoutStoreCode = findViewById(R.id.layout_store_code)
        linearLayoutStoreCode1 = findViewById(R.id.layout_store_code1)
        linearLayoutStoreName = findViewById(R.id.layout_store_name)
        linearLayout1 = findViewById(R.id.layout1)
        linearLayout2 = findViewById(R.id.layout2)
        linearLayout3 = findViewById(R.id.layout3)
        linearLayout4 = findViewById(R.id.layout4)
        linearLayout5 = findViewById(R.id.layout5)
        linearLayout6 = findViewById(R.id.layout6)
        linearLayout7 = findViewById(R.id.layout7)
        linearLayout8 = findViewById(R.id.layout8)
        linearLayout9 = findViewById(R.id.layout9)

        cardView.visibility = GONE
        linearLayout1.visibility = INVISIBLE
        linearLayout2.visibility = INVISIBLE
        linearLayout3.visibility = GONE
        linearLayout4.visibility = GONE
        linearLayout5.visibility = GONE
        linearLayout6.visibility = GONE
        linearLayout7.visibility = GONE
        linearLayout8.visibility = GONE
        linearLayout9.visibility = GONE
        buttonSave.visibility = INVISIBLE


        db = DatabaseHandler(this)
        db.getRegisteredItems()
        textViewItems.text = "Registered : ${DatabaseHandler.registeredItems} item(s)"
        var storenameList = DatabaseHandler.StoreNameArray


//        for(items in 1 until DatabaseHandler.MaintenanceArray.size){
//            companyList.add(DatabaseHandler.MaintenanceArray[items].maintenance_company!!)
//        }
//
//        for(items in 1 until DatabaseHandler.FillingPlantArray.size){
//            planList.add(DatabaseHandler.FillingPlantArray[items].filling_plant!!)
//        }
//
        getDate()

        editTextQr.requestFocus()

        cardView.setOnClickListener {
            askCameraPermissions()
        }

        editTextQr.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                db.checkQR(editTextQr.text.toString())
                if (DatabaseHandler.qrExists) {
                    textViewSavedItem.setTextColor(Color.parseColor("#EE4444"))
                    textViewSavedItem.text =
                            "This QR Code Already saved : ${editTextQr.text.toString()}"
                    imageViewCamera.setImageDrawable(null)
                    Toast.makeText(this, "This QR already saved", Toast.LENGTH_SHORT).show()
                    alarmSound()
                    editTextQr.text.clear()
                    editTextFa.text.clear()
                    editTextSerial.text.clear()
                    editTextQr.requestFocus()
                    currentPhotoPath = null
                    cardView.visibility = GONE
                    linearLayout1.visibility = INVISIBLE
                    linearLayout2.visibility = INVISIBLE
                    linearLayout3.visibility = GONE
                    linearLayout4.visibility = GONE
                    linearLayout5.visibility = GONE
                    linearLayout6.visibility = GONE
                    linearLayout7.visibility = GONE
                    linearLayout8.visibility = GONE
                    linearLayout9.visibility = GONE
                    buttonSave.visibility = INVISIBLE
                } else {
                    textViewSavedItem.text = ""
                    cardView.visibility = VISIBLE
                    imageViewCamera.setImageResource(android.R.drawable.ic_menu_camera)
                    linearLayout1.visibility = VISIBLE
                    linearLayout2.visibility = VISIBLE
                    linearLayout3.visibility = VISIBLE
                    linearLayout4.visibility = VISIBLE
                    linearLayout5.visibility = VISIBLE
                    linearLayout6.visibility = VISIBLE
                    linearLayout7.visibility = VISIBLE
                    linearLayout8.visibility = VISIBLE
                    linearLayout9.visibility = VISIBLE
                    buttonSave.visibility = VISIBLE
                }
            }
            false
        })

        buttonSave.setOnClickListener {
            if(editTextQr.text.toString() == ""|| editTextFa.text.toString() == "" || editTextSerial.text.toString() == ""){
                Toast.makeText(this, "Please enter the required data", Toast.LENGTH_SHORT).show()
            }
            else {
                when {
                    editTextStoreCode.text.toString() == "" && spinnerStoreType.selectedItem.toString() == "C-ลูกค้า" -> {
                        Toast.makeText(this, "Please Enter Store Code", Toast.LENGTH_SHORT).show()
                    }
                    spinnerMaterial.selectedItemId.toInt() == 0 ->  {
                        Toast.makeText(this, "Please Select Material  Code", Toast.LENGTH_SHORT).show()
                    }
                    spinnerMaster.selectedItemId.toInt() == 0 ->  {
                        Toast.makeText(this, "Please Select Manufacturer", Toast.LENGTH_SHORT).show()
                    }
                    spinnerInspector.selectedItemId.toInt() == 0 ->  {
                        Toast.makeText(this, "Please Select Inspector", Toast.LENGTH_SHORT).show()
                    }
                    spinnerStoreType.selectedItemId.toInt() == 0 && spinnerStoreCode.selectedItemId.toInt() == 0 ->  {
                        Toast.makeText(this, "Please Select Maintenance Comapny", Toast.LENGTH_SHORT).show()
                    }
                    spinnerStoreType.selectedItemId.toInt() == 1 && spinnerStoreCode.selectedItemId.toInt() == 0 ->  {
                        Toast.makeText(this, "Please Select Filling Plant", Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        db.checkQR(editTextQr.text.toString())
                        if (DatabaseHandler.qrExists) {
                            textViewSavedItem.setTextColor(Color.parseColor("#EE4444"))
                            textViewSavedItem.text = "This QR Code Already saved : ${editTextQr.text.toString()}"
                            imageViewCamera.setImageDrawable(null)
                            Toast.makeText(this, "This QR already saved", Toast.LENGTH_SHORT).show()
                            alarmSound()
                            editTextQr.text.clear()
                            editTextFa.text.clear()
                            editTextSerial.text.clear()
                            currentPhotoPath = null
                        } else {
                            if (currentPhotoPath == null) {
                                Toast.makeText(this, "Please take the photo", Toast.LENGTH_SHORT).show()
                            } else {
                                val lowerStoreType = spinnerStoreType.selectedItem.toString().toLowerCase().take(1)
                                if (spinnerStoreType.selectedItem == "C-ลูกค้า") {
                                    if (editTextStoreCode.text.toString() == "") {
                                        Toast.makeText(
                                                this,
                                                "Please Enter Store Code",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        db.addData(
                                                editTextQr.text.toString(),
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
                                    db.addData(
                                            editTextQr.text.toString(),
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
//                            makeDir()
//                            copyFile(File(currentPhotoPath), File("/sdcard/Download/WP/Pictures/${editTextQr.text.toString()}.jpg"))
                                copyFile(
                                        File(currentPhotoPath), File(
                                        "${
                                            Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_DOWNLOADS
                                            ).path + path
                                        }/${editTextQr.text.toString()}.jpg"
                                )
                                )
//                                val bytes = ByteArrayOutputStream()
//                                bmp.compress(Bitmap.CompressFormat.PNG, 50, bytes);
//
//                                val fo = FileOutputStream(File(
//                                        "${
//                                            Environment.getExternalStoragePublicDirectory(
//                                                    Environment.DIRECTORY_DOWNLOADS
//                                            ).path + path
//                                        }/${editTextQr.text.toString()}.jpg"
//                                ))
//                                fo.write(bytes.toByteArray())
//                                fo.close()

                                File(currentPhotoPath).delete()

                                textViewItems.text = "Registered : ${DatabaseHandler.registeredItems} item(s)"
                                textViewSavedItem.setTextColor(Color.parseColor("#99CC66"))
                                textViewSavedItem.text = "Saved : ${editTextQr.text.toString()}"
                                editTextQr.text.clear()
                                editTextFa.text.clear()
                                editTextSerial.text.clear()
                                editTextStoreCode.text.clear()
                                currentPhotoPath = null
                                imageViewCamera.setImageDrawable(null)
                                cardView.visibility = GONE
                                linearLayout1.visibility = INVISIBLE
                                linearLayout2.visibility = INVISIBLE
                                linearLayout3.visibility = GONE
                                linearLayout4.visibility = GONE
                                linearLayout5.visibility = GONE
                                linearLayout6.visibility = GONE
                                linearLayout7.visibility = GONE
                                linearLayout8.visibility = GONE
                                linearLayout9.visibility = GONE
                                buttonSave.visibility = INVISIBLE
                                editTextQr.requestFocus()
                                spinnerAdapter()
                            }
                        }
                    }
                }

            }

        }

        imageViewCalendar.setOnClickListener {
            datePicker(textViewCalendar)
        }

        imageViewInspectionCalendar.setOnClickListener {
            datePicker(textViewInspectionDate)
        }


        StoreTypeList.clear()
        StoreTypeList.add("A-โรงซ่อม")
        StoreTypeList.add("B-โรงบรรจุ")
        StoreTypeList.add("C-ลูกค้า")

        spinnerAdapter()

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
                    println(DatabaseHandler.MaintenanceArray.size)
                    arrayAdapter3.clear()
                    for(items in 0 until DatabaseHandler.MaintenanceArray.size){
                        arrayAdapter3.add(DatabaseHandler.MaintenanceArray[items].code)
                    }
                    arrayAdapter3.notifyDataSetChanged()
                    spinnerStoreCode.setSelection(0)
                    textViewStoreName.text = DatabaseHandler.MaintenanceArray[0].maintenance_company

                    linearLayoutStoreCode.visibility = VISIBLE
                    linearLayoutStoreCode1.visibility = GONE
                }

                if(spinnerStoreType.selectedItem == "B-โรงบรรจุ"){
                    arrayAdapter3.clear()
                    for(items in 0 until DatabaseHandler.FillingPlantArray.size){
                        arrayAdapter3.add(DatabaseHandler.FillingPlantArray[items].code)
                    }
                    arrayAdapter3.notifyDataSetChanged()
                    spinnerStoreCode.setSelection(0)
                    textViewStoreName.text = DatabaseHandler.FillingPlantArray[0].filling_plant

                    linearLayoutStoreCode.visibility = VISIBLE
                    linearLayoutStoreCode1.visibility = GONE
                }

                if(spinnerStoreType.selectedItem == "C-ลูกค้า"){
                    editTextStoreCode.requestFocus()
                    linearLayoutStoreCode.visibility = GONE
                    linearLayoutStoreCode1.visibility = VISIBLE
                    Thread {
                        try{
                            for(item in storenameList){
                                println("TEXT : ${editTextStoreCode.text.toString()}")
                                if(item.code == editTextStoreCode.text.toString()){
                                    textViewStoreName.text = item.customer
                                    break
                                }
                                else{
                                    textViewStoreName.text = "Not found"
                                }
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }.start()
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
                var index =spinnerStoreCode.selectedItemId.toInt()
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
                                println("$s")
                                if (item.code == "$s") {
                                    storeName = item.customer!!
                                    break
                                } else {
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

    }

    fun spinnerAdapter(){
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                DatabaseHandler.MaterialArray
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaterial.adapter = arrayAdapter

        var arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                DatabaseHandler.MasterArray
        )
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaster.adapter = arrayAdapter1
        spinnerMaster.setSelection(-1)

        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                StoreTypeList
        )
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStoreType.adapter = arrayAdapter2

        arrayAdapter3 = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                arrayAdapter3List
        )
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStoreCode.adapter = arrayAdapter3

        val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                DatabaseHandler.InspectorArray
        )

        arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInspector.adapter = arrayAdapter4

    }

    private fun makeDir(){

        val path = "/WP/Pictures"
        val filePath: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + path)
        if(!filePath.exists()){
            filePath.mkdir()
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            System.out.println("OK")
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("OK")
                val f = File(currentPhotoPath)
                imageViewCamera!!.setImageURI(Uri.fromFile(f))
//                Picasso.get().load(f).into(imageViewCamera)
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
        textViewCalendar.text = currentDate
        textViewInspectionDate.text = currentDate
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

    fun alarmSound(){
        val afd: AssetFileDescriptor = this.assets.openFd("buzz.wav")
        val player = MediaPlayer()
        player.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
        )
        player.prepare()
        player.start()
        val handler = Handler()
        handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
    }

    fun Compress(){
        val scaleDivider = 4
        try {

            // 1. Convert uri to bitmap
//             val imageUri: Uri = ""
//             val fullBitmap =
//                 MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri)

            // 2. Get the downsized image content as a byte[]
//             val scaleWidth: Int = fullBitmap.width / scaleDivider
//             val scaleHeight: Int = fullBitmap.height / scaleDivider
//             val downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)

            // 3. Upload the byte[]; Eg, if you are using Firebase
//             val storageReference: StorageReference =
//                 FirebaseStorage.getInstance().getReference("/somepath")
//             storageReference.putBytes(downsizedImageBytes)
        } catch (ioEx: IOException) {
            ioEx.printStackTrace()
        }
    }

//    fun ok(){
//        val options = BitmapFactory.Options()
//        options.inSampleSize = 2 //4, 8, etc. the more value, the worst quality of image
//
//        val f = File(currentPhotoPath)
//        bmp = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.fromFile(f)), null, options)!!
//        imageViewCamera.setImageBitmap(bmp)
//    }


}