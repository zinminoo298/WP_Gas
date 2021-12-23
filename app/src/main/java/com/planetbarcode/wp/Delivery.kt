package com.planetbarcode.wp

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import org.apache.commons.codec.binary.StringUtils
import org.apache.poi.util.StringUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Delivery : AppCompatActivity() {
    companion object{
        lateinit var floatingActionButton: FloatingActionButton
        lateinit var editTextQR: EditText
        lateinit var textViewDoc: TextView
        lateinit var textViewPlanningDate: TextView
        lateinit var textViewDeliveryDate: TextView
        lateinit var textViewMaterialList: TextView
        lateinit var textViewItems: TextView
        lateinit var spinnerRoute: Spinner
        lateinit var spinnerRound: Spinner
        lateinit var spinnerLicense: Spinner
        lateinit var spinnerStoreCode: Spinner
        lateinit var spinnerStoreType: Spinner
        lateinit var textViewShip: TextView
        lateinit var spinnerTank: Spinner
        lateinit var linearLayout1: LinearLayout
        lateinit var linearLayout2: LinearLayout
        lateinit var linearLayout3: LinearLayout
        lateinit var linearLayout4: LinearLayout
        lateinit var linearLayout5: LinearLayout
        lateinit var linearLayout6: LinearLayout

        var RouteList = ArrayList<String>()
        var RoundList = ArrayList<String>()
        var CarList = ArrayList<String>()
        var StoreTypeList = ArrayList<String>()
        var StoreCodeList = ArrayList<String>()
        var TankList = ArrayList<String>()

        var docNo = ""
        var docType = ""
        var selectedDate = ""
        val re = Regex("[^A-Za-z0-9 ]")


        lateinit var db:DatabaseHandler

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

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)

        floatingActionButton = findViewById(R.id.fab)
        editTextQR = findViewById(R.id.edt_qr)
        textViewDoc = findViewById(R.id.txt_doc)
        textViewPlanningDate = findViewById(R.id.txt_planning_date)
        textViewDeliveryDate = findViewById(R.id.txt_delivery_date)
        textViewMaterialList = findViewById(R.id.txt_material_list)
        textViewItems = findViewById(R.id.txt_items)
        spinnerRoute = findViewById(R.id.spinner_route)
        spinnerStoreCode = findViewById(R.id.spinner_store_code)
        spinnerLicense = findViewById(R.id.spinner_license)
        spinnerRound = findViewById(R.id.spinner_round)
        textViewShip = findViewById(R.id.txt_ship_to_code)
        spinnerTank = findViewById(R.id.spinner_tank_type)
        spinnerStoreType = findViewById(R.id.spinner_store_type)
        linearLayout1 = findViewById(R.id.layout)
        linearLayout2 = findViewById(R.id.layout1)
        linearLayout3 = findViewById(R.id.layout2)
        linearLayout4 = findViewById(R.id.layout3)
        linearLayout5 = findViewById(R.id.layout4)
        linearLayout6 = findViewById(R.id.layout5)
        db = DatabaseHandler(this)
        db.getDeliveryItems()
        textViewItems.text = "Delivery : ${DatabaseHandler.deliveryItems} item(s)"

        linearLayout1.visibility = INVISIBLE
        linearLayout2.visibility = INVISIBLE
        linearLayout3.visibility = INVISIBLE
        linearLayout4.visibility = INVISIBLE
        linearLayout5.visibility = INVISIBLE
        linearLayout6.visibility = INVISIBLE
        editTextQR.requestFocus()

        textViewDeliveryDate.setOnClickListener {
            if(!DatabaseHandler.docExists){
                datePicker()
                textViewDeliveryDate.text = selectedDate
            }
        }

        textViewPlanningDate.setOnClickListener {
            if(!DatabaseHandler.docExists){
                datePicker()
                textViewPlanningDate.text = selectedDate
            }
        }


        editTextQR.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){

                if (editTextQR.text.toString() == "") {
                    Toast.makeText(this, "Please scan QR code", Toast.LENGTH_SHORT).show()
                } else {
                    if(editTextQR.text.toString().length<47){
                        Toast.makeText(this, "Please Scan Valid Barcode", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        linearLayout1.visibility = VISIBLE
                        linearLayout2.visibility = VISIBLE
                        linearLayout3.visibility = VISIBLE
                        linearLayout4.visibility = VISIBLE
                        linearLayout5.visibility = VISIBLE
                        linearLayout6.visibility = VISIBLE

                        qr = editTextQR.text.toString().replace(" ","")
                        if(qr.take(2)=="2/") {
                            println("TYPE 2")
                            qr = qr.drop(2)
                            val spl = qr.split("&").toTypedArray()
                            doc = spl[0].substring(0, 10)
                            doc_date = spl[0].substring(10, 18)
                            ship = spl[0].substring(18, 28).trimStart('0')
                            material = spl[0].substring(28, 46).trimStart('0')
                            qty = spl[0].substring(46, spl[0].indexOf("."))
                            unit = spl[0].takeLast(3)
                            finalMaterial = "$material-$qty-$unit"
                            println("$material    $qty  $unit")

                            db.checkDoc("2/$doc")
                            if (DatabaseHandler.docExists) {
                                setSpinner()
                                textViewDoc.text = "2/$doc"
                                textViewPlanningDate.text = doc_date
                                textViewDeliveryDate.text = delivery_date
                                textViewShip.text = ship
                                textViewMaterialList.text = finalMaterial
                                Toast.makeText(this, "Document Exists", Toast.LENGTH_SHORT).show()
                            } else {
                                setSpinner()
                                textViewDoc.text = "2/$doc"
                                planningDateFormatter()
                                textViewDeliveryDate.text = DeliverySetup.currentDate
                                textViewShip.text = ship

                                if (spl.size > 1) {
                                    for (i in 1..spl.size - 1) {
                                        material = spl[i].substring(0, 18).trimStart('0')
                                        qty = spl[i].substring(18, spl[i].indexOf("."))
                                        unit = spl[i].takeLast(3)
                                        finalMaterial = "$finalMaterial\n$material-$qty-$unit"
                                        println("$material    $qty  $unit")
                                    }
                                }
                                textViewMaterialList.text = finalMaterial
                                qr = "2/$qr"
                            }
                        }

                        if (qr.take(2) == "1/") {
                            println("TYPE 1")
                            qr = qr.drop(2)
                            val spl = qr.split("&").toTypedArray()
                            doc = spl[0].substring(0, 10)
                            doc_date = spl[0].substring(10, 18)
                            ship = spl[0].substring(18, 28).trimStart('0')
                            material = spl[0].substring(28, 46).trimStart('0')
                            qty = spl[0].substring(46, spl[0].indexOf("."))
                            unit = spl[0].takeLast(3)
                            finalMaterial = "$material-$qty-$unit"
                            println("$material    $qty  $unit")

                            db.checkDoc("1/$doc")
                            if (DatabaseHandler.docExists) {
                                setSpinner()
                                textViewDoc.text = "1/$doc"
                                textViewPlanningDate.text = doc_date
                                textViewDeliveryDate.text = delivery_date
                                textViewShip.text = ship
                                textViewMaterialList.text = finalMaterial
                                Toast.makeText(this, "Document Exists", Toast.LENGTH_SHORT).show()
                            } else {
                                setSpinner()
                                textViewDoc.text = "1/$doc"
                                planningDateFormatter()
                                textViewDeliveryDate.text = DeliverySetup.currentDate
                                textViewShip.text = ship

                                println(spl.size)
                                if (spl.size > 1) {
                                    for (i in 1..spl.size - 1) {
                                        material = spl[i].substring(0, 18).trimStart('0')
                                        qty = spl[i].substring(18, spl[i].indexOf("."))
                                        unit = spl[i].takeLast(3)
                                        finalMaterial = "$finalMaterial\n$material-$qty-$unit"
                                        println("$material    $qty  $unit")
                                    }
                                }
                                textViewMaterialList.text = finalMaterial
                                qr = "1/$qr"
                            }
                        }

                        if (qr.take(2) != "2/" && qr.take(2) != "1/") {
                            println("TYPE 1")
                            val spl = qr.split("&").toTypedArray()
                            doc = spl[0].substring(0, 10)
                            doc_date = spl[0].substring(10, 18)
                            ship = spl[0].substring(18, 28).trimStart('0')
                            material = spl[0].substring(28, 46).trimStart('0')
                            qty = spl[0].substring(46, spl[0].indexOf("."))
                            unit = spl[0].takeLast(3)
                            finalMaterial = "$material-$qty-$unit"
                            println("$material    $qty  $unit")

                            db.checkDoc("1/$doc")
                            if (DatabaseHandler.docExists) {
                                setSpinner()
                                textViewDoc.text = "1/$doc"
                                textViewPlanningDate.text = doc_date
                                textViewDeliveryDate.text = delivery_date
                                textViewShip.text = ship
                                textViewMaterialList.text = finalMaterial
                                Toast.makeText(this, "Document Exists", Toast.LENGTH_SHORT).show()
                            } else {
                                setSpinner()
                                textViewDoc.text = "1/$doc"
                                planningDateFormatter()
                                textViewDeliveryDate.text = DeliverySetup.currentDate
                                textViewShip.text = ship

                                println(spl.size)
                                if (spl.size > 1) {
                                    for (i in 1..spl.size - 1) {
                                        material = spl[i].substring(0, 18).trimStart('0')
                                        qty = spl[i].substring(18, spl[i].indexOf("."))
                                        unit = spl[i].takeLast(3)
                                        finalMaterial = "$finalMaterial\n$material-$qty-$unit"
                                        println("$material    $qty  $unit")
                                    }
                                }
                                textViewMaterialList.text = finalMaterial
                                qr = "1/$qr"
                            }
                        }
                    }
                    editTextQR.text.clear()
                }
            }

            false
        })

        floatingActionButton.setOnClickListener {
            if(textViewDoc.text.toString() == ""){
                Toast.makeText(this,"Please Scan QR Code",Toast.LENGTH_SHORT).show()
            }
            else{
                linearLayout1.visibility = INVISIBLE
                linearLayout2.visibility = INVISIBLE
                linearLayout3.visibility = INVISIBLE
                linearLayout4.visibility = INVISIBLE
                linearLayout5.visibility = INVISIBLE
                linearLayout6.visibility = INVISIBLE

                splittedDocument = textViewDoc.text.toString()
                if(DatabaseHandler.docExists){
                    AsyncGetSerial(this,textViewDoc.text.toString()).execute()
                }
                else{
//                    db.addDoc(textViewDoc.text.toString(), spinnerRoute.selectedItem.toString(), textViewPlanningDate.text.toString(), finalMaterial.replace("\n","/"), spinnerRound.selectedItem.toString(), spinnerLicense.selectedItem.toString(),
//                        textViewDeliveryDate.text.toString(), spinnerStoreCode.selectedItem.toString(), spinnerStoreType.selectedItem.toString(), textViewShip.text.toString(),
//                        spinnerTank.selectedItem.toString())
                    textViewItems.text = "Delivery : ${DatabaseHandler.deliveryItems} item(s)"
                    AsyncGetSerial(this,textViewDoc.text.toString()).execute()
                }
            }
        }


    }

    fun setSpinner(){
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RouteList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoute.adapter = arrayAdapter

        val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RoundList)
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRound.adapter = arrayAdapter1

        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CarList)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLicense.adapter = arrayAdapter2

        val arrayAdapter3: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StoreTypeList)
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStoreType.adapter = arrayAdapter3

        val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StoreCodeList)
        arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStoreCode.adapter = arrayAdapter4

        val arrayAdapter5: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TankList)
        arrayAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTank.adapter = arrayAdapter5
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

                    selectedDate = date_format.format(dateObj)

                },
                year,
                month,
                day
        )
        dpd.show()
    }

    fun planningDateFormatter(){
        var formattedDate = doc_date.substring(0,4)+"-"+ doc_date.substring(4,6)+"-"+ doc_date.substring(6,8)
        textViewPlanningDate.text = formattedDate
    }

    private class AsyncGetSerial(val context: Context,val doc:String) : AsyncTask<String, String, String>() {
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
            editTextQR.text.clear()
            textViewDoc.text = ""
            val intent = Intent(context, DeliverySerial::class.java)
            context.startActivity(intent)
            super.onPostExecute(result)
        }
    }
}