package com.example.geomomentdiary

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class PhotoActivity : AppCompatActivity() {

    private lateinit var imgFull: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)

        imgFull = findViewById(R.id.imgFull)

        val foto = intent.getStringExtra("foto")

        if (!foto.isNullOrEmpty()) {
            imgFull.setImageURI(Uri.parse(foto))
        }

        imgFull.setOnClickListener {
            finish()
        }
    }
}