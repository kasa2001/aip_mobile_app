package com.example.backgroundworker.dto

import com.google.gson.annotations.SerializedName

data class Action(
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String,
    @SerializedName("name") val name: String,
)
