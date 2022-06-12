package com.rz.tbcheck.service

import com.rz.tbcheck.data.ListHistoryItem
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("crud")
    fun getHistory(): Call<List<ListHistoryItem>>

    @FormUrlEncoded
    @POST("crud")
    fun addHistory(
        @Field("image") image: String,
        @Field("accuracy") accuracy: String,
        @Field("status") status: String,
        @Field("description") description: String,
    ): Call<ListHistoryItem>
}
