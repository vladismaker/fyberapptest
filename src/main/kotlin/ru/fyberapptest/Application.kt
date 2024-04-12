package ru.fyberapptest

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import ru.fyberapptest.plugins.configureRouting
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080 // Порт 8080 по умолчанию
    println("port:$port")

    embeddedServer(Netty, port = port) {
        install(WebSockets)
        configureRouting(setDataBase())
    }.start(wait = true)
}

private fun setDataBase(): Connection {
    val databaseUrl = System.getenv("DATABASE_URL")

    // Парсим URL для извлечения параметров подключения
    val dbUri = URI(databaseUrl)
    val username = dbUri.userInfo.split(":")[0]
    val password = dbUri.userInfo.split(":")[1]
    val dbUrl = "jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}"

    val connection = DriverManager.getConnection(dbUrl, username, password)

    try {
        val sql = """
                CREATE TABLE IF NOT EXISTS people (
                    sid VARCHAR(255) PRIMARY KEY,
                    userId VARCHAR(255),
                    amount VARCHAR(255),
                    date VARCHAR(255)
                )
            """.trimIndent()
        connection?.prepareStatement(sql)?.use { statement ->
            statement.execute()
        }
        println("Таблица 'people' успешно создана.")
    } catch (e: Exception) {
        println("Ошибка при создании таблицы 'people': ${e.message}")
    }

    ///
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT CURRENT_DATE")

    // Проверка результата запроса
    if (resultSet.next()) {
        val currentDate = resultSet.getDate(1)
        println("Успешное подключение к базе данных. Текущая дата: $currentDate")
        //return true
    } else {
        println("Не удалось получить текущую дату из базы данных.")
    }
    ///

    val metaData = connection.metaData
    val tables = metaData.getTables(null, null, null, arrayOf("TABLE"))

    // Проверка наличия таблицы в списке
    while (tables.next()) {
        val existingTableName = tables.getString("TABLE_NAME")
        if (existingTableName.equals("people", ignoreCase = true)) {
            println("Таблица 'people' найдена в базе данных.")
            //return true
        }
    }

    //println("Таблица 'people' не найдена в базе данных.")

    ///
    return connection
}


