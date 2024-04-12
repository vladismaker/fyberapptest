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
import kotlin.random.Random
import java.util.UUID


fun Application.configureRouting(connection: Connection) {

    val saveRepository: SaveRepository = DatabaseSaveRepository(connection)
    val loadRepository: LoadAllRepository = DatabaseLoadAllRepository(connection)

    routing {
        val connections = mutableListOf<WebSocketSession>()

        get("/message") {
            call.respondText("Влад молодец!", ContentType.Text.Plain)
        }

        get("/getAll") {
            val listAllUsers:MutableList<User> = loadRepository.getAll()

            val json2: String = Json.encodeToString(listAllUsers)

            println("$$$$$$$$$$$$$$$$$$$$$$$$ $json2")

            connections.forEach { session ->
                session.send(Frame.Text(json2))
            }

            call.respond(Frame.Text(json2))
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

                            val userId = message.userId
                            println("?????????????????User ID: $userId")

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



        // Route to handle Fyber callback
        get("/fyber-callback") {
            // Handle Fyber callback



            val randomUserId = listOf("111", "222")
            val randomAmount = listOf("100", "200", "300")

            // Example: Get data from Fyber callback
            val sid = call.parameters["sid"]?: Random.nextInt(3000000).toString()
            val userId = call.parameters["uid"]?: randomUserId[Random.nextInt(2)]
            val amount = call.parameters["amount"]?: randomAmount[Random.nextInt(3)]

            //Получить список из базы данных
            val list:MutableList<Task> = loadRepository.getTasksForUser(userId)
            //Создать объект
            val task = Task(sid, amount)
            //Добавить в лист
            list.add(task)
            //Перевести лист в JSON
            val json: String = Json.encodeToString(list)
            //val json = Json.encodeToString(callbackData)
            //Отправить сообщением

            println("Received callback from Fyber:")
            println("sid: $sid, userId: $userId, amount: $amount")

            // Send data to connected WebSocket clients
            val message = "Fyber callback: sid=$sid, userId=$userId, amount=$amount"
            connections.forEach { session ->
                session.send(Frame.Text(json))
            }

            //Добавить его в баззу данных
            saveRepository.save(User(userId, list))

            val listAllUsers:MutableList<User> = loadRepository.getAll()

            val json2: String = Json.encodeToString(listAllUsers)

            connections.forEach { session ->
                session.send(Frame.Text(json2))
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun setDataBase(){
    val databaseUrl = System.getenv("DATABASE_URL")

    // Парсим URL для извлечения параметров подключения
    val dbUri = URI(databaseUrl)
    val username = dbUri.userInfo.split(":")[0]
    val password = dbUri.userInfo.split(":")[1]
    val dbUrl = "jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}"

    // Список объектов Person, которые нужно сохранить в базе данных
    var list:MutableList<CallbackData> = mutableListOf()

    // Подключение к базе данных
    DriverManager.getConnection(dbUrl, username, password).use { connection ->
        // SQL запрос для вставки данных
        val sql = "INSERT INTO people (id, name, age) VALUES (?, ?, ?)"

        // Подготовка SQL запроса
        connection.prepareStatement(sql).use { statement ->
            // Цикл по списку объектов Person для вставки каждого из них в базу данных
            for (person in list) {
                // Установка значений параметров запроса
                statement.setString(1, person.sid)
                statement.setString(2, person.userId)
                statement.setString(3, person.amount)

                // Выполнение запроса
                statement.executeUpdate()
            }
        }
    }

    println("Данные успешно сохранены в базе данных.")
}


