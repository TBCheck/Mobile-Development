package com.rz.tbcheck.data

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentSend(
    val accuracy: Float,
    val filePath: Uri
) : Parcelable

@Parcelize
data class ListHistoryItem(
    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("image")
    val image: String? = null,
    @field:SerializedName("accuracy")
    val accuracy: String? = null,
    @field:SerializedName("status")
    val status: String? = null,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("createdAt")
    val createdAt: String? = null,
) : Parcelable