package com.rz.tbcheck.service

interface ApiService {
    /*@POST("login")
    fun login(
        @Body body: LoginBody
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body body: RegisterBody
    ): Call<RegisterResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") authToken: String
    ): Call<GetAllStoriesResponse>

    @GET("stories")
    fun getStoriesWithPaging(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<GetAllStoriesResponse>

    @GET("stories?location=1")
    fun getStoriesWithLocation(
        @Header("Authorization") authToken: String
    ): Call<GetAllStoriesResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>

    @Multipart
    @POST("stories")
    fun addStoryWithLocation(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float,
    ): Call<FileUploadResponse>*/
}
