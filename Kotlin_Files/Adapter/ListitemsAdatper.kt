package com.example.casaconnect.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.casaconnect.Activity.DetailActivity
import com.example.casaconnect.Domain.PropertyDomain
import com.example.casaconnect.databinding.ViewholderItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListitemsAdatper(private val items: ArrayList<PropertyDomain?>) :
    RecyclerView.Adapter<ListitemsAdatper.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.titletxt.text = item?.type +" " + item?.title
        holder.binding.pricetxt.text = "$${item!!.price}"
        holder.binding.sizetxt.text = item?.size ?: "N/A"
        holder.binding.garagetxt.text = if (item?.garage == true) "Car Garage" else "No Garage"
        holder.binding.addresstxt.text = item!!.address
        holder.binding.bathtxt.text = "${item!!.bath} Bath"
        holder.binding.bedtxt.text = "${item!!.bed} Bed"

        Glide.with(context)
            .load(item.pickpath[0])
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.binding.pic)

        if(item.deleteflag == true){
            holder.binding.deletebtn.visibility = View.VISIBLE
        }
        else
            holder.binding.deletebtn.visibility = View.INVISIBLE

        holder.binding.deletebtn.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            db.collection("ads")
                .whereEqualTo("adid", item!!.adid)
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot.documents) {
                        doc.reference.delete()
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                            }
                    }
                }
                .addOnFailureListener { e ->
                }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(val binding: ViewholderItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}


