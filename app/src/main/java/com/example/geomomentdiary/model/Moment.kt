package com.example.geomomentdiary.model

data class Moment(

    val id: Int,

    val judul: String,

    val catatan: String,

    val tanggal: String,

    val foto: String,

    val latitude: Double,

    val longitude: Double

)