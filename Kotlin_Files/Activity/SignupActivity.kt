package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.casaconnect.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

data class UserModel(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val c_n: String,
    val role: String
)

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.signinButton.setOnClickListener {
            val email = binding.Emailtxt.text.toString()
            val pass = binding.Passwordtxt.text.toString()
            val name = binding.Nametxt.text.toString()
            val c_p = binding.cPasswordtxt.text.toString()
            val c_n = binding!!.cNoTxt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty() && c_p.isNotEmpty()) {
                if (pass != c_p) {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                                lateinit var user: UserModel
                                if(binding.checkBox.isChecked)
                                    user = UserModel(userId, name, email,c_n,"agent")
                                else
                                    user = UserModel(userId, name, email,c_n,"user")

                                firestore.collection("users").document(userId).set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }

                            } else {
                                Toast.makeText(this, task.exception?.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
