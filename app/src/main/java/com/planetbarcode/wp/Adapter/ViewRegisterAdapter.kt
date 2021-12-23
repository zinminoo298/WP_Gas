package com.planetbarcode.wp.Adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.EditRegisterItem
import com.planetbarcode.wp.Model.ViewRegisterModel
import com.planetbarcode.wp.R
import com.planetbarcode.wp.ViewRegisteredItems
import java.io.File

class ViewRegisterAdapter(private var Dataset: ArrayList<ViewRegisterModel>, private val context: Context) :
        RecyclerView.Adapter<ViewRegisterAdapter.MyViewHolder>() {

    companion object{
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var dialog: AlertDialog
        lateinit var db:DatabaseHandler
        private var progressBar1: ProgressBar? =null
        var editQRArray = ArrayList<String>()
        var rotatedBitmap: Bitmap? = null
        var storeName = ""
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtQR = itemView.findViewById<TextView>(R.id.txt_qr)
        val txtSerial = itemView.findViewById<TextView>(R.id.txt_serial)
        val txtStore = itemView.findViewById<TextView>(R.id.txt_store)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_register_rowview, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtQR = holder.txtQR
        val txtSerial = holder.txtSerial
        val txtStore = holder.txtStore

        txtQR.text = Dataset[position].qr
        txtSerial.text= Dataset[position].serial
        txtStore.text = "${Dataset[position].storetype} / ${Dataset[position].storecode}"

        db= DatabaseHandler(context)
        holder.view.setOnLongClickListener {
            deleteDialog(position)
            true
        }

        holder.view.setOnClickListener {
            AsyncClearData(context,Dataset,position).execute()
        }


    }

    private fun deleteDialog(position: Int){
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete_dialog, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)

        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener{
            db.deleteRegisterQR(Dataset[position].qr!!)
            Dataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,Dataset.size)
            ViewRegisteredItems.textViewRecord.text ="Total ${Dataset.size} records"
            dialog.dismiss()
            Toast.makeText(context,"QR Delete Complete",Toast.LENGTH_SHORT).show()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    private class AsyncClearData(val context: Context?,var Dataset: ArrayList<ViewRegisterModel>, val position: Int) : AsyncTask<String, String, String>() {
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
            EditRegisterItem.arrayAdapter3 = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, EditRegisterItem.sampleCompany)
            val intent = Intent(context,EditRegisterItem::class.java)
            context!!.startActivity(intent)
            super.onPostExecute(result)
        }

        fun backGroundJob(){
            editQRArray.clear()
            editQRArray.add(Dataset[position].qr!!)
            editQRArray.add(Dataset[position].serial!!)
            if(Dataset[position].storetype == "a"){
                editQRArray.add("A-โรงซ่อม")

            }
            if(Dataset[position].storetype == "b"){
                editQRArray.add("B-โรงบรรจุ")

            }
            if(Dataset[position].storetype == "c"){
                editQRArray.add("C-ลูกค้า")

            }
            editQRArray.add(Dataset[position].storecode!!)
            editQRArray.add(Dataset[position].material!!)
            editQRArray.add(Dataset[position].master!!)
            editQRArray.add(Dataset[position].date!!)
            editQRArray.add(Dataset[position].inspector!!)
            editQRArray.add(Dataset[position].inspection_date!!)
            editQRArray.add(Dataset[position].fa!!)
            db.getMaterail(Dataset[position].material!!)
            db.getManufacturer(Dataset[position].master!!)
            db.getInspector(Dataset[position].inspector!!)



            if(Dataset[position].storetype == "a"){
                db.getMaintenanceCompany(Dataset[position].storecode!!,"-")
                db.getFillingPlant("Select Filling Plant","-")
                db.getStoreName()
            }
            if(Dataset[position].storetype == "b"){
                db.getMaintenanceCompany("Select Maintenance Company","-")
                db.getFillingPlant(Dataset[position].storecode!!,"-")
                db.getStoreName()
            }
            if(Dataset[position].storetype == "c"){
                db.getMaintenanceCompany("Select Maintenance Company","-")
                db.getFillingPlant("Select Filling Plant","-")
                db.getStoreName()
                println(DatabaseHandler.MaintenanceArray.size)
                println(DatabaseHandler.FillingPlantArray.size)
                var list = DatabaseHandler.StoreNameArray
                    try {
                        for (item in list) {
                            if (item.code == "${editQRArray[3]}") {
                                storeName = item.customer!!
                                break
                            }
                            else{
                                storeName = "Not found"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }

            val imgFile = File("/sdcard/Download/WP/Pictures/${ViewRegisterAdapter.editQRArray[0]}.jpg")
            var bitmap: Bitmap
            if(imgFile.exists()){
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                val rotationMatrix = Matrix()
                if (bitmap.getWidth() >= bitmap .getHeight()) {
                    rotationMatrix.setRotate(90F)
                } else {
                    rotationMatrix.setRotate(0F)
                }

                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotationMatrix, true)

            }
            else{
                rotatedBitmap = null
            }
        }
    }


    override fun getItemCount() = Dataset.size
}