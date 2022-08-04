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

                        if (message.startsWith("whitelist set") && bridge.config.getBoolean("manage_whitelist")) {
                            val split = message.split(" ")
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
                    bridge.logger.log(Level.WARNING, "Matrix connection exception: ${ignored.stackTrace.contentToString()}")
                    updateConnection(serverName, roomID, username, password, manageWhiteList, bridge)
                }
            }

        } catch (exception: Exception) {
            bridge.logger.log(Level.WARNING, exception.message)
        }
    }


    fun logOut() {

        connection?.sendMessage("bot logout")
        connection?.disconnect()
    }


    override fun sendChatMessage(message: String) {
        connection?.sendMessage(message)
    }
}