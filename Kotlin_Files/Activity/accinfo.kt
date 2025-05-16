package com.example.casaconnect.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityAccinfoBinding
import com.example.casaconnect.databinding.ActivityProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class accinfo : AppCompatActivity() {
    private lateinit var binding: ActivityAccinfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener{ snaps->
                for(u in snaps){
                    if(currentUser.uid.toString() == u.getString("userId")) {
                        binding.nameeee.text = u.getString("name")
                        binding.idTxt.text = currentUser.uid.toString()
                        binding.maill.text = u.getString("email")
                    }
                }

            }

        }
    }