package ru.fyberapptest.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import ru.fyberapptest.dto.Earning

fun Application.configureRouting() {

    routing {
        get("/message") {
            call.respondText("Влад молодец!", ContentType.Text.Plain)
        }

        get("/earnings/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

            println(userId)

/*            val earnings = transaction {
                EarningDAO.find { EarningsTable.userId eq userId }.map { it.toEarning() }
            }*/

            call.respond("earnings")
        }

        // Маршрут для получения текущего баланса пользователя по его ID
        get("/balance/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

/*            val user = transaction {
                UserDAO.findById(userId)?.toUser()
            }*/

/*            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond("user.balance")
            }*/

            call.respond("user.balance")
        }

        val connections = mutableListOf<WebSocketSession>()
        // Маршрут для обработки callback от Fyber
/*        webSocket("/fyber-callback") {
            println("!!!!!!!!!!!!!!!!!!!!!!!!!WebSocket connection established")
            connections.add(this)
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println("!!!!!!!!!!!!!!!!!!!!!!!!!Received callback from Fyber: $receivedText")

                    // Send received data to all connected clients
                    connections.forEach { session ->
                        session.send(Frame.Text("!!!!!!!!!!!!!!!!!!!!!!!!!Received callback from Fyber: $receivedText"))
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("!!!!!!!!!!!!!!!!!!!!!!!!!WebSocket connection closed")
            } finally {
                connections.remove(this)
            }

        }*/
        get("/fyber-callback") {
            //val earning = call.receive<Earning>()
            //println("callback earning:$earning")

            //val requestBody = call.receiveText()
            //println("Received callback from Fyber: $requestBody")

            val sid = call.parameters["sid"]
            val userId = call.parameters["uid"]
            val amount = call.parameters["amount"]
            val currencyName = call.parameters["currency_name"]
            val currencyId = call.parameters["currency_id"]

            println("Received callback from Fyber:")
            println("sid: $sid, userId: $userId, amount: $amount, currencyName: $currencyName, currencyId: $currencyId")

        }
    }
}
