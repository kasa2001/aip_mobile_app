package com.example.backgroundworker.dto

import com.google.gson.annotations.SerializedName

data class ConfigDto (
    @SerializedName("actions") val action: List<Action>,
)