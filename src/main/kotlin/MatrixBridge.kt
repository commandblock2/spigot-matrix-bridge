import org.bukkit.plugin.java.JavaPlugin

class MatrixBridge : JavaPlugin() {

    override fun onEnable() {
//        config.addDefault("server", "https://matrix.server:8448")
//        config.addDefault("room_id", "!ZJNSsOscMMCydGSmuC:matrix.server")
//
//        config.addDefault("user_name", "username")
//        config.addDefault("password", "password")
//
//        config.addDefault("poll_interval", 1000)

        saveDefaultConfig()

        Thread {
            MatrixChatListener.updateConnection(
                config.getString("server")!!,
                config.getString("room_id")!!,
                config.getString("user_name")!!,
                config.getString("password")!!,
                config.get("poll_interval")!!.toString().toInt(),
                logger,
            )
        }.start()

        server.pluginManager.registerEvents(MinecraftChatListener, this)
    }
}