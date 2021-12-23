package com.planetbarcode.wp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.DeliverySerial
import com.planetbarcode.wp.Model.ViewDeliveryModel
import com.planetbarcode.wp.R

class ViewDeliverySerialAdapter(private var Dataset: ArrayList<ViewDeliveryModel>, private val context: Context) :
        RecyclerView.Adapter<ViewDeliverySerialAdapter.MyViewHolder>() {

    companion object{
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var dialog: AlertDialog
        internal lateinit var db:DatabaseHandler
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtSerial = itemView.findViewById<TextView>(R.id.txt_serial)
        val imgDelete = itemView.findViewById<ImageView>(R.id.img_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_delivery_serial_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtSerial = holder.txtSerial
        val imgDelete = holder.imgDelete

        txtSerial.text= Dataset[position].serial

        imgDelete.setOnClickListener {
            deleteDialog(Dataset[position].doc!!, Dataset[position].serial!!,position)
        }
    }

    fun deleteDialog(doc:String, serial:String, position: Int){
        db = DatabaseHandler(context)
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete_dialog, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("Are you sure?")
        dialog.show()
        dialog.setCancelable(false)

        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener {
            db.deleteSerial(doc,serial)
            Dataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,Dataset.size)
            DeliverySerial.textViewTotalSerial.text ="Total : ${DeliverySerial.totalQty} / ${ Dataset.size } item(s)"
            dialog.dismiss()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }
    override fun getItemCount() = Dataset.size
}