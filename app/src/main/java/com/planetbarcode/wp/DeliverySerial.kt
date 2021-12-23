package com.planetbarcode.wp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.Adapter.ViewDeliverySerialAdapter
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler


class DeliverySerial : AppCompatActivity() {
    companion object{
        lateinit var textViewDoc: TextView
        lateinit var textViewTotalSerial: TextView
        lateinit var editTextSerial: EditText
        lateinit var button: Button
        lateinit var dialog:AlertDialog
        lateinit var buttonOk: Button
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
        lateinit var db:DatabaseHandler
        var totalQty = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_serial)

        totalQty = 0
        textViewDoc = findViewById(R.id.txt_doc)
        textViewTotalSerial = findViewById(R.id.txt_total_serial)
        editTextSerial = findViewById(R.id.edt_serial)
        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewDeliverySerialAdapter(DatabaseHandler.ViewDeleveryItems, this)
        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        db = DatabaseHandler(this)
        editTextSerial.requestFocus()
        for(items in 0 until DeliveryTest.MaterialListArray.size){
            totalQty += DeliveryTest.MaterialListArray[items].qty!!
        }


        textViewDoc.text = DeliveryTest.fullDoc
        textViewTotalSerial.text ="Total : $totalQty / ${ DatabaseHandler.ViewDeleveryItems.size } cylinders(s)"

        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        editTextSerial.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if(editTextSerial.text.isEmpty()){
                    editTextSerial.nextFocusDownId = editTextSerial.id
                    editTextSerial.requestFocus()
                    Toast.makeText(this,"Please Enter Cylinder QR",Toast.LENGTH_SHORT).show()
                }
                else{
                    db.addSerial(DeliveryTest.fullDoc, editTextSerial.text.toString())
                    viewAdapter.notifyDataSetChanged()
                    textViewTotalSerial.text = "Total : $totalQty / ${DatabaseHandler.ViewDeleveryItems.size} item(s)"
                    editTextSerial.text.clear()
                    editTextSerial.nextFocusDownId = editTextSerial.id
                    editTextSerial.requestFocus()
                }
            }
            false
        })
    }

    override fun onBackPressed() {
        if(totalQty != DatabaseHandler.ViewDeleveryItems.size){
            Toast.makeText(this, "Cylinder Quantity Not Match",Toast.LENGTH_SHORT).show()
            com.planetbarcode.wp.AlertDialog(this,layoutInflater,"Warning!", "Cylinder Quantity Not Match").errorDialog()
        }
        else{
            if(DeliveryTest.delivery){
                val myIntent = Intent(this, DeliveryTest::class.java)
                myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                this.startActivity(myIntent)
            }
            else{
                super.onBackPressed()
            }
        }

    }
}