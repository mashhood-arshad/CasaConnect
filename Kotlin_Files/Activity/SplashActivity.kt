package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.casaconnect.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())
        binding.login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                startActivity(Intent(this@SplashActivity, log_signActivity::class.java))
            }
        })
    }

}