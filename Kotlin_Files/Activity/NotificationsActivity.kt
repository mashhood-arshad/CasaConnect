package com.example.casaconnect.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casaconnect.Adapter.NotificationAdapter
import com.example.casaconnect.Domain.NotificationModel
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.clearAll.setOnClickListener {
            clearAllNotifications()
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("notifications")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snaps ->
                val list = ArrayList<NotificationModel>()

                for (doc in snaps.documents) {
                    val id        = doc.getString("userid") ?: ""
                    val message   = doc.getString("message") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L

                   if(id == uid.toString()){
                    val notif = NotificationModel(
                        id        = id,
                        message   = message,
                        timestamp = timestamp
                    )
                    list.add(notif)
                   }
                }

                binding.rvNotifications.layoutManager = LinearLayoutManager(this)
                binding.rvNotifications.adapter       = NotificationAdapter(list)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun clearAllNotifications() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("notifications")
            .whereEqualTo("userid", uid.toString())
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                        .addOnSuccessListener {
                            finish()
                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
            .addOnFailureListener { e ->
            }
    }
}