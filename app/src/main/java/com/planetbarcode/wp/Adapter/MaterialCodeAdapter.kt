package com.planetbarcode.wp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.Model.MaterialCodeModel
import com.planetbarcode.wp.R

class MaterialCodeAdapter(private var Dataset: ArrayList<MaterialCodeModel>, private val context: Context) :
        RecyclerView.Adapter<MaterialCodeAdapter.MyViewHolder>() {

    companion object{
        lateinit var ImageButtonDelete:ImageButton
    }
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtMaterialCode = itemView.findViewById<TextView>(R.id.txt_material_code)
        val txtQty = itemView.findViewById<TextView>(R.id.txt_qty)
        val txtUnit = itemView.findViewById<TextView>(R.id.txt_unit)
        val ImageButtonDelete = itemView.findViewById<ImageButton>(R.id.img_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.material_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtMaterialCode = holder.txtMaterialCode
        val txtQty = holder.txtQty
        val txtUnit = holder.txtUnit

        txtMaterialCode.text = Dataset[position].material_code
        txtQty.text= Dataset[position].qty.toString()
        txtUnit.text = Dataset[position].unit
        ImageButtonDelete = holder.ImageButtonDelete

        ImageButtonDelete.setOnClickListener {
            Dataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,Dataset.size)
        }

    }
    override fun getItemCount() = Dataset.size
}