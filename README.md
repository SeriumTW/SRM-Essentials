# SRM-Essentials

Essential utilities for Hytale servers.

## Features

- **Homes**: Multi-home system with `/home`, `/sethome`, `/delhome`
- **Warps**: Server warps with `/warp`, `/setwarp`, `/delwarp`
- **Spawn**: Spawn management with `/spawn`, `/setspawn`
- **TPA**: Teleport requests with `/tpa`, `/tpaccept`
- **Kits**: Kit system with GUI and cooldowns
- **Chat**: Chat formatting with group prefixes
- **Utilities**: `/heal`, `/god`, `/back`, `/rtp`, `/top`, `/repair`, `/trash`
- **Messages**: Customizable join/leave messages, MOTD
- **Protection**: Spawn protection, build protection

## Installation

1. Download the latest release from [Releases](https://github.com/SeriumTW/SRM-Essentials/releases)
2. Place the JAR file in your Hytale server's `mods` folder
3. Start the server
4. Configure in `config.toml`

## Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Running Development Server

```bash
./gradlew runServer
```

## Configuration

Configuration files are created in the plugin's data folder:
- `config.toml` - Main configuration
- `messages.toml` - Customizable messages
- `kits.toml` - Kit definitions

## Permissions

All permissions use the `essentials.` prefix:
- `essentials.home` - Use /home
- `essentials.sethome` - Use /sethome
- `essentials.homes.<tier>` - Home limit tiers
- `essentials.warp` - Use /warp
- `essentials.tpa` - Use /tpa
- `essentials.kit` - Use /kit
- `essentials.kit.<kitname>` - Access specific kit
- And more...

## Credits

Based on [Essentials by nhulston](https://github.com/nhulston/Essentials).

## License

Apache 2.0
