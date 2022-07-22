import util.MatrixConnection
import java.util.*
import java.util.logging.Level
import kotlin.concurrent.fixedRateTimer

object MatrixListener : ISendMessage {

    private var connection: MatrixConnection? = null
    private var pollThread: Timer? = null


    fun updateConnection(
        serverName: String,
        roomID: String,
        username: String,
        password: String,
        pollInterval: Int,
        manageWhiteList: Boolean,
        bridge: MatrixBridge
    ) {
        try {
            connection = MatrixConnection(serverName, roomID, username, password)
            bridge.logger.log(Level.INFO, "Login successful")
            connection!!.sendMessage("bot login")

            pollThread?.cancel()

            pollThread = fixedRateTimer(name = "poll thread", daemon = false,
                startAt = Date(), period = pollInterval.toLong()
            ) {
                try {
                    connection!!.poll { username, message ->

                        if (message.startsWith("whitelist set") && bridge.config.getBoolean("manage_whitelist")) {
                            val split = message.split(" ")
                            if (split.size != 3)
                                connection!!.sendMessage("malformed whitelist set request, please use `whitelist set name`")
                            else
                                WhitelistManager.updateWhiteList(username, split.last())
                        }

                        MinecraftListener.sendChatMessage(
                            // username is guaranteed to not have a @ or :
                            "[${username.substringAfter('@').substringBefore(':')}]: $message"
                        )
                    }
                } catch (ignored: Exception) {
                    bridge.logger.log(Level.WARNING, "Matrix connection exception: ${ignored.stackTrace}")
                    updateConnection(serverName, roomID, username, password, pollInterval, manageWhiteList, bridge)
                }

            }
        } catch (exception: Exception) {
            bridge.logger.log(Level.WARNING, exception.message)
        }

    }

    fun logOut() {
        pollThread?.cancel()
        pollThread = null

        connection?.sendMessage("bot logout")
        connection?.disconnect()
    }


    override fun sendChatMessage(message: String) {
        connection?.sendMessage(message)
    }
}