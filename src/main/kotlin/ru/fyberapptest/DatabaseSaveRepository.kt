package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import java.sql.Connection

class DatabaseSaveRepository(private val connection: Connection) : SaveRepository {
    override fun save(person: CallbackData) {
        val sql = "INSERT INTO people (id, name, age) VALUES (?, ?, ?)"
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, person.sid)
            statement.setString(2, person.userId)
            statement.setString(3, person.amount)
            statement.executeUpdate()
        }
    }
}