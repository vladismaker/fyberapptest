package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import ru.fyberapptest.dto.User
import java.sql.Connection

class DatabaseSaveRepository(private val connection: Connection) : SaveRepository {
/*    override fun save(person: CallbackData) {
        val sql = "INSERT INTO people (sid, userId, amount) VALUES (?, ?, ?)"
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, person.sid)
            statement.setString(2, person.userId)
            statement.setString(3, person.amount)
            statement.executeUpdate()
        }
    }*/
    override fun save(user: User) {
        val sql = "INSERT INTO people (userId, sid, amount) VALUES (?, ?, ?)"
        connection.prepareStatement(sql).use { statement ->
            user.tasks.forEach { task ->
                statement.setString(1, user.userId)
                statement.setString(2, task.sid)
                statement.setString(3, task.amount)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }
}