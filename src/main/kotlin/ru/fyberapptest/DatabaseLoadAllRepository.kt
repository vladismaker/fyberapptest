package ru.fyberapptest

import ru.fyberapptest.dto.CallbackData
import java.sql.Connection

class DatabaseLoadAllRepository (private val connection: Connection) : LoadAllRepository {
    override fun getAll(): MutableList<CallbackData> {
        val sql = "SELECT id, name, age FROM people"
        val people = mutableListOf<CallbackData>()
        connection.prepareStatement(sql).use { statement ->
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val sid = resultSet.getString("sid")
                val userId = resultSet.getString("userId")
                val amount = resultSet.getString("amount")
                people.add(CallbackData(sid, userId, amount))
            }
        }
        return people
    }
}