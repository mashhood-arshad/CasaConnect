package com.example.casaconnect.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casaconnect.Adapter.ListitemsAdatper
import com.example.casaconnect.Domain.PropertyDomain
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var isFilterApplied = false
    private var minPrice    = 0
    private var maxPrice    = Int.MAX_VALUE
    private var minBeds     = 0
    private var minBaths    = 0
    private var location: String? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = firebaseAuth.currentUser ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener{ snaps->
                for(u in snaps){
                    if(currentUser.uid.toString() == u.getString("userId"))
                        binding.nametxt.text = u.getString("name")
                }

            }
        loadAds()

        initBottommenu()

        binding.sellButton.setOnClickListener {
            startActivity(Intent(this, SellerActivity::class.java))
        }

        // Search button
        binding.searchbtn.setOnClickListener {
            val query = binding.searchbar.text.toString().trim().takeIf { it.isNotBlank() }
            loadAds(minPrice, maxPrice, minBeds, minBaths, location, query)
        }

        // Filter button
        binding.filterButton.setOnClickListener {
            try { showFilterSheet() }
            catch (e: Exception) {
                Log.e("MainActivity", "Error showing filter sheet", e)
                Toast.makeText(this, "Unable to open filters", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initBottommenu() {
        binding.bottommenu.setOnItemSelectedListener(object :
            ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(i: Int) {
                if (i == R.id.profile) {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                }
                if (i == R.id.wishlist) {
                    startActivity(Intent(this@MainActivity, wishlist_activity::class.java))
                }
                if (i == R.id.explorer) {
                    startActivity(Intent(this@MainActivity, Calculator::class.java))
                }
            }
        })
    }

    private fun loadAds(
        minPrice: Int = 0,
        maxPrice: Int = Int.MAX_VALUE,
        minBeds: Int = 0,
        minBaths: Int = 0,
        location: String? = null,
        searchStr: String? = null
    ) {
        isFilterApplied = (
                minPrice > 0 ||
                        maxPrice < Int.MAX_VALUE ||
                        minBeds > 0 ||
                        minBaths > 0 ||
                        location != null
                )

        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("ads")

        query.get()
            .addOnSuccessListener { snaps ->
                val list = ArrayList<PropertyDomain?>()
                for (doc in snaps) {
                    try {
                        val property = PropertyDomain(
                            owndby = doc.getString("userId"),
                            postdate = doc.getTimestamp("postedAt")?.toDate()?.toString(),
                            type = doc.getString("type"),
                            title = doc.getString("title"),
                            address = doc.getString("address"),
                            pickpath = doc.get("imageUrls") as List<String?>,
                            description = doc.getString("description"),
                            price = doc.get("price")?.toString()?.toIntOrNull() ?: 0,
                            bed = doc.get("bed")?.toString()?.toIntOrNull() ?: 0,
                            bath = doc.get("bath")?.toString()?.toIntOrNull() ?: 0,
                            size = doc.getString("size"),
                            garage = doc.get("garage") as? Boolean ?: false,
                            score = doc.get("rating")?.toString()?.toDoubleOrNull() ?: 0.0,
                            adid = doc.getString("adid").toString()
                        )

                        // check numeric/location filters
                        val okFilter = if (isFilterApplied) {
                            (property.price   in minPrice..maxPrice) &&
                                    (property.bed     >= minBeds) &&
                                    (property.bath    >= minBaths) &&
                                    (location == null || property.address == location)
                        } else {
                            true
                        }

                        // check search string match
                        val okSearch = searchStr.isNullOrBlank() || listOf(
                            property.title,
                            property.address
                        ).any { it?.contains(searchStr, ignoreCase = true) == true }

                        if (okFilter && okSearch) {
                            list.add(property)
                        }

                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error parsing ad ${doc.id}", e)
                    }
                }

                // bind to RecyclerView
                binding.viewlist.layoutManager = LinearLayoutManager(this)
                binding.viewlist.adapter    = ListitemsAdatper(list)
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Failed to load ads", e)
                Toast.makeText(this, "Failed to load ads: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showFilterSheet() {
        val dialog = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.filter_bottom_sheet, null)
        dialog.setContentView(v)

        val rem  = v.findViewById<Button>(R.id.removeFilterBtn)
        rem.setOnClickListener {
            dialog.dismiss()
            loadAds()
            return@setOnClickListener
        }

        val minEt  = v.findViewById<EditText>(R.id.minPriceEt)
        val maxEt  = v.findViewById<EditText>(R.id.maxPriceEt)
        val bedEt  = v.findViewById<EditText>(R.id.bedEt)
        val bathEt = v.findViewById<EditText>(R.id.bathEt)
        val locEt  = v.findViewById<EditText>(R.id.locationSpinner)
        val apply  = v.findViewById<Button>(R.id.applyFilterBtn)

        apply.setOnClickListener {
            minPrice = minEt.text.toString().toIntOrNull() ?: 0
            maxPrice = maxEt.text.toString().toIntOrNull() ?: Int.MAX_VALUE
            minBeds  = bedEt.text.toString().toIntOrNull() ?: 0
            minBaths = bathEt.text.toString().toIntOrNull() ?: 0
            location = locEt.text.toString().takeIf { it.isNotBlank() }

            dialog.dismiss()
            val currentSearch = binding.searchbar.text.toString().trim().takeIf { it.isNotBlank() }
            loadAds(minPrice, maxPrice, minBeds, minBaths, location, currentSearch)
        }
        dialog.show()
    }
}
