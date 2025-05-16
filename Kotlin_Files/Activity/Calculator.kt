package com.example.casaconnect.Activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casaconnect.R
import com.example.casaconnect.databinding.ActivityAllUseradBinding
import com.example.casaconnect.databinding.ActivityCalculatorBinding

class Calculator : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalculatorBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())
        val typeSpinner = findViewById<Spinner>(R.id.tax)
        val typeSpinner1 = findViewById<Spinner>(R.id.tax2)

        val types = resources.getStringArray(R.array.Options)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            types
        )
        typeSpinner.adapter = spinnerAdapter
        typeSpinner1.adapter = spinnerAdapter

        binding.CalBtn.setOnClickListener {
            var x:Int =  binding.taxTxt.text.toString().toInt()
            var y:Int = binding.commTxt.text.toString().toInt()
            var result:Int = 0
            if(binding.tax.selectedItem.toString() == "Percent")
                x = (binding.priceTxt.text.toString().toInt()*x)/100
            if(binding.tax2.selectedItem.toString() == "Percent")
                y = (binding.priceTxt.text.toString().toInt()*y)/100

            result = binding.priceTxt.text.toString().toInt() + x + y + binding.DevTxt.text.toString().toInt()
            binding.ResTxt.text = "Result\n" + result.toString()
        }
    }
}