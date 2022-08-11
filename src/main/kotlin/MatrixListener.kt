import util.MatrixConnection
import java.util.*
import java.util.logging.Level
import kotlin.concurrent.fixedRateTimer

object MatrixListener : ISendMessage {

    private var connection: MatrixConnection? = null

    fun updateConnection(
        serverName: String,
        roomID: String,
        username: String,
        password: String,
        manageWhiteList: Boolean,
        bridge: MatrixBridge
    ) {
        try {
            connection = MatrixConnection(serverName, roomID, username, password)
            bridge.logger.log(Level.INFO, "Login successful")
            connection!!.sendMessage("bot login")

            connection!!.poll { _, _ -> }

            while (true) {
                try {
                    connection!!.poll { matrixName, message ->

                        val trimmedMessage = message.trimStart().trimEnd()
                        if (trimmedMessage.startsWith("whitelist set") && bridge.config.getBoolean("manage_whitelist")) {
                            val split = trimmedMessage.split(" ")
                            if (split.size != 3)
                                connection!!.sendMessage("malformed whitelist set request, please use `whitelist set name`")
                            else
                                WhitelistManager.updateWhiteList(matrixName, split.last())
                        }

                        MinecraftListener.sendChatMessage(
                            "[${connection!!.getDisplayName(matrixName)}]: $message"
                        )
                    }
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                    connection!!.disconnect()
                    bridge.logger.log(Level.WARNING, "Exception thrown while running, trying to reconnect")
                    updateConnection(serverName, roomID, username, password, manageWhiteList, bridge)
                }
            }

        } catch (exception: Exception) {
            bridge.logger.log(Level.WARNING, exception.message)
            exception.printStackTrace()
            bridge.logger.log(Level.WARNING, "Exception thrown while trying to connect, reconnecting in 30s")
            Thread.sleep(1000 * 30)
            updateConnection(serverName, roomID, username, password, manageWhiteList, bridge)
            exception.printStackTrace()
        }
    }


    fun logOut() {
        connection ?: return

        connection!!.sendMessage("bot logout")
        connection!!.disconnect()

        connection = null
    }


    override fun sendChatMessage(message: String) {
        connection?.sendMessage(message)
    }
}