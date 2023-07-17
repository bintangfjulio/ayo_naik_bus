package com.pnj.bus_ticket_booking.pemesan

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.pnj.bus_ticket_booking.R
import java.io.File

class BookingAdapter(private val bookingList: ArrayList<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama_tiket: TextView = itemView.findViewById(R.id.TVLNamaTiket)
        val nama_pemilik: TextView = itemView.findViewById(R.id.TVLNamaPemilik)
        val waktu_keberangkatan: TextView = itemView.findViewById(R.id.TVLWaktuKeberangkatan)
        val img_booking : ImageView = itemView.findViewById(R.id.IMLGambarBus)
        val jumlah_tiket: TextView = itemView.findViewById(R.id.TVLJumlahTiket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_list_layout, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking: Booking = bookingList[position]
        holder.nama_tiket.text = "Bus: " + booking.nama_tiket
        holder.nama_pemilik.text = "Pemesan: " + booking.nama_pemesan
        holder.waktu_keberangkatan.text = "Jadwal: " + booking.tanggal_keberangkatan
        holder.jumlah_tiket.text = "Jumlah: " + booking.jumlah_tiket

        val storageRef = FirebaseStorage.getInstance().reference.child(booking.gambar_bus.toString())
        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img_booking.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}