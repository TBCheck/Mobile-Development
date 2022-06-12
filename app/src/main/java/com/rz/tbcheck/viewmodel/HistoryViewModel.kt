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

class HistoryViewModel : ViewModel() {

    private val _listHistory = MutableLiveData<List<ListHistoryItem>>()
    val listHistory: LiveData<List<ListHistoryItem>> = _listHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    fun getHistory() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getHistory()
        client.enqueue(object : Callback<List<ListHistoryItem>> {
            override fun onResponse(
                call: Call<List<ListHistoryItem>>, response: Response<List<ListHistoryItem>>
            ) {
                _listHistory.value = response.body()
                _isLoading.value = false
            }

            override fun onFailure(call: Call<List<ListHistoryItem>>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = "Error from connection"
                Log.e(ContentValues.TAG, "errorFailure: ${t.message}")
            }
        })
    }
}