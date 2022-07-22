import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerLoginEvent

object MinecraftListener : Listener, ISendMessage {

    var manageWhitelist: Boolean = false

    override fun sendChatMessage(message: String) {
        Bukkit.getServer().broadcastMessage(message)
    }

    @EventHandler
    fun onMinecraftChat(chatEvent: AsyncPlayerChatEvent) {
        Thread { MatrixListener.sendChatMessage("<${chatEvent.player.name}> ${chatEvent.message}") }.start()
    }

    @EventHandler
    fun onPlayerJoin(loginEvent: PlayerLoginEvent) {
        if (manageWhitelist && !WhitelistManager.isInWhiteList(loginEvent.player.name))
            loginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,
                "You are not in whitelist. Please type `whitelist set player-name` in the matrix server")
    }
}