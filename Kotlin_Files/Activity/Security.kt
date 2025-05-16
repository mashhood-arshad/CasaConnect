package com.example.casaconnect.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityProfileBinding
import com.example.casaconnect.databinding.ActivitySecurityBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Security : AppCompatActivity() {
    private lateinit var binding: ActivitySecurityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.submitBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email
            val oldPassword = binding.oldTxt.text
            val newPassword = binding.newTxt.text

            if (user != null && email != null) {
                val credential = EmailAuthProvider.getCredential(email, oldPassword.toString())

                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {

                            user.updatePassword(newPassword.toString())
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Old password incorrect: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
    }
}