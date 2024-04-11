package ru.fyberapptest

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import ru.fyberapptest.plugins.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080 // Порт 8080 по умолчанию
    println("port:$port")
    embeddedServer(Netty, port = port) {
        install(WebSockets)
        configureRouting()
    }.start(wait = true)
}
