package ru.fyberapptest.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(val userId: String, val tasks: MutableList<Task>)