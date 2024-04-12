package ru.fyberapptest

import ru.fyberapptest.dto.Task
import ru.fyberapptest.dto.User

interface SaveRepository {
    fun save(user: User)

    fun updateTasksForUser(userId: String, tasks: List<Task>)
}