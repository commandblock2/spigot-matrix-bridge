import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class MatrixBridge : JavaPlugin() {

    override fun onEnable() {

        saveDefaultConfig()

        WhitelistManager.loadMapping(File(dataFolder, "whitelist.txt"))

        Thread {
            MatrixListener.updateConnection(
                config.getString("server")!!,
                config.getString("room_id")!!,
                config.getString("user_name")!!,
                config.getString("password")!!,
                config.getBoolean("manage_whitelist"),
                this,
            )
        }.start()

        MinecraftListener.manageWhitelist = config.getBoolean("manage_whitelist")
        server.pluginManager.registerEvents(MinecraftListener, this)
    }

    override fun onDisable() {
        MatrixListener.logOut()
        WhitelistManager.saveMapping()
    }
}