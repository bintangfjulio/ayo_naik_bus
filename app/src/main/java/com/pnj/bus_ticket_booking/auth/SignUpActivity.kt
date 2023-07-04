package com.pnj.bus_ticket_booking.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.bus_ticket_booking.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val email = binding.txtSignupEmail.text.toString()
            val password = binding.txtSignupPass.text.toString()
            val confirm_password = binding.txtSignupPass.text.toString()

            signup_firebase(email, password, confirm_password)
        }

        binding.tvSignin.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signup_firebase(email: String, password: String, confirm_password: String){
        if(email.isNotEmpty() && password.isNotEmpty() && confirm_password.isNotEmpty()){
            if(password == confirm_password){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val uid = firebaseAuth.currentUser?.uid
                        addUser(uid.toString())
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    else{ Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() }
                }
            }
            else{
                Toast.makeText(this, "Samakan Password untuk Konfirmasi", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Lengkapi Input", Toast.LENGTH_SHORT).show()
        }
    }

    fun addUser(uid: String) {
        val user: MutableMap<String, Any> = HashMap()
        user["email"] = binding.txtSignupEmail.text.toString()
        user["role"] = "pemesan"

        firestoreDatabase.collection("user").document(uid).set(user)
    }
}