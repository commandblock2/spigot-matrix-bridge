package util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MatrixConnection(
    private val serverName: String,
    private val roomID: String,
    private val username: String,
    password: String
) {
    private val accessToken: String
    private var cursor: String = ""

    private val displayNameMap = HashMap<String, String>()

    init {
        // login
        val loginData = JsonObject()
        loginData.addProperty("type", "m.login.password")
        loginData.addProperty("user", username)
        loginData.addProperty("password", password)

        val loginResponse = HttpUtils.request(
            "$serverName/_matrix/client/api/v1/login",
            "POST",
            payload = loginData
        )

        accessToken = loginResponse.get("access_token").asString

    }

    private fun fetchMessages(): JsonArray {

        return try {

            val response = HttpUtils.request(
                "$serverName/_matrix/client/r0/sync?" +
                        "filter={" +
                        "\"room\": {" +
                        "\"rooms\" : [\"$roomID\"]," +
                        "\"timeline\": {" +
                        "\"types\": [\"m.room.message\"]," +
                        "\"not_senders\": [\"$username\"]" +
                        "}}}&" +
                        (if (cursor.isNotEmpty()) "since=$cursor&" else "") +
                        "access_token=$accessToken&timeout=30000",
                "GET",
                timeout = 60000
            )

            cursor = response.get("next_batch").asString

            val roomData = response
                .get("rooms").asJsonObject
                .get("join").asJsonObject


            roomData.get(roomID).asJsonObject
                .get("timeline").asJsonObject
                .get("events").asJsonArray
        } catch (exception: Exception) {
            when (exception) {
                is SocketTimeoutException, is java.lang.NullPointerException ->
                    JsonArray()
                else -> throw exception
            }
        }

    }

    fun getDisplayName(id: String): String {
        if (!displayNameMap.contains(id))
            displayNameMap[id] = HttpUtils.request("$serverName/_matrix/client/r0/profile/$id/displayname", "GET")
                .get("displayname").asString

        return displayNameMap[id]!!
    }

    fun sendMessage(message: String): JsonObject {
        val messagePayload = JsonObject()
        messagePayload.addProperty("msgtype", "m.text")
        messagePayload.addProperty("body", message)

        return HttpUtils.requestWithBearer(
            "$serverName/_matrix/client/api/v1/rooms/$roomID/send/m.room.message",
            "POST",
            token = accessToken,
            payload = messagePayload
        )
    }

    fun poll(action: (String, String) -> Unit) {
        fetchMessages().forEach {
            val sender = it.asJsonObject.get("sender").asString
            val message = it.asJsonObject.get("content").asJsonObject.get("body").asString

            action(sender, message)
        }
    }

    fun disconnect() {
        HttpUtils.requestWithBearer("$serverName/_matrix/client/v3/logout", "POST", token = accessToken)
    }
}