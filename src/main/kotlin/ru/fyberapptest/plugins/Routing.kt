package ru.fyberapptest.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.fyberapptest.DatabaseLoadAllRepository
import ru.fyberapptest.DatabaseSaveRepository
import ru.fyberapptest.LoadAllRepository
import ru.fyberapptest.SaveRepository
import ru.fyberapptest.dto.*
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import java.util.UUID


fun Application.configureRouting(connection: Connection) {

    val saveRepository: SaveRepository = DatabaseSaveRepository(connection)
    val loadRepository: LoadAllRepository = DatabaseLoadAllRepository(connection)
    var adId:String

    routing {
        val connections = mutableListOf<WebSocketSession>()

        get("/message") {
            call.respondText("Влад молодец!", ContentType.Text.Plain)
        }

        get("/getAll") {
            val listAllUsers:MutableList<User> = loadRepository.getAll()

            val json2: String = Json.encodeToString(listAllUsers)

            println("$$$$$$$$$$$$$$$$$$$$$$$$ $json2")

/*            connections.forEach { session ->
                session.send(Frame.Text(json2))
            }*/

            call.respond(json2)
        }

        get("/getClear") {
            loadRepository.clearPeopleTable()

            val listAllUsers:MutableList<User> = loadRepository.getAll()

            val json2: String = Json.encodeToString(listAllUsers)

            println("$$$$$$$$$$$$$$$$$$$$$$$$ $json2")

            connections.forEach { session ->
                session.send(Frame.Text(json2))
            }

            call.respond(Frame.Text(json2))
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

        /*val connections = mutableListOf<WebSocketSession>()
        // Маршрут для обработки callback от Fyber
        webSocket("/fyber-callback") {
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
/*        get("/fyber-callback") {
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

        }*/



        // WebSocket endpoint
        webSocket("/wss") {
            println("!!!!!!!!!!!!!webSocket")
            connections.add(this)

            try {
                for (frame in incoming) {
                    // Handle incoming WebSocket messages if needed
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("Received message: $text")

                            // Парсинг JSON для извлечения идентификатора пользователя
                            val json = Json { ignoreUnknownKeys = true }
                            val message = json.decodeFromString<Message>(text)

                            adId = message.userId
                            println("?????????????????User ID: $adId")

                            // Здесь вы можете обработать идентификатор пользователя или выполнить другие действия в зависимости от ваших потребностей
                        }
                        // Добавляем ветку else для обработки любых других типов фреймов
                        else -> {
                            println("Received frame of type: ${frame.frameType.name}")
                        }
                    }
                }
            } finally {
                connections.remove(this)
            }
        }

        get("/fyber-callback") {
            // Handle Fyber callback

            // Example: Get data from Fyber callback
           /* val sid = call.parameters["sid"]?: "0"
            val userId = call.parameters["uid"]?: "0"
            val amount = call.parameters["amount"]?: "0"*/

            val randomUserId = listOf("111", "222")
            val randomAmount = listOf("100", "200", "300")

            val sid = call.parameters["sid"]?: Random.nextInt(3000000).toString()
            val userId = call.parameters["uid"]?: randomUserId[Random.nextInt(2)]
            val amount = call.parameters["amount"]?: randomAmount[Random.nextInt(3)]

            if (sid!="0" && userId!="0" && amount!="0"){
                // Получить пользователя из базы данных
                val user = loadRepository.getUser(userId)

                val nwDat = getDateNow()

                if (user == null) {
                    // Пользователь не существует, создаем нового пользователя с задачей
                    println("Пользователь не существует, создаем нового пользователя с задачей")
                    val newUser = User(userId, mutableListOf(Task(sid, amount, nwDat)))
                    saveRepository.save(newUser)
                } else {
                    // Пользователь существует, добавляем задачу в его массив задач
                    println("Пользователь существует, добавляем задачу в его массив задач")
                    user.tasks.add(Task(sid, amount, nwDat))
                    saveRepository.updateTasksForUser(userId, user.tasks)
                }

                // Send data to connected WebSocket clients

                val listAllUsers:MutableList<User> = loadRepository.getAll()

                val json2: String = Json.encodeToString(listAllUsers)

                println("$$$$$$$$$$$$$$$$$$$$$$$$ $json2")

                connections.forEach { session ->
                    session.send(Frame.Text(json2))
                }

                /*val listTaskForUserId:MutableList<Task> = loadRepository.getTasksForUser(userId)

                val json3: String = Json.encodeToString(listTaskForUserId)

                println("$$$$$$$$$$$$$$$$$$$$$$$$ $json3")

                connections.forEach { session ->
                    session.send(Frame.Text(json3))
                }*/

                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.BadRequest)
            }


        }
    }
}

private fun getDateNow():String{
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return currentDateTime.format(formatter)
}



