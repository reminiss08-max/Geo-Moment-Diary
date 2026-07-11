package com.example.geomomentdiary

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geomomentdiary.adapter.MomentAdapter
import com.example.geomomentdiary.database.DatabaseHelper
import com.example.geomomentdiary.model.Moment
import java.util.Locale

class LihatCatatanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: MomentAdapter

    private val listMoment = ArrayList<Moment>()
    private val listSemuaMoment = ArrayList<Moment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_catatan)

        recyclerView = findViewById(R.id.rvMoment)
        searchView = findViewById(R.id.searchView)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)

        loadData()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterData(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText ?: "")
                return true
            }
        })
    }

    private fun loadData() {

        listMoment.clear()
        listSemuaMoment.clear()

        val cursor: Cursor = databaseHelper.getAllMoment()

        if (cursor.moveToFirst()) {

            do {

                val moment = Moment(
                    id = cursor.getInt(0),
                    judul = cursor.getString(1),
                    catatan = cursor.getString(2),
                    tanggal = cursor.getString(3),
                    foto = cursor.getString(4),
                    latitude = cursor.getDouble(5),
                    longitude = cursor.getDouble(6)
                )

                listMoment.add(moment)
                listSemuaMoment.add(moment)

            } while (cursor.moveToNext())
        }

        cursor.close()

        adapter = MomentAdapter(listMoment) { moment ->

            val intent = Intent(this, DetailActivity::class.java)

            intent.putExtra("id", moment.id)
            intent.putExtra("judul", moment.judul)
            intent.putExtra("catatan", moment.catatan)
            intent.putExtra("tanggal", moment.tanggal)
            intent.putExtra("foto", moment.foto)
            intent.putExtra("latitude", moment.latitude)
            intent.putExtra("longitude", moment.longitude)

            startActivity(intent)

        }

        recyclerView.adapter = adapter

        cekDataKosong()
    }

    private fun filterData(keyword: String) {

        listMoment.clear()

        if (keyword.isEmpty()) {

            listMoment.addAll(listSemuaMoment)

        } else {

            for (moment in listSemuaMoment) {

                if (moment.judul.lowercase(Locale.getDefault())
                        .contains(keyword.lowercase(Locale.getDefault()))
                ) {

                    listMoment.add(moment)

                }

            }

        }

        adapter.notifyDataSetChanged()

        cekDataKosong()
    }

    private fun cekDataKosong() {

        if (listMoment.isEmpty()) {

            layoutEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            layoutEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}