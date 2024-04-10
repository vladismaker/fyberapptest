package ru.fyberapptest

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.fyberapptest.plugins.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt()){
        configureRouting()
    }.start(wait = true)
}
