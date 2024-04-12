package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import ru.fyberapptest.dto.Task
import ru.fyberapptest.dto.User
import java.sql.Connection

class DatabaseLoadAllRepository (private val connection: Connection) : LoadAllRepository {
    override fun clearPeopleTable() {
        val sql = "DELETE FROM people"
        connection.prepareStatement(sql).use { statement ->
            statement.executeUpdate()
        }
    }

    override fun getAll(): MutableList<User> {
        val sql = "SELECT userId, sid, amount FROM people"
        val users = mutableListOf<User>()
        //val tasks = mutableListOf<Task>()
        connection.prepareStatement(sql).use { statement ->
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val userId = resultSet.getString("userId")
                val sid = resultSet.getString("sid")
                val amount = resultSet.getString("amount")
                // Проверяем, есть ли уже пользователь с таким userId в списке
                val existingUser = users.find { it.userId == userId }
                if (existingUser != null) {
                    // Если пользователь уже есть, добавляем задачу к его списку
                    existingUser.tasks.add(Task(sid, amount))
                } else {
                    // Если пользователя еще нет, создаем нового пользователя и добавляем задачу к его списку
                    val newUser = User(userId, mutableListOf(Task(sid, amount)))
                    users.add(newUser)
                }
            }
        }
        return users
    }

    override fun getTasksForUser(userId: String): MutableList<Task> {
        val sql = "SELECT sid, amount FROM people WHERE userId = ?"
        val tasks = mutableListOf<Task>()
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, userId)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val sid = resultSet.getString("sid")
                val amount = resultSet.getString("amount")
                tasks.add(Task(sid, amount))
            }
        }
        return tasks
    }

    override fun getUser(userId: String): User? {
        val sql = "SELECT userId, sid, amount FROM people WHERE userId = ?"
        var user: User? = null
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, userId)
            val resultSet = statement.executeQuery()
            val tasks = mutableListOf<Task>()
            while (resultSet.next()) {
                if (user == null) {
                    user = User(userId, tasks)
                }
                val sid = resultSet.getString("sid")
                val amount = resultSet.getString("amount")
                tasks.add(Task(sid, amount))
            }
        }
        return user
    }
}