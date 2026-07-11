package com.example.geomomentdiary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val tvKoordinat = findViewById<TextView>(R.id.tvKoordinat)
        val btnBukaMaps = findViewById<Button>(R.id.btnBukaMaps)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        tvKoordinat.text =
            "Latitude : $latitude\nLongitude : $longitude"

        btnBukaMaps.setOnClickListener {

            val uri = Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")

            val intent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(intent)
        }
    }
}