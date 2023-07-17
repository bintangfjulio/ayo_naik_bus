package com.pnj.bus_ticket_booking.pemesan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.bus_ticket_booking.R
import com.pnj.bus_ticket_booking.admin.TicketActivity
import com.pnj.bus_ticket_booking.databinding.ActivityAddBookingBinding
import com.pnj.bus_ticket_booking.databinding.ActivityAddTicketBinding
import java.text.SimpleDateFormat
import java.util.*

class AddBookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnAddBooking.setOnClickListener {
            addBooking()
        }
    }

    fun addBooking() {
        var nama_pemesan: String = binding.txtNamaPemesan.text.toString()
        var jumlah_tiket: String = binding.txtJumlahTiket.text.toString()

        val intent = intent
        val nama_tiket = intent.getStringExtra("nama_tiket").toString()
        val tanggal_keberangkatan = intent.getStringExtra("tanggal_keberangkatan").toString()
        val gambar_bus = intent.getStringExtra("gambar_bus").toString()

        val booking: MutableMap<String, Any> = HashMap()
        booking["nama_pemesan"] = nama_pemesan
        booking["jumlah_tiket"] = jumlah_tiket
        booking["nama_tiket"] = nama_tiket
        booking["tanggal_keberangkatan"] = tanggal_keberangkatan
        booking["gambar_bus"] = gambar_bus

        val user = firebaseAuth.currentUser
        val uid = user?.uid
        Log.e("Uid", uid!!)

        firestoreDatabase.collection("user").document(uid.toString()).collection("booking").add(booking)
            .addOnSuccessListener {
                val intentMain = Intent(this, CatalogActivity::class.java)
                startActivity(intentMain)
            }
    }
}