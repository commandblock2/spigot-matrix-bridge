# Spigot-Matrix-Bridge
https://github.com/commandblock2/matrix-spigot-bridge isn't buildable.
I decided to write my own.  
The bridge is implemented as a spigot plugin, which act as a bot for **unencrypted** room.

Currently, **E2EE** is **NOT** supported.  
And message sync could potentially take longer.
No command support currently.

## Setup
1. create an account on your desired server for the bot.
2. (Optionally) create a dedicated room for the bot, but you can also use an existing room.
3. put the plugin jar(which can be found in Shithub CI) in the plugins' folder.
4. create the config at `plugins/SpigotMatrixBridge/config.yml` manually or run the server once.
### Default config
```
server: "https://matrix.server"
room_id: "!ZJNSsOscMMCydGSmuC:matrix.server"
user_name: "@username:home.server"
password: "password"
poll_interval: 1000

manage_whitelist: true
```
## Whitelist Control
Set `manage_whitelist: true`.  
Type `whitelist set username` in the room the bot is in.  
And after the bot replied, `Updated Whitelist: @user:home.server -> username`
the users is added to the whitelist.  
The whitelist is based on username not online account.  
Can be used together with AuthMeReload to prevent the players from impersonating others.
Shouldn't conflict with other plugins but IDK never tested lmao.
