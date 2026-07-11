package com.example.geomomentdiary

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geomomentdiary.database.DatabaseHelper
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DetailActivity : AppCompatActivity() {

    private lateinit var imgDetail: ImageView
    private lateinit var tvJudul: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvCatatan: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView

    private lateinit var mapView: MapView

    private lateinit var btnLihatPeta: Button
    private lateinit var btnEdit: Button
    private lateinit var btnHapus: Button

    private lateinit var databaseHelper: DatabaseHelper

    private var id = 0

    // Koordinat
    private var latitude = 0.0
    private var longitude = 0.0

    // Path foto
    private var fotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_detail)

        databaseHelper = DatabaseHelper(this)

        imgDetail = findViewById(R.id.imgDetail)
        tvJudul = findViewById(R.id.tvJudul)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvCatatan = findViewById(R.id.tvCatatan)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        mapView = findViewById(R.id.mapView)

        btnLihatPeta = findViewById(R.id.btnLihatPeta)
        btnEdit = findViewById(R.id.btnEdit)
        btnHapus = findViewById(R.id.btnHapus)

        id = intent.getIntExtra("id", 0)

        tampilkanData()

        // ==========================
        // Klik Foto -> Fullscreen
        // ==========================
        imgDetail.setOnClickListener {

            if (fotoPath.isNotEmpty()) {

                val intent = Intent(this, PhotoActivity::class.java)
                intent.putExtra("foto", fotoPath)
                startActivity(intent)

            }

        }

        // ==========================
        // Tombol Lihat Google Maps
        // ==========================
        btnLihatPeta.setOnClickListener {

            if (latitude == 0.0 && longitude == 0.0) {

                Toast.makeText(
                    this,
                    "Lokasi belum tersedia",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
                )
            }
        }

        // ==========================
        // Tombol Edit
        // ==========================
        btnEdit.setOnClickListener {

            val intent = Intent(this, EditMomentActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        }

        // ==========================
        // Tombol Hapus
        // ==========================
        btnHapus.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Hapus Momen")
                .setMessage("Apakah Anda yakin ingin menghapus momen ini?")
                .setPositiveButton("Hapus") { _, _ ->

                    val berhasil = databaseHelper.deleteMoment(id)

                    if (berhasil) {

                        Toast.makeText(
                            this,
                            "Momen berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Gagal menghapus momen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun tampilkanData() {

        val moment = databaseHelper.getMomentById(id) ?: return

        tvJudul.text = moment.judul
        tvTanggal.text = "📅 ${moment.tanggal}"
        tvCatatan.text = moment.catatan

        latitude = moment.latitude
        longitude = moment.longitude

        fotoPath = moment.foto

        tvLatitude.text = "📍 Latitude : $latitude"
        tvLongitude.text = "📍 Longitude : $longitude"

        if (fotoPath.isNotEmpty()) {
            imgDetail.setImageURI(Uri.parse(fotoPath))
        }

        mapView.overlays.clear()

        mapView.setMultiTouchControls(true)

        val lokasi = GeoPoint(latitude, longitude)

        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(lokasi)

        val marker = Marker(mapView)
        marker.position = lokasi
        marker.title = moment.judul

        mapView.overlays.add(marker)

        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        tampilkanData()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}