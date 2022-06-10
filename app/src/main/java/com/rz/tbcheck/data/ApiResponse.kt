package com.rz.tbcheck.data

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class ApiResponse {
    @Parcelize
    data class ListHistoryItem(
        @field:SerializedName("id")
        val id: String,
        @field:SerializedName("date")
        val date: String,
        @field:SerializedName("status")
        val status: String,
        @field:SerializedName("accuracy")
        val accuracy: Double,
        @field:SerializedName("photoUrl")
        val photoUrl: String? = null,
    ) : Parcelable

    @Parcelize
    data class IntentSend(
        val accuracy: Float,
    ) : Parcelable
}