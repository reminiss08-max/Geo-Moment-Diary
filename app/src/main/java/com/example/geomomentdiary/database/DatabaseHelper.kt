package com.example.geomomentdiary.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.geomomentdiary.model.Moment

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "GeoMomentDiary.db"
        const val DATABASE_VERSION = 1

        const val TABLE_MOMENT = "moment"

        const val COL_ID = "id"
        const val COL_JUDUL = "judul"
        const val COL_CATATAN = "catatan"
        const val COL_TANGGAL = "tanggal"
        const val COL_FOTO = "foto"
        const val COL_LATITUDE = "latitude"
        const val COL_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val query = """
            CREATE TABLE $TABLE_MOMENT(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_JUDUL TEXT,
                $COL_CATATAN TEXT,
                $COL_TANGGAL TEXT,
                $COL_FOTO TEXT,
                $COL_LATITUDE REAL,
                $COL_LONGITUDE REAL
            )
        """.trimIndent()

        db.execSQL(query)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOMENT")
        onCreate(db)
    }

    // ==========================
    // INSERT
    // ==========================

    fun insertMoment(
        judul: String,
        catatan: String,
        tanggal: String,
        foto: String,
        latitude: Double,
        longitude: Double
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_JUDUL, judul)
            put(COL_CATATAN, catatan)
            put(COL_TANGGAL, tanggal)
            put(COL_FOTO, foto)
            put(COL_LATITUDE, latitude)
            put(COL_LONGITUDE, longitude)
        }

        val result = db.insert(TABLE_MOMENT, null, values)

        db.close()

        return result != -1L
    }

    // ==========================
    // READ ALL
    // ==========================

    fun getAllMoment(): Cursor {

        val db = readableDatabase

        return db.rawQuery(
            "SELECT * FROM $TABLE_MOMENT ORDER BY $COL_ID DESC",
            null
        )
    }

    // ==========================
    // READ BY ID
    // ==========================

    fun getMomentById(id: Int): Moment? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MOMENT WHERE $COL_ID=?",
            arrayOf(id.toString())
        )

        var moment: Moment? = null

        if (cursor.moveToFirst()) {

            moment = Moment(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                judul = cursor.getString(cursor.getColumnIndexOrThrow(COL_JUDUL)),
                catatan = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATATAN)),
                tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL)),
                foto = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE))
            )
        }

        cursor.close()
        db.close()

        return moment
    }

    // ==========================
    // UPDATE
    // ==========================

    fun updateMoment(
        id: Int,
        judul: String,
        catatan: String
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_JUDUL, judul)
            put(COL_CATATAN, catatan)
        }

        val result = db.update(
            TABLE_MOMENT,
            values,
            "$COL_ID=?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }

    // ==========================
    // DELETE
    // ==========================

    fun deleteMoment(id: Int): Boolean {

        val db = writableDatabase

        val result = db.delete(
            TABLE_MOMENT,
            "$COL_ID=?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }
}