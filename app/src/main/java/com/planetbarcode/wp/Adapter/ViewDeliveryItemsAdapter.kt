package com.planetbarcode.wp.Adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.*
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.Model.ViewDeliveryItemsModel
import com.planetbarcode.wp.Model.ViewRegisterModel

class ViewDeliveryItemsAdapter(private var Dataset: ArrayList<ViewDeliveryItemsModel>, private val context: Context) :
    RecyclerView.Adapter<ViewDeliveryItemsAdapter.MyViewHolder>() {

    companion object{
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var textViewTitle: TextView
        internal lateinit var dialog: AlertDialog
        lateinit var db: DatabaseHandler
        private var progressBar1: ProgressBar? =null
        var currentDoc = ""
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtDoc = itemView.findViewById<TextView>(R.id.txt_doc)
        val txtItems = itemView.findViewById<TextView>(R.id.txt_items)
        val txtStore = itemView.findViewById<TextView>(R.id.txt_store)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_delivery_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtDoc = holder.txtDoc
        val txtItems = holder.txtItems
        val txtStore = holder.txtStore

        txtDoc.text = Dataset[position].doc
        txtItems.text= Dataset[position].items
        txtStore.text = "${Dataset[position].store_type} / ${Dataset[position].store_code}"

        db = DatabaseHandler(context)
        holder.view.setOnClickListener {
            AsyncLoadData(context,Dataset,position).execute()
        }

        holder.view.setOnLongClickListener {
            deleteDialog(position)
            true
        }
    }

    private fun deleteDialog(position: Int){
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete_dialog, null)
        builder.setView(view)
        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)
        textViewTitle =  view.findViewById(R.id.txt_title)
        textViewTitle.text = "Delete Document?"
        dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)

        buttonYes.setOnClickListener{
            db.deleteDocument(Dataset[position].doc!!)
            Dataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,Dataset.size)
            ViewDeliveryItems.textViewRecord.text ="Total ${Dataset.size} records"
            dialog.dismiss()
            Toast.makeText(context,"QR Delete Complete",Toast.LENGTH_SHORT).show()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }


    private class AsyncLoadData(val context: Context?,var Dataset: ArrayList<ViewDeliveryItemsModel>, val position: Int) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {
            backGroundJob()
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
            if(DatabaseHandler.docExists){
                val intent = Intent(context, EditDelivery::class.java)
                context!!.startActivity(intent)
            }
            else{
                Toast.makeText(context,"Document does not Exists",Toast.LENGTH_SHORT).show()
            }
            super.onPostExecute(result)
        }

        fun backGroundJob(){
            db.getCarCode("Select Vehicle Code")
            db.getFillingPlant("Select Filling Plant","-")
            db.getMaintenanceCompany("Select Maintenance Company","-")
            db.getStoreName()
            db.getMaterail("Select Material Code")
            db.checkDoc(Dataset[position].doc!!)
            currentDoc = Dataset[position].doc!!.drop(2)
        }
    }
    override fun getItemCount() = Dataset.size
}