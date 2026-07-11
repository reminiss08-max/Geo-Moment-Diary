package com.example.geomomentdiary.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geomomentdiary.R
import com.example.geomomentdiary.model.Moment

class MomentAdapter(
    private val listMoment: ArrayList<Moment>,
    private val onItemClick: (Moment) -> Unit
) : RecyclerView.Adapter<MomentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgMoment: ImageView = itemView.findViewById(R.id.imgMoment)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_moment, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listMoment.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val moment = listMoment[position]

        holder.tvTitle.text = moment.judul
        holder.tvDescription.text = moment.catatan

        // ============================
        // Pisahkan tanggal dan jam
        // ============================

        val bagian = moment.tanggal.split(" ")

        if (bagian.size >= 4) {

            holder.tvDate.text =
                "📅 ${bagian[0]} ${bagian[1]} ${bagian[2]}"

            holder.tvTime.text =
                "🕒 ${bagian[3]} WIB"

        } else {

            holder.tvDate.text = "📅 ${moment.tanggal}"
            holder.tvTime.text = ""

        }

        // ============================
        // Status GPS
        // ============================

        if (moment.latitude != 0.0 && moment.longitude != 0.0) {

            holder.tvLocation.text =
                "📍 Lokasi GPS Tersimpan ✓"

        } else {

            holder.tvLocation.text =
                "📍 Lokasi belum tersedia"

        }

        // ============================
        // Foto
        // ============================

        if (moment.foto.isNotEmpty()) {

            holder.imgMoment.setImageURI(
                Uri.parse(moment.foto)
            )

        } else {

            holder.imgMoment.setImageResource(
                R.drawable.ic_photo_placeholder
            )

        }

        holder.itemView.setOnClickListener {
            onItemClick(moment)
        }

    }
}