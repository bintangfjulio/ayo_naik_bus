package com.pnj.bus_ticket_booking.pemesan

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.pnj.bus_ticket_booking.R
import com.pnj.bus_ticket_booking.admin.Ticket
import java.io.File

class CatalogAdapter (private val catalogList: ArrayList<Catalog>) :
RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {
    //inisiasi variabel
    private lateinit var activity: AppCompatActivity

    class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //referensi elemen yang terdaoat pada layout ticket list laout
        val nama_bus: TextView = itemView.findViewById(R.id.TVLNamaBus)
        val harga: TextView = itemView.findViewById(R.id.TVLHarga)
        val tujuan: TextView = itemView.findViewById(R.id.TVLTujuan)
        val tanggal_keberangkatan: TextView = itemView.findViewById(R.id.TVLTanggalKeberangkatan)
        val img_bus : ImageView = itemView.findViewById(R.id.IMLGambarBus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                //nflate layout item tiket (ticket_list_layout)
            .inflate(R.layout.ticket_list_layout, parent, false)
        //kembalikan
        return CatalogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        //mengambil data dari catalog List
        val catalog: Catalog = catalogList[position]
        holder.nama_bus.text = "Bus: " + catalog.nama_bus
        holder.harga.text = "Harga: " +catalog.harga
        holder.tujuan.text = "Tujuan: " + catalog.tujuan
        holder.tanggal_keberangkatan.text = "Jadwal: " + catalog.tanggal_keberangkatan

        val storageRef = FirebaseStorage.getInstance().reference.child(catalog.gambar_bus.toString())
        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img_bus.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }

        holder.itemView.setOnClickListener{
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, AddBookingActivity::class.java).apply {
                putExtra("nama_tiket", catalog.nama_bus.toString())
                putExtra("tanggal_keberangkatan", catalog.tanggal_keberangkatan.toString())
                putExtra("gambar_bus", catalog.gambar_bus.toString())
            })
        }
    }

    override fun getItemCount(): Int {
        return catalogList.size
    }
}