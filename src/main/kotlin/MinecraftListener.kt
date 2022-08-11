import io.papermc.paper.event.player.AbstractChatEvent
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.text.PaperComponents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

object MinecraftListener : Listener, ISendMessage {

    var manageWhitelist: Boolean = false

    override fun sendChatMessage(message: String) {
        // Bukkit.getServer().broadcastMessage(message)
        Bukkit.getOnlinePlayers().forEach { it.sendMessage(message) }
        Bukkit.getConsoleSender().sendMessage(message)
    }

    @EventHandler
    fun onMinecraftChat(chatEvent: AsyncChatEvent) {
        Thread {
            MatrixListener.sendChatMessage("<${chatEvent.player.name}> ${getMessage(chatEvent.message())}")
        }.start()
    }

    @EventHandler
    fun onPlayerDeath(deathEvent: PlayerDeathEvent) {
        Thread {
            MatrixListener.sendChatMessage(
                getMessage(deathEvent.deathMessage())
            )
        }.start()
    }

    @EventHandler
    fun onPlayerQuit(playerQuitEvent: PlayerQuitEvent) {
        Thread {
            MatrixListener.sendChatMessage(getMessage(playerQuitEvent.quitMessage()))
        }.start()
    }

    @EventHandler
    fun onPlayerAdvancement(playerAdvancementDoneEvent: PlayerAdvancementDoneEvent) {
        Thread {
            MatrixListener.sendChatMessage(
                getMessage(playerAdvancementDoneEvent.message())
            )
        }
    }

    @EventHandler
    fun onPlayerJoin(loginEvent: PlayerLoginEvent) {
        if (manageWhitelist && !WhitelistManager.isInWhiteList(loginEvent.player.name))
            loginEvent.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                Component.text("You are not in whitelist. Please type `whitelist set player-name` in the matrix server")
            )
    }

    @EventHandler
    fun onPlayerLogin(joinEvent: PlayerJoinEvent) {
        Thread {MatrixListener.sendChatMessage(getMessage(joinEvent.joinMessage()))}.start()
    }

    private fun getMessage(component: Component?): String {
        component ?: return ""
        return PlainTextComponentSerializer.plainText().serialize(component)
    }
}