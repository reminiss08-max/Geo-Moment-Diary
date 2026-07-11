package com.example.geomomentdiary

import java.io.File
import java.io.FileOutputStream
import com.example.geomomentdiary.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.net.Uri
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class TambahMomenActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etCatatan: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnCamera: Button
    private lateinit var btnLokasi: Button


    private lateinit var imgPreview: ImageView
    private lateinit var tvLokasi: TextView

    private lateinit var databaseHelper: DatabaseHelper


    private var latitude = 0.0
    private var longitude = 0.0

    private var fotoPath = ""

    // GPS
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }


    // Launcher Kamera
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val bitmap = result.data?.extras?.get("data") as? Bitmap

                bitmap?.let {

                    imgPreview.setImageBitmap(it)

                    fotoPath = simpanFotoInternal(it)

                    Toast.makeText(
                        this,
                        "Foto berhasil diambil 📷",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }

    // Permission Kamera
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                bukaKamera()
            } else {
                Toast.makeText(
                    this,
                    "Izin kamera ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    // Permission Lokasi
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                ambilLokasi()
            } else {
                Toast.makeText(
                    this,
                    "Izin lokasi ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_momen)

        etJudul = findViewById(R.id.etJudul)
        etCatatan = findViewById(R.id.etCatatan)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnCamera = findViewById(R.id.btnCamera)
        btnLokasi = findViewById(R.id.btnLokasi)
        imgPreview = findViewById(R.id.imgPreview)
        tvLokasi = findViewById(R.id.tvLokasi)
        databaseHelper = DatabaseHelper(this)

        // Tombol Kamera
        btnCamera.setOnClickListener {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                bukaKamera()

            } else {

                cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )

            }

        }

        // Tombol Lokasi
        btnLokasi.setOnClickListener {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                ambilLokasi()

            } else {

                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            }

        }


        // Tombol Simpan
        btnSimpan.setOnClickListener {

            val judul = etJudul.text.toString().trim()
            val cerita = etCatatan.text.toString().trim()

            if (judul.isEmpty() || cerita.isEmpty()) {

                Toast.makeText(
                    this,
                    "Lengkapi data terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val tanggal = SimpleDateFormat(
                "dd MMMM yyyy HH:mm",
                Locale("id", "ID")
            ).format(Date())

            val berhasil = databaseHelper.insertMoment(
                judul,
                cerita,
                tanggal,
                fotoPath,
                latitude,
                longitude
            )

            if (berhasil) {

                Toast.makeText(
                    this,
                    "Momen berhasil disimpan 🌸",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Gagal menyimpan momen",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    // Membuka Kamera
    private fun bukaKamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {

            cameraLauncher.launch(intent)

        } else {

            Toast.makeText(
                this,
                "Aplikasi kamera tidak tersedia",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    // Mengambil Lokasi
    @SuppressLint("MissingPermission")
    private fun ambilLokasi() {

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .addOnSuccessListener { location ->

                if (location != null) {

                    latitude = location.latitude
                    longitude = location.longitude

                    tvLokasi.text =
                        "📍 Latitude : $latitude\n📍 Longitude : $longitude"

                    Toast.makeText(
                        this,
                        "Lokasi berhasil diambil",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Lokasi tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()


                }


            }
    }

    private fun simpanFotoInternal(bitmap: Bitmap): String {

        val namaFile = "IMG_${System.currentTimeMillis()}.jpg"

        val file = java.io.File(filesDir, namaFile)

        val output = java.io.FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)

        output.flush()
        output.close()

        return file.absolutePath
    }
}