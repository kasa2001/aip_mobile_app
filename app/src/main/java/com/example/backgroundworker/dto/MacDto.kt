package com.example.backgroundworker.dto

import com.google.gson.annotations.SerializedName

data class MacDto(
    @SerializedName("mac_address") val mac: String,
)
