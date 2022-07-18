import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.server.BroadcastMessageEvent

object MinecraftChatListener : Listener, ISendMessage {

    override fun sendChatMessage(message: String) {
        Bukkit.getServer().broadcastMessage(message)
    }

    @EventHandler
    fun onMinecraftChat(chatEvent: AsyncPlayerChatEvent) {
        MatrixChatListener.sendChatMessage("<${chatEvent.player.name}> ${chatEvent.message}")
    }
}