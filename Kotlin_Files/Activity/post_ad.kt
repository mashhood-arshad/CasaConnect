package com.example.casaconnect.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.casaconnect.Domain.NotificationModel
import com.example.casaconnect.Model.AdModel
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityPostAdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import kotlin.io.encoding.Base64

class post_ad : AppCompatActivity() {
    private lateinit var binding: ActivityPostAdBinding
    private val selectedImageUris = mutableListOf<Uri>()
    private val imageUrls = mutableListOf<String>()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore   = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPostAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setarr()
        setarr1()
        setarr2()
        setarr3()

        binding.imgButton.setOnClickListener {
            pickImages()
        }

        binding.doneButton.visibility = View.VISIBLE
        binding.doneButton.setOnClickListener {
            binding.doneButton.visibility = View.INVISIBLE
            if (selectedImageUris.isEmpty()) {
                binding.doneButton.visibility = View.VISIBLE
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (areFieldsValid()) {
                uploadAllImagesToImgBB()
            } else {
                binding.doneButton.visibility = View.VISIBLE
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun pickImages() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(this, "Select Pictures"), 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUris.clear()
            data?.let { intent ->
                intent.clipData?.let { clip ->
                    for (i in 0 until clip.itemCount) {
                        selectedImageUris.add(clip.getItemAt(i).uri)
                    }
                }
                intent.data?.let { uri ->
                    selectedImageUris.add(uri)
                }
            }
            Toast.makeText(this, "${selectedImageUris.size} images selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun areFieldsValid(): Boolean {
        return  binding.addText.text.isNotBlank()
                && binding.bedTxt.text.isNotBlank()
                && binding.bathTxt.text.isNotBlank()
                && binding.sizeTxt.text.isNotBlank()
                && binding.desctxt.text.isNotBlank()
                && binding.priceeTxt.text.isNotBlank()
    }
    private fun setarr() {
        val typeSpinner = findViewById<Spinner>(R.id.typeltxt)

        val types = resources.getStringArray(R.array.property_types)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            types
        )
        typeSpinner.adapter = spinnerAdapter

    }

    private fun setarr1() {
        val typeSpinner = findViewById<Spinner>(R.id.gragetxt)

        val types = resources.getStringArray(R.array.garageYN)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            types
        )
        typeSpinner.adapter = spinnerAdapter

    }

    private fun setarr2() {
        val typeSpinner = findViewById<Spinner>(R.id.size_txt2)

        val types = resources.getStringArray(R.array.size_types)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            types
        )
        typeSpinner.adapter = spinnerAdapter

    }

    private fun setarr3() {
        val typeSpinner = findViewById<Spinner>(R.id.title_txt)

        val types = resources.getStringArray(R.array.titles)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            types
        )
        typeSpinner.adapter = spinnerAdapter

    }

    private fun uploadAllImagesToImgBB() {
        imageUrls.clear()
        val apiKey = "c00ccfa768659c2bfe6b8d107f4dc5d3"
        val client = OkHttpClient()
        selectedImageUris.forEachIndexed { index, uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return@forEachIndexed
            val b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

            val body = FormBody.Builder()
                .add("key", apiKey)
                .add("image", b64)
                .build()

            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@post_ad, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val resp = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val link = JSONObject(resp)
                            .getJSONObject("data")
                            .getString("url")
                        imageUrls.add(link)
                        if (imageUrls.size == selectedImageUris.size) {
                            runOnUiThread { saveAdToFirestore() }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@post_ad, "ImgBB error: $resp", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }
    }

    private fun saveAdToFirestore() {
        val user = firebaseAuth.currentUser ?: run {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val newDoc = firestore.collection("ads").document()
        val ad = AdModel(
            adid        = newDoc.id,
            title       = binding.titleTxt.selectedItem.toString(),
            address     = binding.addText.text.toString(),
            bed         = binding.bedTxt.text.toString(),
            bath        = binding.bathTxt.text.toString(),
            size        = binding.sizeTxt.text.toString() + " " + binding.sizeTxt2.selectedItem,
            garage      = binding.gragetxt.selectedItem.toString(),
            type        = binding.typeltxt.selectedItem.toString(),
            description = binding.desctxt.text.toString(),
            price       = binding.priceeTxt.text.toString(),
            imageUrls   = imageUrls,
            userId      = user.uid
        )
        newDoc.set(ad)
            .addOnSuccessListener {
                sendAdminNotifications(ad)
                Toast.makeText(this, "Ad posted!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("post_ad", "Firestore error", e)
                Toast.makeText(this, "Failed to post ad: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendAdminNotifications(ad: AdModel) {
        val now = System.currentTimeMillis()
        firestore.collection("users")
            .whereEqualTo("role", "admin")
            .get()
            .addOnSuccessListener { snaps ->
                snaps.documents.forEach { userDoc ->
                    val notif = NotificationModel(
                        id        = firestore.collection("notifications").document().id,
                        userid    = userDoc.id,
                        message   = "New ad by ${ad.userId}: ${ad.title}",
                        timestamp = now
                    )
                    firestore.collection("notifications").document(notif.id).set(notif)
                }
            }
    }
}
