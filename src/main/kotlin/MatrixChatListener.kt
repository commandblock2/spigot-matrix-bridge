import util.MatrixConnection
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer
import kotlin.math.log

object MatrixChatListener : ISendMessage {

    private var connection: MatrixConnection? = null
    private var pollThread: Timer? = null


    fun updateConnection(
        serverName: String,
        roomID: String,
        username: String,
        password: String,
        pollInterval: Int,
        logger: Logger
    ) {
        try {
            connection = MatrixConnection(serverName, roomID, username, password)

            pollThread?.cancel()

            pollThread = fixedRateTimer(name = "poll thread", daemon = false,
                startAt = Date(), period = pollInterval.toLong()
            ) {
                try {
                    connection!!.poll { username, message ->
                        MinecraftChatListener.sendChatMessage(
                            // username is guaranteed to not have a @ or :
                            "[${username.substringAfter('@').substringBefore(':')}]: $message"
                        )
                    }
                } catch (ignored: Exception) {
                    updateConnection(serverName, roomID, username, password, pollInterval, logger)
                }

            }
        } catch (exception: Exception) {
            logger.log(Level.WARNING, exception.message)
        }

    }


    override fun sendChatMessage(message: String) {
        connection?.sendMessage(message)
    }
}