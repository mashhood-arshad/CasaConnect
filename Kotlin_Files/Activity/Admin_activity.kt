package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityAdminBinding
import com.example.casaconnect.databinding.ActivityDetailBinding
import com.example.casaconnect.databinding.ActivityLogSignBinding
import com.google.firebase.auth.FirebaseAuth

class Admin_activity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(getLayoutInflater())
        setContentView(binding.root)
        binding.postedAdd.setOnClickListener {
            startActivity(Intent(this, all_userad::class.java))
        }
        binding.NotificationAct.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
        binding.outButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, log_signActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}