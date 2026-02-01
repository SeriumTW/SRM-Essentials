package io.github.seriumtw.essentials;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
import io.github.seriumtw.essentials.commands.back.BackCommand;
import io.github.seriumtw.essentials.commands.essentials.EssentialsCommand;
import io.github.seriumtw.essentials.commands.freecam.FreecamCommand;
import io.github.seriumtw.essentials.commands.god.GodCommand;
import io.github.seriumtw.essentials.commands.heal.HealCommand;
import io.github.seriumtw.essentials.commands.home.DelHomeCommand;
import io.github.seriumtw.essentials.commands.home.HomeCommand;
import io.github.seriumtw.essentials.commands.home.SetHomeCommand;
import io.github.seriumtw.essentials.commands.kit.KitCommand;
import io.github.seriumtw.essentials.commands.list.ListCommand;
import io.github.seriumtw.essentials.commands.msg.MsgCommand;
import io.github.seriumtw.essentials.commands.msg.ReplyCommand;
import io.github.seriumtw.essentials.commands.repair.RepairCommand;
import io.github.seriumtw.essentials.commands.rtp.RtpCommand;
import io.github.seriumtw.essentials.commands.rules.RulesCommand;
import io.github.seriumtw.essentials.commands.shout.ShoutCommand;
import io.github.seriumtw.essentials.commands.top.TopCommand;
import io.github.seriumtw.essentials.commands.tphere.TphereCommand;
import io.github.seriumtw.essentials.commands.trash.TrashCommand;
import io.github.seriumtw.essentials.commands.spawn.SetSpawnCommand;
import io.github.seriumtw.essentials.commands.spawn.SpawnCommand;
import io.github.seriumtw.essentials.commands.tpa.TpaCommand;
import io.github.seriumtw.essentials.commands.tpa.TpacceptCommand;
import io.github.seriumtw.essentials.commands.warp.DelWarpCommand;
import io.github.seriumtw.essentials.commands.warp.SetWarpCommand;
import io.github.seriumtw.essentials.commands.warp.WarpCommand;
import io.github.seriumtw.essentials.events.BuildProtectionEvent;
import io.github.seriumtw.essentials.events.ChatEvent;
import io.github.seriumtw.essentials.events.DeathLocationEvent;
import io.github.seriumtw.essentials.events.JoinLeaveEvent;
import io.github.seriumtw.essentials.events.MotdEvent;
import io.github.seriumtw.essentials.events.PlayerQuitEvent;
import io.github.seriumtw.essentials.events.SpawnProtectionEvent;
import io.github.seriumtw.essentials.events.SpawnRegionTitleEvent;
import io.github.seriumtw.essentials.events.SpawnTeleportEvent;
import io.github.seriumtw.essentials.events.TeleportMovementEvent;
import io.github.seriumtw.essentials.events.SleepPercentageEvent;
import io.github.seriumtw.essentials.events.StarterKitEvent;
import io.github.seriumtw.essentials.events.UpdateNotifyEvent;
import io.github.seriumtw.essentials.managers.BackManager;
import io.github.seriumtw.essentials.managers.ChatManager;
import io.github.seriumtw.essentials.managers.HomeManager;
import io.github.seriumtw.essentials.managers.KitManager;
import io.github.seriumtw.essentials.managers.SpawnManager;
import io.github.seriumtw.essentials.managers.SpawnProtectionManager;
import io.github.seriumtw.essentials.managers.TeleportManager;
import io.github.seriumtw.essentials.managers.TpaManager;
import io.github.seriumtw.essentials.managers.WarpManager;
import io.github.seriumtw.essentials.util.ConfigManager;
import io.github.seriumtw.essentials.util.Log;
import io.github.seriumtw.essentials.util.MessageManager;
import io.github.seriumtw.essentials.util.StorageManager;
import io.github.seriumtw.essentials.util.VersionChecker;

import javax.annotation.Nonnull;

public class SRMEssentials extends JavaPlugin {
    public static final String VERSION = "1.0.0";
    
    private static SRMEssentials instance;
    
    private ConfigManager configManager;
    private StorageManager storageManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private ChatManager chatManager;
    private SpawnProtectionManager spawnProtectionManager;
    private TpaManager tpaManager;
    private TeleportManager teleportManager;
    private KitManager kitManager;
    private BackManager backManager;
    private VersionChecker versionChecker;
    private MessageManager messageManager;

    public SRMEssentials(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        Log.init(getLogger());
        Log.info("SRM-Essentials is starting...");

        configManager = new ConfigManager(getDataDirectory());
        messageManager = new MessageManager(getDataDirectory());
        storageManager = new StorageManager(getDataDirectory());

        homeManager = new HomeManager(storageManager, configManager);
        warpManager = new WarpManager(storageManager);
        spawnManager = new SpawnManager(storageManager);
        chatManager = new ChatManager(configManager);
        spawnProtectionManager = new SpawnProtectionManager(configManager, storageManager);
        tpaManager = new TpaManager(configManager);
        teleportManager = new TeleportManager(configManager);
        kitManager = new KitManager(getDataDirectory(), storageManager);
        backManager = new BackManager();
        versionChecker = new VersionChecker(VERSION);
    }

    @Override
    protected void start() {
        registerCommands();
        registerEvents();
        
        // Check for updates asynchronously
        versionChecker.checkForUpdatesAsync();
        
        Log.info("SRM-Essentials v" + VERSION + " started successfully!");
    }

    @Override
    protected void shutdown() {
        Log.info("SRM-Essentials is shutting down...");

        if (storageManager != null) {
            storageManager.shutdown();
        }

        if (tpaManager != null) {
            tpaManager.shutdown();
        }

        if (teleportManager != null) {
            teleportManager.shutdown();
        }

        Log.info("SRM-Essentials shut down.");
    }

    private void registerCommands() {
        // Home commands
        getCommandRegistry().registerCommand(new SetHomeCommand(homeManager));
        getCommandRegistry().registerCommand(new HomeCommand(homeManager, teleportManager, backManager));
        getCommandRegistry().registerCommand(new DelHomeCommand(homeManager));

        // Warp commands
        getCommandRegistry().registerCommand(new SetWarpCommand(warpManager));
        getCommandRegistry().registerCommand(new WarpCommand(warpManager, teleportManager, backManager));
        getCommandRegistry().registerCommand(new DelWarpCommand(warpManager));

        // Spawn commands
        getCommandRegistry().registerCommand(new SetSpawnCommand(spawnManager));
        getCommandRegistry().registerCommand(new SpawnCommand(spawnManager, teleportManager, backManager));

        // TPA commands
        getCommandRegistry().registerCommand(new TpaCommand(tpaManager));
        getCommandRegistry().registerCommand(new TpacceptCommand(tpaManager, teleportManager, backManager));

        // Kit command
        getCommandRegistry().registerCommand(new KitCommand(kitManager, configManager));

        // Back command
        getCommandRegistry().registerCommand(new BackCommand(backManager, teleportManager));

        // RTP command
        getCommandRegistry().registerCommand(new RtpCommand(configManager, storageManager, teleportManager, backManager));

        // List command
        getCommandRegistry().registerCommand(new ListCommand());

        // Heal command
        getCommandRegistry().registerCommand(new HealCommand());

        // Freecam command
        getCommandRegistry().registerCommand(new FreecamCommand());

        // God command
        getCommandRegistry().registerCommand(new GodCommand());

        // Msg command (with aliases: m, message, whisper, pm)
        getCommandRegistry().registerCommand(new MsgCommand());

        // Reply command (with alias: reply)
        getCommandRegistry().registerCommand(new ReplyCommand());

        // Tphere command
        getCommandRegistry().registerCommand(new TphereCommand());

        // Top command
        getCommandRegistry().registerCommand(new TopCommand());

        // Essentials info command
        getCommandRegistry().registerCommand(new EssentialsCommand());

        // Shout/broadcast command
        getCommandRegistry().registerCommand(new ShoutCommand(configManager));

        // Repair command
        getCommandRegistry().registerCommand(new RepairCommand(configManager, storageManager));
        
        // Rules command
        getCommandRegistry().registerCommand(new RulesCommand(configManager));
        
        // Trash command
        getCommandRegistry().registerCommand(new TrashCommand());
    }

    private void registerEvents() {
        new ChatEvent(chatManager).register(getEventRegistry());
        new BuildProtectionEvent(configManager).register(getEntityStoreRegistry());
        new SpawnProtectionEvent(spawnProtectionManager).register(getEntityStoreRegistry());
        new SpawnRegionTitleEvent(spawnProtectionManager, configManager).register(getEntityStoreRegistry());
        new TeleportMovementEvent(teleportManager).register(getEntityStoreRegistry());

        SpawnTeleportEvent spawnTeleportEvent = new SpawnTeleportEvent(spawnManager, configManager, storageManager);
        spawnTeleportEvent.registerEvents(getEventRegistry());
        spawnTeleportEvent.registerSystems(getEntityStoreRegistry());

        // Death location tracking for /back
        new DeathLocationEvent(backManager).register(getEntityStoreRegistry());

        // MOTD on join
        new MotdEvent(configManager).register(getEventRegistry());

        // Join/leave broadcast messages
        new JoinLeaveEvent(configManager, storageManager).register(getEventRegistry());

        // Update notification for admins
        new UpdateNotifyEvent(versionChecker, configManager).register(getEventRegistry());

        // Starter kit for new players
        new StarterKitEvent(kitManager, configManager, storageManager).register(getEventRegistry());

        // Sleep percentage system
        new SleepPercentageEvent(configManager, messageManager).register(getEntityStoreRegistry());

        // Player disconnect cleanup
        new PlayerQuitEvent(storageManager, tpaManager, teleportManager, backManager).register(getEventRegistry());

        // Sync spawn provider with world config after all worlds are loaded
        // This updates the spawn marker on the map
        getEventRegistry().registerGlobal(AllWorldsLoadedEvent.class, event -> {
            spawnManager.syncWorldSpawnProvider();
        });
    }

    /**
     * Gets the plugin instance.
     */
    @Nonnull
    public static SRMEssentials getInstance() {
        return instance;
    }

    /**
     * Reloads all configuration files.
     */
    public void reloadConfigs() {
        configManager.reload();
        messageManager.reload();
        kitManager.reload();
        Log.info("All configurations reloaded.");
    }

    /**
     * Gets the message manager.
     */
    @Nonnull
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Gets the storage manager.
     */
    @Nonnull
    public StorageManager getStorageManager() {
        return storageManager;
    }
}
