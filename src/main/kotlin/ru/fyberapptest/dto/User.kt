package ru.fyberapptest.dto

data class User(val userId: String, val tasks: MutableList<Task>)