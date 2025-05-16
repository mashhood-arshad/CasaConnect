package com.example.casaconnect.Activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.casaconnect.Domain.PropertyDomain
import com.example.casaconnect.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class wish(
    var userid: String?= null,
    val adid: String? = null
)

class DetailActivity : AppCompatActivity() {
    var binding: ActivityDetailBinding? = null
    private var `object`: PropertyDomain? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var index:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        this.bundles
        setVariable()

        firestore.collection("users").document(`object`!!.owndby.toString()).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "user"

                if (role == "agent") {
                    binding!!.agenttxt.text = "Ad poster is a CasaConnect Property Agent"
                } else {
                    binding!!.agenttxt.text = "Ad poster is a CasaConnect User"
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch role: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        binding!!.contacttxt2.visibility = View.INVISIBLE
        binding!!.prev.setOnClickListener {
            index--
            if(index < 0 )
                index = `object`!!.pickpath.size - 1
            Glide.with(this@DetailActivity)
                .load(`object`!!.pickpath[index])
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(binding!!.image1)

        }

        binding!!.prev3.setOnClickListener {
            index++
            if(index > `object`!!.pickpath.size - 1)
                index = 0
            Glide.with(this@DetailActivity)
                .load(`object`!!.pickpath[index])
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(binding!!.image1)

        }

        binding!!.analysisbtn.setOnClickListener {
            binding!!.contacttxt2.visibility = View.VISIBLE
            FirebaseFirestore.getInstance().collection("ads")
                .whereEqualTo("type", `object`!!.type)
                .whereEqualTo("size", `object`!!.size)
                .whereEqualTo("title", `object`!!.title)
                .get()
                .addOnSuccessListener { documents ->
                    var ad: Int = 0
                    var count:Int = 0
                    for (doc in documents) {
                       ad +=  doc.get("price")?.toString()?.toIntOrNull() ?: 0
                        Log.e("Analysis", "price: ${ad}")
                        count++
                    }
                    if(count == 0)
                        count++

                    var x: Int =  (`object`!!.price.toInt()*0.1).toInt()
                    if((ad/count).toInt() >= `object`!!.price.toInt()-x && (ad/count).toInt() <= `object`!!.price.toInt()+x){
                        binding!!.contacttxt2.text = "Fair Price"
                    }
                    else if((ad/count).toInt() > `object`!!.price.toInt()+x){
                        binding!!.contacttxt2.text = "Over Priced"
                    }
                    else {
                        binding!!.contacttxt2.text = "Good Price"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Analysis", "Query failed: ${e.message}", e)
                }
        }
        val currentUser = firebaseAuth.currentUser ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        binding!!.wishlistBtn.setOnClickListener{


            val obj = wish(userid = currentUser.uid.toString(),adid = `object`!!.adid.toString())
            firestore.collection("wishlist").add(obj)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Firestore Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore Error", exception.message, exception)
                }
        }
        binding!!.calcBtn.setOnClickListener  {
            startActivity(Intent(this, Calculator::class.java))
        }
        binding!!.backbtn.setOnClickListener {
            finish()
        }
    }

    private fun setVariable() {
        binding!!.backbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        Glide.with(this@DetailActivity)
            .load(`object`!!.pickpath[index])
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.stat_notify_error)
            .into(binding!!.image1)

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener{ snaps->
                for(u in snaps){
                    if(`object`!!.owndby.toString() == u.getString("userId")) {
                        binding!!.contacttxt.text = u.getString("email")+"\n\n"+u.getString("c_n")

                    }
                }

            }

        binding!!.titletxt.setText(`object`!!.type + " " + `object`!!.title)
        binding!!.typetxt.setText(`object`!!.type)
        binding!!.addresstxt.setText(`object`!!.address)
        binding!!.descriptiontxt.setText(`object`!!.description)
        binding!!.scoretxt.setText(`object`!!.score.toString())
        binding!!.pricetxt.setText("Price: " + `object`!!.price)
        binding!!.bedtxt.setText(`object`!!.bed.toString() + " Bed")
        binding!!.bathtxt.setText(`object`!!.bath.toString() + " bath")
        if (`object`!!.garage == true) {
            binding!!.garagetxt.setText("Yes")
        } else {
            binding!!.garagetxt.setText("No")
        }
    }

    private val bundles: Unit
        get() {
            `object` = getIntent().getSerializableExtra("object") as PropertyDomain?
        }
}
