package com.example.casaconnect.Model

import com.google.firebase.Timestamp

data class AdModel(
    val title: String = "",
    val address: String = "",
    val bed: String = "",
    val bath: String = "",
    val size: String = "",
    val garage: String = "",
    val type: String = "",
    val description: String = "",
    val imageUrls: List<String?>,
    val userId: String = "",
    val price: String="",
    val rating: String="0",
    val postedAt: Timestamp = Timestamp.now(),
    val adid:String=""
)
