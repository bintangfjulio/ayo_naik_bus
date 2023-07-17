package com.pnj.bus_ticket_booking.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pnj.bus_ticket_booking.chat.ChatActivity
import com.pnj.bus_ticket_booking.databinding.ActivityTicketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import com.pnj.bus_ticket_booking.R
import com.pnj.bus_ticket_booking.auth.SignInActivity

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding

    private lateinit var ticketRecyclerView: RecyclerView
    private lateinit var ticketArrayList: ArrayList<Ticket>
    private lateinit var ticketAdapter: TicketAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        ticketRecyclerView = binding.ticketListView
        ticketRecyclerView.layoutManager = LinearLayoutManager(this)
        ticketRecyclerView.setHasFixedSize(true)

        ticketArrayList = arrayListOf()
        ticketAdapter = TicketAdapter(ticketArrayList)

        ticketRecyclerView.adapter = ticketAdapter

        load_data()

        binding.btnAddTicket.setOnClickListener {
            val intentAdd = Intent(this, AddTicketActivity::class.java)
            startActivity(intentAdd)
        }

        binding.txtSearchTiket.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = binding.txtSearchTiket.text.toString()
                if (keyword.isNotEmpty()){
                    search_data(keyword)
                }
                else{
                    load_data()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_bottom_tiket -> {
                    val intent = Intent(this, TicketActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_bottom_diskusi -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_bottom_logout -> {
                    firebaseAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        swipeDelete()
    }

    private fun load_data() {
        ticketArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("tiket").
        addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED)
                        ticketArrayList.add(dc.document.toObject(Ticket::class.java))
                }
                ticketAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun search_data(keyword: String) {
        ticketArrayList.clear()

        db = FirebaseFirestore.getInstance()

        val query = db.collection("tiket")
            .orderBy("nama_bus")
            .startAt(keyword)
            .get()
        query.addOnSuccessListener {
            ticketArrayList.clear()
            for (document in it) {
                ticketArrayList.add(document.toObject(Ticket::class.java))
            }
        }
    }

    private fun deleteTicket(ticket: Ticket, doc_id: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah ${ticket.nama_bus} ingin dihapus ?")
            .setCancelable(false)
            .setPositiveButton("Yes"){ dialog, id ->
                lifecycleScope.launch {
                    db.collection("tiket")
                        .document(doc_id).delete()

                    deleteFoto(ticket.gambar_bus.toString())
                    Toast.makeText(
                        applicationContext,
                        ticket.nama_bus.toString() + " is deleted",
                        Toast.LENGTH_LONG
                    ).show()
                    load_data()
                }
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
                load_data()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun swipeDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                lifecycleScope.launch {
                    val ticket = ticketArrayList[position]
                    val personQuery = db.collection("tiket")
                        .whereEqualTo("nama_bus", ticket.nama_bus)
                        .whereEqualTo("jumlah_kursi", ticket.jumlah_kursi)
                        .whereEqualTo("harga", ticket.harga)
                        .whereEqualTo("tanggal_keberangkatan",ticket.tanggal_keberangkatan)
                        .whereEqualTo("gambar_bus",ticket.gambar_bus)
                        .whereEqualTo("tujuan", ticket.tujuan)
                        .get()
                        .await()
                    if(personQuery.documents.isNotEmpty()){
                        for(document in personQuery){
                            try{
                                deleteTicket(ticket, document.id)
                                load_data()
                            } catch (e: Exception){
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    else{
                        withContext(Dispatchers.Main){
                            Toast.makeText(applicationContext, "Tiket yang ingin dihapus tidak ditemukan", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }).attachToRecyclerView(ticketRecyclerView)
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
}