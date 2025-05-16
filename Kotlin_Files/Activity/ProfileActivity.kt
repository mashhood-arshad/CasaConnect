package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.casaconnect.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
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
                        binding.nameTxt.text = u.getString("name")
                        binding.number.text = u.getString("c_n")
                        binding.mail.text = u.getString("email")
                    }
                }

            }
        binding.backbtn.setOnClickListener {
            finish()
        }
        binding.pp.setOnClickListener {
            startActivity(Intent(this, privacypolicy::class.java))
        }
        binding.sec.setOnClickListener {
            startActivity(Intent(this, Security::class.java))
        }
        binding.accinfo.setOnClickListener {
            startActivity(Intent(this, accinfo::class.java))
        }

        binding.tc.setOnClickListener {
            startActivity(Intent(this, Terms::class.java))
        }
        binding.help.setOnClickListener {
            startActivity(Intent(this, help::class.java))
        }
        binding.signOutbtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, log_signActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
