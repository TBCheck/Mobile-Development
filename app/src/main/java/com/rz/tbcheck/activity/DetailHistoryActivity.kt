package com.rz.tbcheck.activity

import android.R
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.rz.tbcheck.data.ApiResponse
import com.rz.tbcheck.databinding.ActivityDetailHistoryBinding


class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val from = intent.getStringExtra("from").toString()
        if (from == "1") {
            val dataIntent =
                intent.getParcelableExtra<ApiResponse.IntentSend>("data") as ApiResponse.IntentSend
            var accuracy = (dataIntent.accuracy * 100).toString()

            if (accuracy.substring(2) == ".") {
                accuracy = accuracy.substring(0, 2)
            }

            binding.tvAccuracy.text = "${accuracy}%"
        } else {

        }
    }

    private fun setClick() {
        binding.apply {

        }
    }
}