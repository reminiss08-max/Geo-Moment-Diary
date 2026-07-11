package com.example.geomomentdiary

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geomomentdiary.database.DatabaseHelper

class EditMomentActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etCatatan: EditText
    private lateinit var btnSimpan: Button

    private lateinit var databaseHelper: DatabaseHelper

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_moment)

        etJudul = findViewById(R.id.etJudul)
        etCatatan = findViewById(R.id.etCatatan)
        btnSimpan = findViewById(R.id.btnSimpan)

        databaseHelper = DatabaseHelper(this)

        // Ambil id dari DetailActivity
        id = intent.getIntExtra("id", 0)

        // Ambil data dari database
        val moment = databaseHelper.getMomentById(id)

        if (moment != null) {
            etJudul.setText(moment.judul)
            etCatatan.setText(moment.catatan)
        }

        btnSimpan.setOnClickListener {

            val judul = etJudul.text.toString().trim()
            val catatan = etCatatan.text.toString().trim()

            if (judul.isEmpty() || catatan.isEmpty()) {

                Toast.makeText(
                    this,
                    "Lengkapi data terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val berhasil = databaseHelper.updateMoment(
                id,
                judul,
                catatan
            )

            if (berhasil) {

                Toast.makeText(
                    this,
                    "Momen berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Gagal memperbarui momen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}