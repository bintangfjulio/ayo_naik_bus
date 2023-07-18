package com.pnj.bus_ticket_booking.pemesan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.bus_ticket_booking.databinding.ActivityAddBookingBinding
import java.util.*

class AddBookingActivity : AppCompatActivity() {
    //inisiasi variabel global
    private lateinit var binding: ActivityAddBookingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    //method dari appcompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        //menentukan aksi dari button addBooking
        binding.BtnAddBooking.setOnClickListener {
            addBooking()
        }
    }

    //mengambil data dari inputan pengguna
    fun addBooking() {
        var nama_pemesan: String = binding.txtNamaPemesan.text.toString()
        var jumlah_tiket: String = binding.txtJumlahTiket.text.toString()

        //memanggil data dari tiket activity
        val intent = intent
        val nama_tiket = intent.getStringExtra("nama_tiket").toString()
        val tanggal_keberangkatan = intent.getStringExtra("tanggal_keberangkatan").toString()
        val gambar_bus = intent.getStringExtra("gambar_bus").toString()

        //disimpan melalui objek booking
        //mutablemap
        val booking: MutableMap<String, Any> = HashMap()
        booking["nama_pemesan"] = nama_pemesan
        booking["jumlah_tiket"] = jumlah_tiket
        booking["nama_tiket"] = nama_tiket
        booking["tanggal_keberangkatan"] = tanggal_keberangkatan
        booking["gambar_bus"] = gambar_bus

        val user = firebaseAuth.currentUser
        val uid = user?.uid

        firestoreDatabase.collection("user").document(uid.toString()).collection("booking").add(booking)
            .addOnSuccessListener {
                val intentMain = Intent(this, CatalogActivity::class.java)
                startActivity(intentMain)
            }
    }
}