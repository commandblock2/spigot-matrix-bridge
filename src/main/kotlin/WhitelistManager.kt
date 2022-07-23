import java.io.File

object WhitelistManager {
    private val userNameMapping = HashMap<String, String>()

    private lateinit var file: File

    fun loadMapping(file: File) {
        this.file = file

        if (!file.exists())
            file.createNewFile()

        file.useLines { lines ->
            lines.forEach {
                val (matrixName, mcWhiteListString) = it.split(" ")
                userNameMapping[matrixName] = mcWhiteListString
            }
        }
    }

    fun updateWhiteList(matrixUser: String, mcName :String) {
        userNameMapping[matrixUser] = mcName

        MatrixListener.sendChatMessage("Updated Whitelist: $matrixUser -> $mcName")
        saveMapping()
    }

    fun isInWhiteList(mcName: String): Boolean {
        return userNameMapping.values.contains(mcName)
    }

    fun saveMapping() {
        val content = StringBuilder()
        userNameMapping.forEach {
            content
                .append(it.key)
                .append(" ")
                .append(it.value)
                .append("\n")
        }

        file.writeText(content.toString())
    }
}