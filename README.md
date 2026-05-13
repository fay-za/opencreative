<div align="center">

<img src="https://i.imgur.com/rcYzFd2.png" alt="drawing" width="600"/>

[![bStats Servers](https://img.shields.io/bstats/servers/22001?color=blue)](https://bstats.org/plugin/bukkit/OpenCreative/22001)
[![bStats Players](https://img.shields.io/bstats/players/22001?=blue)](https://bstats.org/plugin/bukkit/OpenCreative/22001)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Discord](https://img.shields.io/discord/1300864436076286069.svg?label=Discord&color=purple)](https://discord.com/invite/sSFCXUeq63)
[![Wiki](https://img.shields.io/badge/Wiki-Gitlab-orange)](https://gitlab.com/eagles-creative/opencreative/-/wikis/home)

# Let your players create the worlds

Minecraft plugin for PaperMC servers that allows players to create their worlds.

### [Showcase Video](https://www.youtube.com/watch?v=dXLuH8MwmJ8) - [Modrinth](https://modrinth.com/plugin/opencreative) - [Hangar](https://hangar.papermc.io/mcchickenstudio/OpenCreative) - [Builds](https://gitlab.com/eagles-creative/opencreative/-/packages) - [Installation](https://gitlab.com/eagles-creative/opencreative/-/wikis/home/Installation) - [Permissions](https://gitlab.com/eagles-creative/opencreative/-/wikis/Home/Permissions)

![Screenshot](https://i.imgur.com/4jzB4F9.png)

</div>

## Features

- An **unique gamemode**, where **players can build** and even **create mini-games with coding**.
 It's completely new experience compared to classic Creative servers with plots.

- **A lot of options for worlds generation**: players can choose terrain (Flat, Empty, Ocean, Normal) and
 environment (Overworld, Nether, The End). You can add own custom generators (Flat, Folders) in config.yml.

- **Players can easily change their worlds** with settings. They can set a time, weather, game rules and world's name, description,
 icon and even set custom ID for join command.

- **It's better to create together**, because world owners can add players to builders, developers, white list or even ban list.
 Not trusted players will lose their permissions, when owner leaves the server.

- **Coding on blocks** allows players to create mini-games. With simple and friendly syntax, 120+ events, 320+ actions, 90 conditions, 
 9 values and variables it's easy to code something cool.

- **Built-in security things**. Plugin has redstone limiter, bad items fixer and lobby protection. 
 You can achieve more security by using [WorldGuard](https://dev.bukkit.org/projects/worldguard), [Panilla](https://www.spigotmc.org/resources/panilla-prevent-hacked-items.65694/),
 [CoreProtect](https://modrinth.com/plugin/coreprotect) and other plugins. 

- Change messages, items names, descriptions and even plugin sounds as you want, because it's **translatable**. By default we have English and Russian translations.

- You can get donations by players by adding **player groups** in config.yml with custom limits, cooldowns,
  lobby permissions, world permissions, world advertisement price and like reward amount.

- Be able to **execute any commands by console or player** on different events: Lobby Teleport, World Join, World Quit, World Chat,
  Global Chat, Maintenance Start, Maintenance End. Useful, if you have some plugins (Skript, MyCommand) to do your own things without 
  rewriting the plugin.

- Add more features with other plugins:
  - [Vault](https://www.spigotmc.org/resources/vault.34315/) - Adds economy support.
  - [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) - Adds coding chests animations, glowing blocks while selecting location.
  - [LibsDisguises](https://www.spigotmc.org/resources/libs-disguises-free.81/) - Adds entity disguise action.
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - Adds placeholders and parses them in some messages (world join, world icon).

- Plugin **developers can easily add own** [world generators](https://gitlab.com/eagles-creative/opencreative/-/wikis/home/Configuration/World-Generators), [coding platformers](https://gitlab.com/eagles-creative/opencreative/-/wikis/Plugin-Development/Coding-Platformers), [coding placeholders, coding game values](https://gitlab.com/eagles-creative/opencreative/-/wikis/Plugin-Development/Adding-new-features)
 and [set their managers](https://gitlab.com/eagles-creative/opencreative/-/wikis/Plugin-Development/Managers) (economy, packets). Also we have our plugin events (Lobby Teleport, World Chat, Global Chat, Maintenance Start/End,
 4 module events, 10 world events), so you can listen on them,
 maybe cancel and execute your code. 

 - You're welcome to create forks of this plugin, because it's **open-sourced**. Download source code, edit it, publish it
 and use on your own servers.

## Usage

### Creating a world
Players can create their worlds using 4 different generators:
- Plains.
- Flat world.
- Ocean.
- Void.

You can change world's sizes with player's permissions in config.yml.

![](https://i.imgur.com/sID0dxv.png)

![World Creation](https://gitlab.com/eagles-creative/opencreative/-/wikis/uploads/82d2118f4aee556c146a22b1419ed0df/WorldCreation.mp4)

### Browsing worlds
Players can search, sort and like worlds. World can be found by name or ID. Also worlds list can be sorted by:
- Categories (Fighting, Sandbox, Arcade...)
- Amount of online players in world.
- Creation time.

![](https://i.imgur.com/mL6FS4o.png)

### World's settings
Every owner of world can change in his world:
- Name
- Description
- Category
- ID
- Flags
- Icon Item

![](https://i.imgur.com/iMJjFh0.png)

![World Settings](https://gitlab.com/eagles-creative/opencreative/-/wikis/uploads/9a1cfb155d1391f4cba17be23a1fc13a/WorldSettings.mp4)

### Coding
Players can create code for their worlds with coding blocks and run it.

![](https://i.imgur.com/1SkIjhv.png)

![Coding World](https://gitlab.com/eagles-creative/opencreative/-/wikis/uploads/1eda4a8c0ee3dcdb4b9a23954fa6d7e6/DevelopersWorld.mp4)

Coding blocks will be parsed into codeScript.yml.
Example: Send a message "Hello world!"

```yaml
code:
  blocks:
    exec_block_4_1:
      category: EVENT_PLAYER
      type: PLAYER_JOIN
      actions:
        action_block1:
          category: PLAYER_ACTION
          type: PLAYER_SEND_MESSAGE
          arguments:
            type:
              type: TEXT
              value: join-spaces
            messages:
              type: LIST
              value:
                '1':
                  type: TEXT
                  value:
                    name: Hello
                '2':
                  type: TEXT
                  value:
                    name: World
```

## Commands

### `/creative`
Without arguments opens a menu with information about OpenCreative+.
- `reload` - reloads configuration and localization files.
- `resetlocale` - resets localization file to default from plugin.
- `load [world id]` - loads specified world.
- `unload [world id]` - unloads specified world.
- `list` - displays list of loaded worlds on server.
- `deprecated` - displays list of deprecated worlds, whose owner haven't logged in for a month.
- `corrupted` - displays list of corrupted worlds, that lost settings.yml.
- `maintenance start [seconds]` - start maintenance mode in next [seconds], that disallows players without permissions to create or join worlds.
- `maintenance stop` - stop maintenance mode, players will be able to create and join worlds.
- `creative-chat clear` - clears chat for every player. Useful to hide some inappropriate messages.
- `creative-chat disable` - disables creative chat for players, that don't have bypass permission, they can't send messages in /cc chat.
- `creative-chat enable` - enables creative chat for players, they will be able to speak in creative chat again.
- `kick-all starts [nickname part]` - kicks players with nicknames that start with part. Useful for kicking bots.
- `kick-all ends [nickname part]` - kicks players with nicknames that end with part. Useful for kicking bots.
- `kick-all contains [nickname part]` - kicks players with nicknames that contain part. Useful for kicking bots.
- `kick-all ignore [nickname] [nickname2]...` - kicks everyone except command sender and specified players.

### `/cc` - Creative Chat
Creative chat is global chat where players can send messages and speak with everyone.
- `[message]` - send messages. It supports colors!
- `off` - hides creative chat messages only for command sender.
- `on` - shows creative chat messages for command sender.

### `/world`
- `info` - displays information about world, where command sender currently is in.
- `delete` - deletes world.
- `deletemobs` - opens a menu where owner can delete mobs from world.

### `/ad`
Without arguments advertises world, where command sender currently is in. World advertisement is a clickable message with world information that will be sent to every player.
- `[world id]` - teleports command sender to world.

### `/play`
Changes world's mode to Play mode.

### `/build`
Changes world's mode to Build mode.

### `/dev`
Teleports command sender to developer world.

### `/like`
Increases reputation of world, where command sender currently is in.

### `/dislike`
Decreases reputation of world, where command sender currently is in.

### `/join`
- `[world id]` - teleports command sender to world.

### `/locate`
- `[player name]` - locates in which world player currently plays.

### `/spawn`
Without arguments teleports command sender to spawn world.
- `[player name]` - teleports specified player to spawn world.

### `/environment`
- `variables list` - shows list of all variables in world.
- `variables size` - outputs amount of all variables in world.
- `variables clear` - clears all variables.
- `debug enable` - enables debug mode for world. It sends messages about events and action arguments.
- `debug disable` - disables debug mode for world.

### Minecraft commands
These commands overwrite minecraft and EssentialsX commands.
They will work only if command sender is owner of world, or if he has bypass permission. Using commands from console will redirect to default /minecraft:command.

- `/gamemode [player name] [game mode]` - changes player's game mode.
- `/gamemode [game mode]` - changes player's game mode.
- `/teleport [x] [y] [z] [yaw] [pitch]` - teleports sender to specified coordinates.
- `/teleport [player name] [x] [y] [z] [yaw] [pitch]` - teleports specified player to specified coordinates.
- `/give [player name] [item material] [amount]` - gives item to specified player.
- `/playsound [player name] [sound name] [volume] [pitch]` - plays a sound for player.
- `/stopsound [player name] [sound category]` - stops a playing sounds for player.
- `/weather [rain/thunder/sun]` - changes weather in world.
- `/time [set/add] [ticks]` - changes time in world.


## Development

To develop this plugin, you can fork it, then simply download project sources and use Intellij IDEA to code.

We use Maven to compile plugin into jar file.

Contribute your code into [`development branch`](https://gitlab.com/eagles-creative/opencreative/-/tree/development), then we will check it and we create merge request to the Main branch.

## Statistic

[<img src="https://bstats.org/signatures/bukkit/OpenCreative.svg" height="250" />](https://bstats.org/plugin/bukkit/OpenCreative/22001)

## License

This plugin is licensed under GNU GPL v3, because it uses Paper API. When you use or edit source code of this plugin, please open your source code.

[![GPLv3 Logo](https://www.gnu.org/graphics/gplv3-with-text-136x68.png)](https://www.gnu.org/licenses/gpl-3.0.txt)
&nbsp; &nbsp; &nbsp; &nbsp;

## Special thanks

### ❤️ Thanks to every server, that uses this plugin.

Thank you, JetBrains, for providing most powerful tool to develop with Java. 

[![IntelliJ IDEA Logo](https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.svg)](https://www.jetbrains.com/idea/) 
&nbsp; &nbsp; &nbsp; &nbsp; 

Thank you, PaperMC Team, for providing stable API and support.

Thank you, developers of ReActions, ProtocolLib, Vault, PlaceholderAPI, LibsDisguises.

## Credits

OpenCreative+ is made by McChicken Studio 2017-2025.

**Contributors:**
- McChicken Team
- onn512
- pawsashatoy
- LWJENNI
- HACKERPRO17
- liventmst

**Testers:**
- tokkyo35

**Translators:**
- Nagibator6000LoL
- initzero
- DrakesWeb
- kogtyv
- Senmpai333
## Support
Report any issues and send your ideas in **[our Discord server](https://discord.com/invite/sSFCXUeq63)**.