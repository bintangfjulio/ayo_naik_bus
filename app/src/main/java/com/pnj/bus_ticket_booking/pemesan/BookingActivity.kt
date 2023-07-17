package com.pnj.bus_ticket_booking.pemesan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.pnj.bus_ticket_booking.R
import com.pnj.bus_ticket_booking.auth.SignInActivity
import com.pnj.bus_ticket_booking.chat.ChatActivity
import com.pnj.bus_ticket_booking.databinding.ActivityBookingBinding

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingArrayList: ArrayList<Booking>
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        bookingRecyclerView = binding.bookingListView
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingRecyclerView.setHasFixedSize(true)

        bookingArrayList = arrayListOf()
        bookingAdapter = BookingAdapter(bookingArrayList)

        bookingRecyclerView.adapter = bookingAdapter
        load_data()

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_bottom_tiket -> {
                    val intent = Intent(this, CatalogActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_bottom_booking -> {
                    val intent = Intent(this, BookingActivity::class.java)
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
    }

    private fun load_data() {
        bookingArrayList.clear()
        db = FirebaseFirestore.getInstance()

        val user = firebaseAuth.currentUser
        val uid = user?.uid

        db.collection("user").document(uid.toString()).collection("booking").
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
                        bookingArrayList.add(dc.document.toObject(Booking::class.java))
                }
                bookingAdapter.notifyDataSetChanged()
            }
        })
    }
}