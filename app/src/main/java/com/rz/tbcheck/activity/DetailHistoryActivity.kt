package com.rz.tbcheck.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rz.tbcheck.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    private fun setClick() {
        binding.apply {

        }
    }
}