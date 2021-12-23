package com.planetbarcode.wp

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class AlertDialog(private val context: Context, val inflater: LayoutInflater, val header:String, val title:String) {
    lateinit var dialog:AlertDialog
    lateinit var buttonOk: Button
    lateinit var textViewHeader: TextView
    lateinit var textViewTitle: TextView

    fun errorDialog(){
        val builder= AlertDialog.Builder(context)
        val view=inflater.inflate(R.layout.error_dialog, null)
        builder.setView(view)
        dialog =builder.create()
        dialog.show()
        dialog.setCancelable(false)
        buttonOk = view.findViewById(R.id.btn_yes)
        textViewHeader = view.findViewById(R.id.txt_header)
        textViewTitle = view.findViewById(R.id.txt_title)

        textViewHeader.text = header
        textViewTitle.text = title

        buttonOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)

    }
}