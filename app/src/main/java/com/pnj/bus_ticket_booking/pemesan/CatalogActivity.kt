package com.pnj.bus_ticket_booking.pemesan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.pnj.bus_ticket_booking.R
import com.pnj.bus_ticket_booking.auth.SignInActivity
import com.pnj.bus_ticket_booking.chat.ChatActivity
import com.pnj.bus_ticket_booking.databinding.ActivityCatalogBinding


class CatalogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogBinding

    private lateinit var catalogRecyclerView: RecyclerView
    private lateinit var catalogArrayList: ArrayList<Catalog>
    private lateinit var catalogAdapter: CatalogAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inisiasi objek
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        catalogRecyclerView = binding.ticketListView
        catalogRecyclerView.layoutManager = LinearLayoutManager(this)
        catalogRecyclerView.setHasFixedSize(true)

        catalogArrayList = arrayListOf()
        catalogAdapter = CatalogAdapter(catalogArrayList)

        catalogRecyclerView.adapter = catalogAdapter

        //memangil data tiket dari firestore
        load_data()

        binding.txtSearchTiket.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = binding.txtSearchTiket.text.toString()
                if (keyword.isNotEmpty()){
                    //keyword dari search data
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

    //mengambil data tiket dari Firestore dan menampilkannya dalam catalogRecyclerView
    private fun load_data() {
        catalogArrayList.clear()
        db = FirebaseFirestore.getInstance() //akses layanan firebasestore
        db.collection("tiket").
        addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error != null){ //mengecek saat ada kesalahan data dari firestore
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED)
                        catalogArrayList.add(dc.document.toObject(Catalog::class.java)) //toObject(Catalog::class.java mengkonversi data menjadi objek
                }
                catalogAdapter.notifyDataSetChanged()//konfirmasi
            }
        })
    }

    private fun search_data(keyword: String) {
        catalogArrayList.clear()

        db = FirebaseFirestore.getInstance()

        val query = db.collection("tiket")
            .orderBy("nama_bus")
            .startAt(keyword)
            .get()
        query.addOnSuccessListener {
            catalogArrayList.clear()
            for (document in it) {
                catalogArrayList.add(document.toObject(Catalog::class.java))
            }
        }
    }



}