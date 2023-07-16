package com.pnj.bus_ticket_booking.admin

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
import java.io.File

class TicketAdapter(private val ticketList: ArrayList<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {
    private lateinit var activity: AppCompatActivity

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama_bus: TextView = itemView.findViewById(R.id.TVLNamaBus)
        val harga: TextView = itemView.findViewById(R.id.TVLHarga)
        val tujuan: TextView = itemView.findViewById(R.id.TVLTujuan)
        val tanggal_keberangkatan: TextView = itemView.findViewById(R.id.TVLTanggalKeberangkatan)
        val img_bus : ImageView = itemView.findViewById(R.id.IMLGambarBus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_list_layout, parent, false)
        return TicketViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket: Ticket = ticketList[position]
        holder.nama_bus.text = "Bus: " + ticket.nama_bus
        holder.harga.text = "Harga: " +ticket.harga
        holder.tujuan.text = "Tujuan: " + ticket.tujuan
        holder.tanggal_keberangkatan.text = "Jadwal: " + ticket.tanggal_keberangkatan

        val storageRef = FirebaseStorage.getInstance().reference.child(ticket.gambar_bus.toString())
        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.img_bus.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }

        holder.itemView.setOnClickListener{
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, EditTicketActivity::class.java).apply {
                putExtra("nama_bus", ticket.nama_bus.toString())
                putExtra("tanggal_keberangkatan", ticket.tanggal_keberangkatan.toString())
                putExtra("jumlah_kursi", ticket.jumlah_kursi.toString())
                putExtra("harga", ticket.harga.toString())
                putExtra("tujuan", ticket.tujuan.toString())
                putExtra("gambar_bus", ticket.gambar_bus.toString())
            })
        }
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }
}