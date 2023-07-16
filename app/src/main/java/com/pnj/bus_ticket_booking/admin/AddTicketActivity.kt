package com.pnj.bus_ticket_booking.admin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.bus_ticket_booking.databinding.ActivityAddTicketBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTicketBinding
    private val firestoreDatabase = FirebaseFirestore.getInstance()
    private val REQ_GALLERY = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TxtAddTanggalKeberangkatan.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                    binding.TxtAddTanggalKeberangkatan.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)
                }, year, month, day)

            dpd.show()
        }

        binding.BtnAddTicket.setOnClickListener {
            addTicket()
        }

        binding.BtnImgBus.setOnClickListener {
            openGallery()
        }
    }

    fun addTicket() {
        var nama_bus: String = binding.txtNamaBus.text.toString()
        var jumlah_kursi: String = binding.txtJumlahKursi.text.toString()
        var harga: String = binding.txtHarga.text.toString()
        var tujuan: String = binding.txtTujuan.text.toString()
        var tanggal_keberangkatan: String = binding.TxtAddTanggalKeberangkatan.text.toString()

        if (imageUri != null) {
            val ticket: MutableMap<String, Any> = HashMap()
            ticket["nama_bus"] = nama_bus
            ticket["jumlah_kursi"] = jumlah_kursi
            ticket["harga"] = harga
            ticket["tujuan"] = tujuan
            ticket["tanggal_keberangkatan"] = tanggal_keberangkatan
            ticket["gambar_bus"] = uploadPictFirebase(imageUri!!)

            firestoreDatabase.collection("tiket").add(ticket)
                .addOnSuccessListener {
                    val intentMain = Intent(this, TicketActivity::class.java)
                    startActivity(intentMain)
                }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQ_GALLERY)
    }

    private fun uploadPictFirebase(imgUri: Uri): String {
        val fileName = generateUniqueFileName()
        val ref = FirebaseStorage.getInstance().reference.child(fileName)

        ref.putFile(imgUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { downloadTask ->
                        downloadTask.result?.let { uri ->
                            imageUri = uri
                            binding.BtnImgBus.setImageURI(imageUri)
                        }
                    }
                }
            }

        return fileName
    }

    private fun generateUniqueFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "img_$timeStamp.jpg"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.BtnImgBus.setImageURI(imageUri)
        }
    }
}