package com.rz.tbcheck.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rz.tbcheck.adapter.ListHistoryAdapter
import com.rz.tbcheck.data.ListHistoryItem
import com.rz.tbcheck.databinding.ActivityHistoryBinding
import com.rz.tbcheck.viewmodel.HistoryViewModel

class HistoryActivity : AppCompatActivity() {

    private val list = ArrayList<ListHistoryItem>()
    private lateinit var binding: ActivityHistoryBinding
    private val model: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.listHistory.observe(this) { listUser ->
            setHistoryResult(listUser)
        }

        model.isLoading.observe(this) {
            setLoading(it)
        }

        model.snackbarText.observe(this) {
            Snackbar.make(
                binding.rvMain,
                it,
                Snackbar.LENGTH_SHORT
            ).show()
        }


        model.getHistory()
    }

    private fun setHistoryResult(stories: List<ListHistoryItem>) {
        list.clear()
        val listHistory = ArrayList<ListHistoryItem>()
        for (history in stories) {
            history.apply {
                val getResult = ListHistoryItem(
                    id, image, accuracy, status, description, createdAt
                )
                listHistory.add(getResult)
            }
        }
        list.addAll(listHistory)

        if (listHistory.isEmpty()) {
            binding.tvNonData.visibility = View.VISIBLE
        } else {
            binding.tvNonData.visibility = View.GONE
        }

        setRecycler()
    }

    private fun setRecycler() {
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListHistoryAdapter(list)
        binding.rvMain.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : ListHistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListHistoryItem) {
                val detailIntent = Intent(this@HistoryActivity, DetailHistoryActivity::class.java)
                detailIntent.putExtra(DetailHistoryActivity.INTENT_FROM2, data)
                startActivity(detailIntent)
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}