package ru.fyberapptest.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

        // Маршрут для обработки callback от Fyber
        get("/fyber-callback") {
            //val earning = call.receive<Earning>()
            //println("callback earning:$earning")

            val requestBody = call.receiveText()
            println("Received callback from Fyber: $requestBody")

            // Распарсим строку запроса для извлечения параметров
            val params = requestBody.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }

            // Извлечение количества полученных монет (amount)
            val amountOfCoins = params["amount"]

            if (amountOfCoins != null) {
                println("Amount of coins received: $amountOfCoins")
            } else {
                println("Amount of coins not found in the request")
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}
