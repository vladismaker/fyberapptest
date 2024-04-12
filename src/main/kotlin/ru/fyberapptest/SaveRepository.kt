package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import ru.fyberapptest.dto.User

interface SaveRepository {
    fun save(user: User)
}