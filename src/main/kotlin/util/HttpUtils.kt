package util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection

object HttpUtils {

    val gson = GsonBuilder().setLenient().create()

    fun requestWithBearer(
        url: String, method: String,
        payload: JsonObject = JsonObject(),
        requestProperties: JsonObject = JsonObject(),
        timeout: Int = 100,
        ast: String
    ): JsonObject {
        requestProperties.addProperty("Authorization", "Bearer $ast")
        return request(url, method, payload, requestProperties, timeout)
    }

    fun request(
        url: String, method: String,
        payload: JsonObject = JsonObject(),
        requestProperties: JsonObject = JsonObject(),
        timeout: Int = 100
    ): JsonObject {
        val urlAsURL = URL(url)
        val uri = URI(
            urlAsURL.protocol,
            urlAsURL.userInfo,
            urlAsURL.host,
            urlAsURL.port,
            urlAsURL.path,
            urlAsURL.query,
            urlAsURL.ref
        )
        val connection = URL(uri.toASCIIString()).openConnection() as HttpsURLConnection

        connection.requestMethod = method
        connection.connectTimeout = timeout
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
        connection.doOutput = true

        requestProperties
            .entrySet()
            .forEach { entry -> connection.setRequestProperty(entry.key, entry.value.asString) }


        if (method != "GET")
            connection
                .outputStream
                .write(payload.toString().toByteArray(Charsets.UTF_8))


        val inputStream =
            if (connection.contentEncoding == "gzip")
                GZIPInputStream(connection.inputStream)
            else
                connection.inputStream

        val response = StringBuilder()
        BufferedReader(InputStreamReader(inputStream, "utf-8")).use { br ->
            var responseLine: String?
            while (br.readLine().also { responseLine = it } != null) {
                response.append(responseLine!!.trim { it <= ' ' })
            }
        }

        connection.disconnect()

        return gson.fromJson(response.toString(), JsonElement::class.java).asJsonObject
    }
}