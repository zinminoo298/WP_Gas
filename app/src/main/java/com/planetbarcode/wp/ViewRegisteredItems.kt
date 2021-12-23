package com.planetbarcode.wp

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.planetbarcode.wp.Adapter.ViewRegisterAdapter
import com.planetbarcode.wp.DatabaseHandler.DatabaseHandler
import com.planetbarcode.wp.Model.ViewRegisterModel

class ViewRegisteredItems : AppCompatActivity() {

    companion object{
        lateinit var textViewRecord: TextView
        private lateinit var searchView: SearchView
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_registered_items)

        textViewRecord = findViewById(R.id.txt_records)
        searchView = findViewById(R.id.search_view)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewRegisterAdapter(DatabaseHandler.ViewRegisterItems, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        var list = DatabaseHandler.ViewRegisterItems
        textViewRecord.text ="Total ${ DatabaseHandler.registeredItems.toString() } records"


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // collapse the view ?
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // search goes here !!
                var filteredList = ArrayList<ViewRegisterModel>()
                if (newText != "") {
                    for (item in list) {
                        if (item.qr!!.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(item)
                        }
                    }
                    setupRecyclerView(filteredList)
                } else {
                    setupRecyclerView(list)
                }
                return false
            }
        })
    }
    private fun setupRecyclerView(list: ArrayList<ViewRegisterModel>) {
        viewAdapter = ViewRegisterAdapter(list, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewAdapter = ViewRegisterAdapter(DatabaseHandler.ViewRegisterItems, this)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onBackPressed() {
        AsyncTaskRunner(this).execute()
        super.onBackPressed()
    }

    private class AsyncTaskRunner(val context: Context) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db:DatabaseHandler

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context)
            db.getRegisteredItems()

            return DatabaseHandler.registeredItems.toString()
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setTitle("Loading")
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