package com.pnj.bus_ticket_booking.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.bus_ticket_booking.admin.TicketActivity
import com.pnj.bus_ticket_booking.databinding.ActivitySignInBinding
import com.pnj.bus_ticket_booking.pemesan.CatalogActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPass.text.toString()
            signin_firebase(email, password)
        }

        binding.tvSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signin_firebase(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid

                    val userRef = FirebaseFirestore.getInstance().collection("user").document(uid.toString())
                    userRef.get().addOnSuccessListener { documentSnapshot ->
                        val role = documentSnapshot.getString("role").toString()

                        if(role == "pemesan") {
                            val intent = Intent(this, CatalogActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, TicketActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                else{
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this, "Lengkapi Input", Toast.LENGTH_SHORT).show()
        }
    }
}