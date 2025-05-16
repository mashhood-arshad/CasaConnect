package com.example.casaconnect.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casaconnect.Adapter.ListitemsAdatper
import com.example.casaconnect.Domain.PropertyDomain
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityAllUseradBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class all_userad : AppCompatActivity() {
    var binding: ActivityAllUseradBinding? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllUseradBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        initList()
        binding!!.backbtn.setOnClickListener {
            finish()
        }
    }
    private fun initList() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val items1 = ArrayList<PropertyDomain?>()
        val currentUser = firebaseAuth.currentUser ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("ads")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val property = PropertyDomain(
                        owndby = document.getString("userId"),
                        postdate = document.getTimestamp("postedAt")?.toDate()?.toString(),
                        type = document.getString("type"),
                        title = document.getString("title"),
                        address = document.getString("address"),
                        pickpath = document.get("imageUrls") as List<String?>,
                        description = document.getString("description"),
                        price = document.get("price")?.toString()?.toIntOrNull() ?: 0,
                        bed = document.get("bed")?.toString()?.toIntOrNull() ?: 0,
                        bath = document.get("bath")?.toString()?.toIntOrNull() ?: 0,
                        size = document.getString("size"),
                        garage = document.get("garage") as? Boolean ?: false,
                        score = document.get("rating")?.toString()?.toDoubleOrNull() ?: 0.0,
                        adid = document.getString("adid").toString(),
                        deleteflag = true
                    )
                    items1.add(property)
                        binding!!.viewlist.setLayoutManager(
                            LinearLayoutManager(
                                this,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        )
                        binding!!.viewlist.setAdapter(ListitemsAdatper(items1))

                }

            }
            .addOnFailureListener {
                android.widget.Toast.makeText(
                    this,
                    "Failed to load ads: ${it.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

    }
}