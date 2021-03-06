package com.rz.tbcheck.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rz.tbcheck.config.ApiConfig
import com.rz.tbcheck.data.ListHistoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHistoryViewModel : ViewModel() {
    private val _history = MutableLiveData<ListHistoryItem>()
    val history: LiveData<ListHistoryItem> = _history

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    fun addHistory(historyItem: ListHistoryItem) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().addHistory(
            historyItem.image!!,
            historyItem.accuracy!!,
            historyItem.status!!,
            historyItem.description!!
        )
        client.enqueue(object : Callback<ListHistoryItem> {
            override fun onResponse(
                call: Call<ListHistoryItem>, response: Response<ListHistoryItem>
            ) {
                _history.value = response.body()
                _isLoading.value = false
            }

            override fun onFailure(call: Call<ListHistoryItem>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = "Error from connection"
                Log.e(ContentValues.TAG, "errorFailure: ${t.message}")
            }
        })
    }
}