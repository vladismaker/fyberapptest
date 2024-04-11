package ru.fyberapptest

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import ru.fyberapptest.dto.CallbackData
import ru.fyberapptest.plugins.*
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

    return DriverManager.getConnection(dbUrl, username, password)
}


