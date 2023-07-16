package com.pnj.bus_ticket_booking.admin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.pnj.bus_ticket_booking.databinding.ActivityEditTicketBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTicketBinding
    private val db = FirebaseFirestore.getInstance()
    private val REQ_GALLERY = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (year, month, day, curr_ticket) = setDefaultValue()

        binding.TxtEditTanggalKeberangkatan.setOnClickListener {
            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    binding.TxtEditTanggalKeberangkatan.setText(
                        "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                }, year.toString().toInt(), month.toString().toInt(), day.toString().toInt()
            )
            dpd.show()
        }

        binding.BtnEditTicket.setOnClickListener {
            val new_data_ticket = newTicket()
            updateTicket(curr_ticket as Ticket, new_data_ticket)

            val intentMain = Intent(this, TicketActivity::class.java)
            startActivity(intentMain)
            finish()
        }

        showFoto()

        binding.BtnImgBus.setOnClickListener {
            openGallery()
        }
    }

    fun setDefaultValue(): Array<Any> {
        val intent = intent
        val nama_bus = intent.getStringExtra("nama_bus").toString()
        val jumlah_kursi = intent.getStringExtra("jumlah_kursi").toString()
        val harga = intent.getStringExtra("harga").toString()
        val tujuan = intent.getStringExtra("tujuan").toString()
        val tgl_berangkat = intent.getStringExtra("tanggal_keberangkatan").toString()
        val gambar_bus = intent.getStringExtra("gambar_bus").toString()

        binding.txtNamaBus.setText(nama_bus)
        binding.txtJumlahKursi.setText(jumlah_kursi)
        binding.txtHarga.setText(harga)
        binding.txtTujuan.setText(tujuan)
        binding.TxtEditTanggalKeberangkatan.setText(tgl_berangkat)

        val tanggal_split = intent.getStringExtra("tanggal_keberangkatan")
            .toString().split("-").toTypedArray()
        val year = tanggal_split[0].toInt()
        val month = tanggal_split[1].toInt() - 1
        val day = tanggal_split[2].toInt()

        val curr_tiket = Ticket(nama_bus, tgl_berangkat, gambar_bus, jumlah_kursi, harga, tujuan)
        return arrayOf(year, month, day, curr_tiket)
    }

    fun newTicket(): Map<String, Any>{
        var nama_bus: String = binding.txtNamaBus.text.toString()
        var jumlah_kursi: String = binding.txtJumlahKursi.text.toString()
        var harga: String = binding.txtHarga.text.toString()
        var tujuan: String = binding.txtTujuan.text.toString()
        var tanggal_keberangkatan: String = binding.TxtEditTanggalKeberangkatan.text.toString()

        var image_bus = ""

        if(imageUri != null) {
            image_bus = uploadPictFirebase(imageUri!!)
        } else {
            image_bus = intent.getStringExtra("gambar_bus").toString()
        }

        val ticket: MutableMap<String, Any> = HashMap()

        ticket["nama_bus"] = nama_bus
        ticket["jumlah_kursi"] = jumlah_kursi
        ticket["harga"] = harga
        ticket["tujuan"] = tujuan
        ticket["tanggal_keberangkatan"] = tanggal_keberangkatan
        ticket["gambar_bus"] = image_bus

        return ticket
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.BtnImgBus.setImageURI(imageUri)
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

    fun showFoto(){
        val intent = intent
        val gambar_bus = intent.getStringExtra("gambar_bus").toString()

        val storageRef = FirebaseStorage.getInstance().reference.child(gambar_bus)
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.BtnImgBus.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Log.e("foto ?", "gagal")
        }
    }

    private fun deleteFoto(file_name: String){
        val storage = Firebase.storage
        val storageRef = storage.reference
        val deleteFileRef = storageRef.child(file_name)
        if(deleteFileRef != null){
            deleteFileRef.delete().addOnSuccessListener {
                Log.e("deleted", "success")
            }.addOnFailureListener {
                Log.e("deleted", "failed")
            }
        }
    }

    private fun updateTicket(ticket: Ticket, newTicketMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = db.collection("tiket")
                .whereEqualTo("nama_bus", ticket.nama_bus)
                .whereEqualTo("jumlah_kursi", ticket.jumlah_kursi)
                .whereEqualTo("harga", ticket.harga)
                .whereEqualTo("tanggal_keberangkatan", ticket.tanggal_keberangkatan)
                .whereEqualTo("tujuan", ticket.tujuan)
                .whereEqualTo("gambar_bus", ticket.gambar_bus)
                .get()
                .await()
            if(personQuery.documents.isNotEmpty()){
                for(document in personQuery){
                    try{
                        deleteFoto(ticket.gambar_bus.toString())
                        db.collection("tiket").document(document.id).set(
                            newTicketMap,
                            SetOptions.merge()
                        )
                    } catch (e: java.lang.Exception){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditTicketActivity,
                                e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else{
                withContext(Dispatchers.Main){
                    Toast.makeText(this@EditTicketActivity,
                        "No person marched the query.", Toast.LENGTH_LONG).show()
                }
            }
        }
}