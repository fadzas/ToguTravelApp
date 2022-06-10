package com.example.togutravelapp.activity.api

import com.example.togutravelapp.data.ListWisataResponse
import com.example.togutravelapp.data.ObjectWisataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("getobjek")
    fun getObjek(
        @Query("search") search: String,
    ): Call<ObjectWisataResponse>

    @GET("getwisata")
    fun getWisata():Call<ListWisataResponse>
}