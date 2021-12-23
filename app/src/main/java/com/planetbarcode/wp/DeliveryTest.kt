package com.planetbarcode.wp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.planetbarcode.wp.Adapter.MaterialCodeAdapter
import com.planetbarcode.wp.Adapter.ViewRegisterAdapter
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.Model.MaterialCodeModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DeliveryTest : AppCompatActivity() {

    companion object {
        lateinit var floatingActionButton: FloatingActionButton
        lateinit var editTextQR: EditText
        lateinit var editTextLocationCode: EditText
        lateinit var editTextDestinationCode: EditText
        lateinit var editTextDoc: EditText
        lateinit var editTextRouteNo: EditText
        lateinit var linearLayoutLocationCode: LinearLayout
        lateinit var linearLayoutLocationCode1: LinearLayout
        lateinit var linearLayoutDestinationCode: LinearLayout
        lateinit var linearLayoutDestinationCode1: LinearLayout
        lateinit var textViewItems: TextView
        lateinit var textViewDeliveryDate: TextView
        lateinit var textViewShippingDate:  TextView
        lateinit var textViewLocationName: TextView
        lateinit var textViewDestinationName: TextView
        lateinit var spinnerDocType: Spinner
        lateinit var spinnerTankType: Spinner
        lateinit var spinnerRound: Spinner
        lateinit var spinnerVehicleCode:Spinner
        lateinit var spinnerLocationCode:Spinner
        lateinit var spinnerLocationType:Spinner
        lateinit var spinnerDestinationCode:Spinner
        lateinit var spinnerDestinationType:Spinner
        lateinit var imageButtonAddMaterialCode: ImageButton
        lateinit var arrayAdapterDocType: ArrayAdapter<String>
        lateinit var arrayAdaterTankType: ArrayAdapter<String>
        lateinit var arrayAdapterRound: ArrayAdapter<String>
        lateinit var arrayAdapterVehicle: ArrayAdapter<String>
        lateinit var arrayAdaperLocationType: ArrayAdapter<String>
        lateinit var arrayAdaperLocationCode: ArrayAdapter<String>
        lateinit var arrayAdaperDestinationType: ArrayAdapter<String>
        lateinit var arrayAdaperDestinationCode: ArrayAdapter<String>
        lateinit var linearLayout1: LinearLayout
        lateinit var linearLayout2: LinearLayout
        lateinit var linearLayout3: LinearLayout
        lateinit var linearLayout4: LinearLayout
        lateinit var linearLayout5: LinearLayout
        lateinit var linearLayout6: LinearLayout
        lateinit var linearLayout7: LinearLayout
        lateinit var linearLayout8: LinearLayout
        lateinit var linearLayout9: LinearLayout
        lateinit var cardView: CardView
        internal lateinit var dialog: AlertDialog
        internal lateinit var buttonOk: Button
        internal lateinit var buttonCancel: Button
//        internal lateinit var editTextMaterialCode: EditText
        internal lateinit var spinnerMaterial : Spinner
        internal lateinit var editTextQty: EditText
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager

        lateinit var db: DatabaseHandler
        var DocTypeArray = ArrayList<String>()
        var TankTypeArray = ArrayList<String>()
        var RoundArray = ArrayList<String>()
        var StoreTypeArray = ArrayList<String>()
        var StoreTypeArray1 = ArrayList<String>()

        var unit = ""
        var doc = ""
        var doc_date = ""
        var ship = ""
        var material = ""
        var qty = ""
        var finalMaterial = ""
        var qr = ""
        var delivery_date = ""
        var splittedDocument = ""
        var currentDate = ""
        var selectedDate = ""
        var fullDoc = ""
        var MaterialListArray = ArrayList<MaterialCodeModel>()
        var EmptyArray = ArrayList<String>()
        var delivery = true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_test)

        delivery = true
        db = DatabaseHandler(this)
        db.getDeliveryItems()
        var storenameArray = DatabaseHandler.StoreNameArray

        floatingActionButton = findViewById(R.id.fab)
        editTextQR = findViewById(R.id.edt_qr)
        editTextLocationCode = findViewById(R.id.edt_location_code)
        editTextDestinationCode = findViewById(R.id.edt_destination_code)
        editTextRouteNo = findViewById(R.id.edt_route_no)
        linearLayoutLocationCode = findViewById(R.id.layout_location_code)
        linearLayoutLocationCode1 = findViewById(R.id.layout_location_code1)
        linearLayoutDestinationCode = findViewById(R.id.layout_destination_code)
        linearLayoutDestinationCode1 = findViewById(R.id.layout_destination_code1)
        editTextDoc = findViewById(R.id.edt_doc)
        textViewItems = findViewById(R.id.txt_items)
        textViewDeliveryDate = findViewById(R.id.txt_delivery_date)
        textViewShippingDate = findViewById(R.id.txt_shipping_date)
        textViewLocationName = findViewById(R.id.txt_location_name)
        textViewDestinationName = findViewById(R.id.txt_destination_name)
        spinnerDocType = findViewById(R.id.spinner_doc_type)
        spinnerTankType = findViewById(R.id.spinner_tank_type)
        spinnerRound = findViewById(R.id.spinner_round_no)
        spinnerVehicleCode = findViewById(R.id.spinner_vehicle)
        spinnerLocationType = findViewById(R.id.spinner_location_type)
        spinnerLocationCode = findViewById(R.id.spinner_location_code)
        spinnerDestinationType = findViewById(R.id.spinner_destination_type)
        spinnerDestinationCode = findViewById(R.id.spinner_destination_code)
        imageButtonAddMaterialCode = findViewById(R.id.img_add_material_code)
        linearLayout1 = findViewById(R.id.layout1)
        linearLayout2 = findViewById(R.id.layout2)
        linearLayout3 = findViewById(R.id.layout3)
        linearLayout4 = findViewById(R.id.layout4)
        linearLayout5 = findViewById(R.id.layout5)
        linearLayout6 = findViewById(R.id.layout6)
        linearLayout7 = findViewById(R.id.layout7)
        linearLayout8 = findViewById(R.id.layout8)
        linearLayout9 = findViewById(R.id.layout9)
        cardView = findViewById(R.id.layout10)

        linearLayout1.visibility = GONE
        linearLayout2.visibility = GONE
        linearLayout3.visibility = GONE
        linearLayout4.visibility = GONE
        linearLayout5.visibility = GONE
        linearLayout6.visibility = GONE
        linearLayout7.visibility = GONE
        linearLayout8.visibility = GONE
        linearLayout9.visibility = GONE
        cardView.visibility = GONE


        viewAdapter = MaterialCodeAdapter(MaterialListArray, this)
        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        textViewItems.text = "Delivery : ${DatabaseHandler.deliveryItems} item(s)"
        editTextQR.requestFocus()
        getDate()
        MaterialListArray.clear()

        DocTypeArray.clear()
        DocTypeArray.add("1=ใบส่งของตัวจริง")
        DocTypeArray.add("2=ใบส่งของชั่วคราว")

        TankTypeArray.clear()
        TankTypeArray.add("0=ถังหมุนเวียน")
        TankTypeArray.add("1=ถังมัดจำ")
        TankTypeArray.add("2=ถังยืม")
        TankTypeArray.add("3=ถังคืน")
        TankTypeArray.add("4=ถังซ่อม")
        TankTypeArray.add("5=ถังเคลม")

        RoundArray.clear()
        RoundArray.add("Select Round No")
        RoundArray.add("1")
        RoundArray.add("2")
        RoundArray.add("3")
        RoundArray.add("4")
        RoundArray.add("5")

        StoreTypeArray.clear()
        StoreTypeArray.add("A-โรงซ่อม")
        StoreTypeArray.add("B-โรงบรรจุ")
        StoreTypeArray.add("C-ลูกค้า")

        StoreTypeArray1.clear()
        StoreTypeArray1.add("A-โรงซ่อม")
        StoreTypeArray1.add("B-โรงบรรจุ")
        StoreTypeArray1.add("C-ลูกค้า")

        arrayAdapterDocType = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DocTypeArray)
        arrayAdapterDocType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocType.adapter = arrayAdapterDocType

        arrayAdaterTankType = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TankTypeArray)
        arrayAdaterTankType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTankType.adapter = arrayAdaterTankType

        arrayAdapterRound = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RoundArray)
        arrayAdapterRound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRound.adapter = arrayAdapterRound

        arrayAdapterVehicle  = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.CarCodeList)
        arrayAdapterVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVehicleCode.adapter = arrayAdapterVehicle

        arrayAdaperLocationType = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StoreTypeArray)
        arrayAdaperLocationType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocationType.adapter  = arrayAdaperLocationType

        arrayAdaperDestinationType   = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StoreTypeArray1)
        arrayAdaperDestinationType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDestinationType.adapter  = arrayAdaperDestinationType

        arrayAdaperLocationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, EmptyArray)
        arrayAdaperLocationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        arrayAdaperDestinationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, EmptyArray)
        arrayAdaperDestinationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerDocType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (spinnerDocType.selectedItem.toString() == "1=ใบส่งของตัวจริง") {
                    spinnerTankType.setSelection(0)
                    spinnerTankType.isEnabled = false
                } else {
                    spinnerTankType.isEnabled = true
                }
            }
        }

        spinnerLocationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if(spinnerLocationType.selectedItem == "A-โรงซ่อม"){
                    spinnerLcA()
                    textViewLocationName.text = DatabaseHandler.MaintenanceArray[0].maintenance_company
                    linearLayoutLocationCode.visibility = View.VISIBLE
                    linearLayoutLocationCode1.visibility = View.GONE
                    println("A")
                }

                if(spinnerLocationType.selectedItem == "B-โรงบรรจุ"){
                    spinnerLcB()
                    textViewLocationName.text = DatabaseHandler.FillingPlantArray[0].filling_plant
                    linearLayoutLocationCode.visibility = View.VISIBLE
                    linearLayoutLocationCode1.visibility = View.GONE
                    println("B")
                }

                if(spinnerLocationType.selectedItem == "C-ลูกค้า"){
                    editTextLocationCode.requestFocus()
                    linearLayoutLocationCode.visibility = View.GONE
                    linearLayoutLocationCode1.visibility = View.VISIBLE
                    println(storenameArray.size)
                    Thread {
                        try{
                            for(item in storenameArray){
                                if(item.code == editTextLocationCode.text.toString()){
                                    textViewLocationName.text = item.customer
                                    break
                                }
                                else{
                                    textViewLocationName.text = "Not found"
                                }
                            }
                        }catch (e:Exception ){
                            e.printStackTrace()
                        }
                    }.start()
                }
            }
        }

        spinnerLocationCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                var index = spinnerLocationCode.selectedItemId.toInt()
                if(spinnerLocationType.selectedItem == "A-โรงซ่อม"){
                    textViewLocationName.text = DatabaseHandler.MaintenanceArray[index].maintenance_company
                }

                if(spinnerLocationType.selectedItem == "B-โรงบรรจุ"){
                    textViewLocationName.text = DatabaseHandler.FillingPlantArray[index].filling_plant
                }
            }
        }

        editTextLocationCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Thread {
                    try {
                        var storeName = ""
                        if (s != "") {
                            for (item in storenameArray) {
                                if (item.code == "$s") {
                                    storeName = item.customer!!
                                    break
                                }
                                else{
                                    storeName = "Not found"
                                }
                            }
                            textViewLocationName.text = storeName
                        } else {
                            textViewLocationName.text = "Not found"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        })

        spinnerDestinationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if(spinnerDestinationType.selectedItem == "A-โรงซ่อม"){
                    spinnerDsA()
                    textViewDestinationName.text = DatabaseHandler.MaintenanceArray[0].maintenance_company
                    linearLayoutDestinationCode.visibility = VISIBLE
                    linearLayoutDestinationCode1.visibility = GONE
                }

                if(spinnerDestinationType.selectedItem == "B-โรงบรรจุ"){
                    spinnerDsB()
                    textViewDestinationName.text = DatabaseHandler.FillingPlantArray[0].filling_plant
                    linearLayoutDestinationCode.visibility = VISIBLE
                    linearLayoutDestinationCode1.visibility = GONE
                    println("DES B")
                }

                if(spinnerDestinationType.selectedItem == "C-ลูกค้า"){
                    editTextDestinationCode.requestFocus()
                    linearLayoutDestinationCode.visibility = View.GONE
                    linearLayoutDestinationCode1.visibility = View.VISIBLE
                    println(storenameArray.size)
                    Thread {
                        try{
                            for(item in storenameArray){
                                if(item.code == editTextDestinationCode.text.toString()){
                                    textViewDestinationName.text = item.customer
                                    break
                                }
                                else{
                                    textViewDestinationName.text = "Not found"
                                }
                            }
                        }catch (e:Exception ){
                            e.printStackTrace()
                        }
                    }.start()
                }
            }
        }

        spinnerDestinationCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                var index = spinnerDestinationCode.selectedItemId.toInt()
                if(spinnerDestinationType.selectedItem == "A-โรงซ่อม"){
                    textViewDestinationName.text = DatabaseHandler.MaintenanceArray[index].maintenance_company
                }

                if(spinnerDestinationType.selectedItem == "B-โรงบรรจุ"){
                    textViewDestinationName.text = DatabaseHandler.FillingPlantArray[index].filling_plant
                }
            }
        }

        editTextDestinationCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Thread {
                    try {
                        var storeName = ""
                        if (s != "") {
                            for (item in storenameArray) {
                                if (item.code == "$s") {
                                    storeName = item.customer!!
                                    break
                                }
                                else{
                                    storeName = "Not found"
                                }
                            }
                            textViewDestinationName.text = storeName
                        } else {
                            textViewDestinationName.text = "Not found"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        })

        editTextQR.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (editTextQR.text.toString() == "") {
                    Toast.makeText(this, "Please scan QR code", Toast.LENGTH_SHORT).show()
                } else {
                    if (editTextQR.text.toString().length < 47) {
                        Toast.makeText(this, "Please Scan Valid Barcode", Toast.LENGTH_SHORT).show()
                    } else {
                        linearLayout1.visibility = VISIBLE
                        linearLayout2.visibility = VISIBLE
                        linearLayout3.visibility = VISIBLE
                        linearLayout4.visibility = VISIBLE
                        linearLayout5.visibility = VISIBLE
                        linearLayout6.visibility = VISIBLE
                        linearLayout7.visibility = VISIBLE
                        linearLayout8.visibility = VISIBLE
                        linearLayout9.visibility = VISIBLE
                        cardView.visibility = VISIBLE

                        spinnerDocType.setSelection(0)
                        MaterialListArray.clear()
                        qr = editTextQR.text.toString().replace(" ", "")
                        val spl = qr.split("&").toTypedArray()
                        doc = spl[0].substring(0, 10)
                        doc_date = spl[0].substring(10, 18)
                        var formattedDate = doc_date.substring(0, 4) + "-" + doc_date.substring(4, 6) + "-" + doc_date.substring(6, 8)

                        ship = spl[0].substring(18, 28).trimStart('0')
                        material = spl[0].substring(28, 46).trimStart('0')
                        qty = spl[0].substring(46, spl[0].indexOf("."))
                        unit = spl[0].takeLast(3)
                        finalMaterial = "${material}-${qty}-${unit}"
                        MaterialListArray.add(MaterialCodeModel(material, Integer.parseInt(qty), unit))
                        println("${material}    ${qty}  ${unit}")

                        var docType = spinnerDocType.selectedItem.toString().take(1)
                        println()
                        db.checkDoc("$docType/${doc}")
                        if (DatabaseHandler.docExists) {
//                            textViewDeliveryDate.text = formattedDate
//                            textViewMaterialList.text = finalMaterial
                            val spinnerTankPosition = arrayAdaterTankType.getPosition(DatabaseHandler.tank)
                            spinnerTankType.setSelection(spinnerTankPosition)

                            val spinnerVehiclePostion = arrayAdapterVehicle.getPosition(DatabaseHandler.v_code)
                            spinnerVehicleCode.setSelection(spinnerVehiclePostion)

                            val spinnerRoundPosition = arrayAdapterRound.getPosition(DatabaseHandler.round)
                            spinnerRound.setSelection(spinnerRoundPosition)

                            val spinnerLocaitonTypePosition = arrayAdaperLocationType.getPosition(DatabaseHandler.lc_type)
                            spinnerLocationType.setSelection(spinnerLocaitonTypePosition)

                            val spinnerDestinationTypePosition = arrayAdaperDestinationType.getPosition(DatabaseHandler.des_type)
                            spinnerDestinationType.setSelection(spinnerDestinationTypePosition)

                            textViewDeliveryDate.text = DatabaseHandler.d_date
                            textViewShippingDate.text = DatabaseHandler.s_date
                            editTextRouteNo.setText(DatabaseHandler.route)
                            editTextDoc.setText("${doc}")
                            if (spinnerLocationType.selectedItem == "C-ลูกค้า"){
                                editTextLocationCode.setText(DatabaseHandler.lc_code)
                            }
                            if (spinnerDestinationType.selectedItem == "C-ลูกค้า"){
                                editTextDestinationCode.setText(DatabaseHandler.des_code)
                            }

                            println ("SIZE ${MaterialListArray.size}")
                            setupRecyclerView (MaterialListArray)

                            Toast.makeText(this, "Document Exists", Toast.LENGTH_SHORT).show()
                        } else {
                            spinnerTankType.setSelection(0)
                            spinnerVehicleCode.setSelection(0)
                            spinnerRound.setSelection(0)
                            spinnerLocationType.setSelection(0)
                            spinnerLocationCode.setSelection(0)
                            spinnerDestinationType.setSelection(0)
                            spinnerDestinationCode.setSelection(0)

                            editTextDoc.setText("${doc}")
                            textViewDeliveryDate.text = formattedDate
                            MaterialListArray.add(MaterialCodeModel(material, Integer.parseInt(qty), unit))
                            if (spl.size > 1) {
                                for (i in 1..spl.size - 1) {
                                    material = spl[i].substring(0, 18).trimStart('0')
                                    qty = spl[i].substring(18, spl[i].indexOf("."))
                                    unit = spl[i].takeLast(3)
                                    MaterialListArray.add(MaterialCodeModel(material, Integer.parseInt(qty), unit))
                                    finalMaterial = "${finalMaterial}\n${material}-${qty}-${unit}"
                                    println("${Delivery.material}${Delivery.qty}  ${Delivery.unit}")
                                }
                            }
//                            textViewMaterialList.text = finalMaterial
                        }
                        spinnerDestinationType.setSelection(2)
                        editTextDestinationCode.setText(ship)
                        linearLayoutDestinationCode.visibility = View.GONE
                        linearLayoutDestinationCode1.visibility = View.VISIBLE
                        editTextQR.text.clear()
                        setupRecyclerView(MaterialListArray)
                    }
                }
            }
            false
        })

        editTextDoc.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                linearLayout1.visibility = VISIBLE
                linearLayout2.visibility = VISIBLE
                linearLayout3.visibility = VISIBLE
                linearLayout4.visibility = VISIBLE
                linearLayout5.visibility = VISIBLE
                linearLayout6.visibility = VISIBLE
                linearLayout7.visibility = VISIBLE
                linearLayout8.visibility = VISIBLE
                linearLayout9.visibility = VISIBLE
                cardView.visibility = VISIBLE

                var docType = spinnerDocType.selectedItem.toString().take(1)
                db.checkDoc("$docType/${editTextDoc.text.toString()}")
                if (DatabaseHandler.docExists) {
                    val spinnerTankPosition = arrayAdaterTankType.getPosition(DatabaseHandler.tank)
                    spinnerTankType.setSelection(spinnerTankPosition)

                    val spinnerVehiclePostion = arrayAdapterVehicle.getPosition(DatabaseHandler.v_code)
                    spinnerVehicleCode.setSelection(spinnerVehiclePostion)

                    val spinnerRoundPosition = arrayAdapterRound.getPosition(DatabaseHandler.round)
                    spinnerRound.setSelection(spinnerRoundPosition)

                    val spinnerLocaitonTypePosition = arrayAdaperLocationType.getPosition(DatabaseHandler.lc_type)
                    spinnerLocationType.setSelection(spinnerLocaitonTypePosition)

                    val spinnerDestinationTypePosition = arrayAdaperDestinationType.getPosition(DatabaseHandler.des_type)
                    spinnerDestinationType.setSelection(spinnerDestinationTypePosition)

                    textViewDeliveryDate.text = DatabaseHandler.d_date
                    textViewShippingDate.text = DatabaseHandler.s_date
                    editTextRouteNo.setText(DatabaseHandler.route)
                    if (spinnerLocationType.selectedItem == "C-ลูกค้า"){
                        editTextLocationCode.setText(DatabaseHandler.lc_code)
                    }
                    if (spinnerDestinationType.selectedItem == "C-ลูกค้า"){
                        editTextDestinationCode.setText(DatabaseHandler.des_code)
                    }

                    println ("SIZE ${MaterialListArray.size}")
                    setupRecyclerView (MaterialListArray)
                    Toast.makeText(this, "Document Exists", Toast.LENGTH_SHORT).show()
                }
                else{
                    spinnerTankType.setSelection(0)
                    spinnerVehicleCode.setSelection(0)
                    spinnerRound.setSelection(0)
                    spinnerLocationType.setSelection(0)
                    spinnerLocationCode.setSelection(0)
                    spinnerDestinationType.setSelection(0)
                    spinnerDestinationCode.setSelection(0)
                }

            }
            false
        })

        textViewDeliveryDate.setOnClickListener {
            if(textViewDeliveryDate.text.toString() != currentDate){
                datePicker(textViewDeliveryDate,true)
            }
            else{
                datePicker(textViewDeliveryDate,false)
            }
        }

        textViewShippingDate.setOnClickListener {
            if(textViewShippingDate.text.toString() != currentDate){
                datePicker(textViewShippingDate,true)
            }
            else{
                datePicker(textViewShippingDate,false)
            }
        }

        imageButtonAddMaterialCode.setOnClickListener{
            AddMaterialCodeDialog()
        }

        floatingActionButton.setOnClickListener {
            var lowerLcCode = ""
            var lowerDesCode = ""
            var MaterailList = ""

            when {
                spinnerVehicleCode.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Vehicle Code",Toast.LENGTH_SHORT).show()
                }
                spinnerRound.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Round Number",Toast.LENGTH_SHORT).show()
                }
                spinnerLocationType.selectedItemId.toInt() == 0 && spinnerLocationCode.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Location Code",Toast.LENGTH_SHORT).show()
                }
                spinnerLocationType.selectedItemId.toInt() == 1 && spinnerLocationCode.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Location Code",Toast.LENGTH_SHORT).show()
                }
                spinnerLocationType.selectedItemId.toInt() == 2 && editTextLocationCode.text.isEmpty() -> {
                    Toast.makeText(this,"Please Enter Location Code",Toast.LENGTH_SHORT).show()
                }
                spinnerDestinationType.selectedItemId.toInt() == 0 && spinnerDestinationCode.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Destination Code",Toast.LENGTH_SHORT).show()
                }
                spinnerDestinationType.selectedItemId.toInt() == 1 && spinnerDestinationCode.selectedItemId.toInt() == 0 -> {
                    Toast.makeText(this,"Please Select Destination Code",Toast.LENGTH_SHORT).show()
                }
                spinnerDestinationType.selectedItemId.toInt() == 2 && editTextDestinationCode.text.isEmpty() -> {
                    Toast.makeText(this,"Please Enter Destination Code",Toast.LENGTH_SHORT).show()
                }
                editTextDoc.text.toString().isEmpty() ->{
                    Toast.makeText(this,"Please Enter Document Number",Toast.LENGTH_SHORT).show()
                }
                editTextRouteNo.text.toString().isEmpty() ->{
                    Toast.makeText(this,"Please Enter Route Number",Toast.LENGTH_SHORT).show()
                }
                spinnerLocationType.selectedItem.toString() == "C-ลูกค้า" && editTextLocationCode.text.toString().isEmpty()  ->{
                    Toast.makeText(this,"Please Enter Location Code",Toast.LENGTH_SHORT).show()
                }
                spinnerDestinationType.selectedItem.toString() == "C-ลูกค้า" && editTextDestinationCode.text.toString().isEmpty()  ->{
                    Toast.makeText(this,"Please Enter Destination Code",Toast.LENGTH_SHORT).show()
                }
                MaterialListArray.isEmpty() ->{
                    Toast.makeText(this,"Please Enter Material List",Toast.LENGTH_SHORT).show()
                }
                else -> {
                    for(i in 0 until MaterialListArray.size){
                        MaterailList = "$MaterailList${MaterialListArray[i].material_code}-${MaterialListArray[i].qty}-${MaterialListArray[i].unit}/"
                    }
                    var docType = spinnerDocType.selectedItem.toString().take(1)

                    val lowerLcType = spinnerLocationType.selectedItem.toString().toLowerCase().take(1)
                    lowerLcCode = if(lowerLcType == "c"){
                        editTextLocationCode.text.toString()
                    } else{
                        spinnerLocationCode.selectedItem.toString()
                    }

                    val lowerDesType = spinnerDestinationType.selectedItem.toString().toLowerCase().take(1)
                    lowerDesCode = if(lowerDesType == "c"){
                        editTextDestinationCode.text.toString()
                    }else{
                        spinnerDestinationCode.selectedItem.toString()
                    }

                    val firstCharTankType = spinnerTankType.selectedItem.toString().take(1)

                    db.addDoc(""+docType+"/"+editTextDoc.text.toString(), editTextRouteNo.text.toString(), textViewDeliveryDate.text.toString(), MaterailList.dropLast(1) , spinnerRound.selectedItem.toString(),
                            spinnerVehicleCode.selectedItem.toString(), textViewShippingDate.text.toString(), lowerLcCode, lowerLcType, lowerDesCode, lowerDesType, firstCharTankType)
//                textViewItems.text = "Delivery : ${DatabaseHandler.deliveryItems} item(s)"
                    AsyncGetSerial(this, ""+docType+"/"+editTextDoc.text.toString()).execute()
                }
            }

        }
    }

    fun spinnerLcA(){
        arrayAdaperLocationCode.clear()
        var array = ArrayList<String>()
        for(items in 0 until DatabaseHandler.MaintenanceArray.size){
            array.add(DatabaseHandler.MaintenanceArray[items].code!!)
        }
        arrayAdaperLocationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdaperLocationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocationCode.adapter = arrayAdaperLocationCode
        val spinnerLocaitonCodePosition = arrayAdaperLocationCode.getPosition(DatabaseHandler.lc_code)
        spinnerLocationCode.setSelection(spinnerLocaitonCodePosition)
    }

    fun spinnerLcB(){
        arrayAdaperLocationCode.clear()
        var array = ArrayList<String>()
        for(items in 0 until DatabaseHandler.FillingPlantArray.size){
            array.add(DatabaseHandler.FillingPlantArray[items].code!!)
        }
        arrayAdaperLocationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdaperLocationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocationCode.adapter = arrayAdaperLocationCode
        val spinnerLocaitonCodePosition = arrayAdaperLocationCode.getPosition(DatabaseHandler.lc_code)
        spinnerLocationCode.setSelection(spinnerLocaitonCodePosition)
    }

    fun spinnerDsA(){
        arrayAdaperDestinationCode.clear()
        var array = ArrayList<String>()
        for(items in 0 until DatabaseHandler.MaintenanceArray.size){
            array.add(DatabaseHandler.MaintenanceArray[items].code!!)
        }
        arrayAdaperDestinationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdaperDestinationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDestinationCode.adapter = arrayAdaperDestinationCode
        val spinnerDestinationCodePosition = arrayAdaperDestinationCode.getPosition(DatabaseHandler.des_code)
        spinnerDestinationCode.setSelection(spinnerDestinationCodePosition)

    }

    fun spinnerDsB(){
        arrayAdaperDestinationCode.clear()
        var array = ArrayList<String>()
        for(items in 0 until DatabaseHandler.FillingPlantArray.size){
            array.add(DatabaseHandler.FillingPlantArray[items].code!!)
        }
        arrayAdaperDestinationCode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
        arrayAdaperDestinationCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDestinationCode.adapter = arrayAdaperDestinationCode
        val spinnerDestinationCodePosition = arrayAdaperDestinationCode.getPosition(DatabaseHandler.des_code)
        spinnerDestinationCode.setSelection(spinnerDestinationCodePosition)
    }

    private fun setupRecyclerView(list: ArrayList<MaterialCodeModel>) {
        viewAdapter = MaterialCodeAdapter(list, this)

        recyclerView = findViewById(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun AddMaterialCodeDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.add_new_material, null)
        builder.setView(view)

        spinnerMaterial = view.findViewById(R.id.spinner_material)
        editTextQty = view.findViewById(R.id.edt_qty)
        buttonOk = view.findViewById(R.id.btn_ok)
        buttonCancel = view.findViewById(R.id.btn_cancel)
        var arrayAdapterMaterial = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.MaterialArray)
        arrayAdapterMaterial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaterial.adapter = arrayAdapterMaterial
        dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)

        buttonOk.setOnClickListener {
            if (editTextQty.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Quantity", Toast.LENGTH_SHORT).show()
            } else {
                MaterialListArray.add(MaterialCodeModel(spinnerMaterial.selectedItem.toString(), Integer.parseInt(editTextQty.text.toString()), "CYL"))
                viewAdapter.notifyDataSetChanged()
                dialog.dismiss()

            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun datePicker(textView: TextView,hasDate: Boolean){

        val c = GregorianCalendar()
        var year:Int? = null
        var month:Int? = null
        var day:Int? = null

        if(hasDate){
            var parsed = textView.text.toString().split("-").toTypedArray()
            println(parsed[0]+parsed[1]+parsed[2])
            c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(parsed[2]))
            c.set(Calendar.MONTH,Integer.parseInt(parsed[1])-1)
            c.set(Calendar.YEAR,Integer.parseInt(parsed[0]))
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }
        else{
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val mth = monthOfYear + 1
                val date = "" + dayOfMonth + "/" + mth + "/" + year

                val date_format = SimpleDateFormat("yyyy-mm-dd")
                val curFormater = SimpleDateFormat("dd/mm/yyyy")
                val dateObj = curFormater.parse(date)

                selectedDate = date_format.format(dateObj)
                textView.text  = selectedDate

            },
            year!!,
            month,
            day
        )
        dpd.show()
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
        textViewDeliveryDate.text = currentDate
        textViewShippingDate.text = currentDate
    }

    private class AsyncGetSerial(val context: Context, val doc:String) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db:DatabaseHandler

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context)
            db.getSerial(doc)
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
            fullDoc = doc
            val intent = Intent(context, DeliverySerial::class.java)
            context.startActivity(intent)
            super.onPostExecute(result)
        }
    }
}