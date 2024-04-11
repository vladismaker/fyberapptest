package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData

interface SaveRepository {
    fun save(person: CallbackData)
}