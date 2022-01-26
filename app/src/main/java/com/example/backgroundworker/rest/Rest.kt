package com.example.backgroundworker.rest

import com.example.backgroundworker.dto.ConfigDto
import com.example.backgroundworker.dto.MacDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Rest {

    @Headers("Content-Type: application/json")
    @POST("/api/config")
    fun config(@Body macDto: MacDto): Call<ConfigDto>
}