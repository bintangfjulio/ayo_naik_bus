package com.pnj.bus_ticket_booking

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.bus_ticket_booking.admin.TicketActivity
import com.pnj.bus_ticket_booking.auth.SignInActivity
import com.pnj.bus_ticket_booking.pemesan.CatalogActivity

class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 1000L
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser == null) {
            Handler().postDelayed({
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_TIME_OUT)
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            val user = firebaseAuth.currentUser
            val uid = user?.uid

            val userRef = FirebaseFirestore.getInstance().collection("user").document(uid.toString())
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val role = documentSnapshot.getString("role").toString()

                Handler().postDelayed({
                    if(role == "pemesan") {
                        val intent = Intent(this, CatalogActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, TicketActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }, SPLASH_TIME_OUT)
            }
        }
    }
}