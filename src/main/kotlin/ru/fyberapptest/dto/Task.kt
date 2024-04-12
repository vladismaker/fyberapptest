package ru.fyberapptest.dto

import kotlinx.serialization.Serializable

@Serializable
data class Task(val sid: String, val amount: String)
