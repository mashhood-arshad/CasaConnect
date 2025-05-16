package com.example.casaconnect.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.Domain.NotificationModel
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityCalculatorBinding
import com.example.casaconnect.databinding.ActivityHelpBinding
import com.google.firebase.firestore.FirebaseFirestore

class help : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.submitBtn.setOnClickListener {
            val now = System.currentTimeMillis()
            firestore.collection("users")
                .whereEqualTo("role", "admin")
                .get()
                .addOnSuccessListener { userSnaps ->
                    for (userDoc in userSnaps.documents) {
                        val notifRef = firestore.collection("notifications").document()
                        val notif = NotificationModel(
                            id = notifRef.id,
                            userid = userDoc.id.toString(),
                            message = "New Message by: ${binding.nameTxt}\nMessage: ${binding.DevTxt}",
                            timestamp = now
                        )
                        notifRef.set(notif)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("post_ad", "Failed to fetch admins: ${e.message}", e)
                }
        }
    }
}