package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.casaconnect.databinding.ActivityLogSignBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class log_signActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogSignBinding
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogSignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinButton.setOnClickListener {
            val email = binding.Emailtxt.text.toString()
            val password = binding.Passwordtxt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                            firestore.collection("users").document(uid).get()
                                .addOnSuccessListener { doc ->
                                    val role = doc.getString("role") ?: "user"

                                    if (role == "admin") {
                                        startActivity(Intent(this, Admin_activity::class.java))
                                    } else {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to fetch role: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            Toast.makeText(this, it.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signuptxt.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser ?: return
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "user"
                if (role == "admin") {
                    startActivity(Intent(this, Admin_activity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
    }
}
