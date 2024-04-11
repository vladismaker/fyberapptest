package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData

interface LoadAllRepository {
    fun getAll(): MutableList<CallbackData>
}