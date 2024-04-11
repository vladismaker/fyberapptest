package ru.fyberapptest.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val userId: String
)