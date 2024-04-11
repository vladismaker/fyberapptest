package ru.fyberapptest.dto

import kotlinx.serialization.Serializable

@Serializable
data class CallbackData(
    val sid: String,
    val userId: String,
    val amount: String
)