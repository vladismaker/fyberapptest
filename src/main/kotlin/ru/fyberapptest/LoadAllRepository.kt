package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import ru.fyberapptest.dto.Task
import ru.fyberapptest.dto.User

interface LoadAllRepository {
    fun getAll(): MutableList<User>
    fun getTasksForUser(userId:String): MutableList<Task>

    fun clearPeopleTable()

    fun getUser(userId: String):User?
}