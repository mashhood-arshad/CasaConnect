package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.Activity.SignupActivity
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityDetailBinding
import com.example.casaconnect.databinding.ActivitySellerBinding
import kotlin.jvm.java

class SellerActivity : AppCompatActivity() {
    private var binding: ActivitySellerBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivitySellerBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        binding!!.postedAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                startActivity(Intent(this@SellerActivity, own_Ad_list::class.java))
            }
        })
        binding!!.adPost.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                startActivity(Intent(this@SellerActivity, post_ad::class.java))
            }
        })
        binding!!.buyButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }
        })
    }
}