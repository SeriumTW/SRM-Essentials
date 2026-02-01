package io.github.seriumtw.essentials.managers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.seriumtw.essentials.integration.SRMPermsIntegration;
import io.github.seriumtw.essentials.util.ColorUtil;
import io.github.seriumtw.essentials.util.ConfigManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Manages chat formatting with SRM-Perms integration.
 * 
 * When SRM-Perms is available:
 * - Uses prefix/suffix from SRM-Perms group metadata
 * - Format: %prefix%%player%%suffix%: %message%
 * 
 * When SRM-Perms is NOT available:
 * - Falls back to group-based formats from config.toml
 * - Uses [chat.fallback-formats] section
 */
public class ChatManager {
    private static final String COLOR_PERMISSION = "essentials.chat.color";
    // Matches color codes (&0-&f, &#RRGGBB) and formatting codes (&l, &r)
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("&[0-9a-fA-FlLrR]|&#[0-9a-fA-F]{6}");

    private final ConfigManager configManager;

    public ChatManager(@Nonnull ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Creates a custom formatter for the PlayerChatEvent based on player's groups.
     */
    @Nonnull
    public PlayerChatEvent.Formatter createFormatter() {
        return this::formatMessage;
    }

    /**
     * Formats a chat message for a player.
     * Uses SRM-Perms prefix/suffix if available, otherwise falls back to config-based formats.
     */
    @Nonnull
    public Message formatMessage(@Nonnull PlayerRef sender, @Nonnull String content) {
        UUID uuid = sender.getUuid();
        String format;
        String prefix = "";
        String suffix = "";
        String group = "";
        
        // Check if SRM-Perms is available for prefix/suffix
        if (SRMPermsIntegration.isAvailable()) {
            // Use SRM-Perms data
            prefix = SRMPermsIntegration.getPrefix(uuid);
            suffix = SRMPermsIntegration.getSuffix(uuid);
            group = SRMPermsIntegration.getPrimaryGroup(uuid);
            format = configManager.getChatFormat();
        } else {
            // Fallback to config-based formats
            group = getPrimaryGroupFromHytale(uuid);
            format = getFormatForGroup(group);
        }

        // Strip color codes from message unless player has permission
        String sanitizedContent = content;
        if (!PermissionsModule.get().hasPermission(uuid, COLOR_PERMISSION)) {
            sanitizedContent = stripColorCodes(content);
        }

        // Apply all placeholders
        String formatted = format
                .replace("%prefix%", prefix)
                .replace("%suffix%", suffix)
                .replace("%group%", group)
                .replace("%player%", sender.getUsername())
                .replace("%message%", sanitizedContent);

        return ColorUtil.colorize(formatted);
    }

    /**
     * Strips color codes (&0-&f, &#RRGGBB) and formatting codes (&l, &r) from a string.
     */
    @Nonnull
    private String stripColorCodes(@Nonnull String text) {
        return FORMAT_CODE_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Gets the primary group for a player from Hytale's PermissionsModule.
     * Returns the first group found, or empty string if none.
     */
    @Nonnull
    private String getPrimaryGroupFromHytale(@Nonnull UUID playerUuid) {
        Set<String> groups = PermissionsModule.get().getGroupsForUser(playerUuid);
        if (groups.isEmpty()) {
            return "";
        }
        // Return first group (typically the primary/highest priority)
        return groups.iterator().next();
    }

    /**
     * Gets the appropriate chat format for a player based on their permission groups.
     * Used as fallback when SRM-Perms is not available.
     */
    @Nonnull
    private String getFormatForGroup(@Nonnull String primaryGroup) {
        List<ConfigManager.ChatFormat> formats = configManager.getChatFormats();

        if (formats.isEmpty()) {
            return configManager.getChatFallbackFormat();
        }

        // Check each configured format in order (List preserves insertion order)
        for (ConfigManager.ChatFormat chatFormat : formats) {
            if (chatFormat.group().equalsIgnoreCase(primaryGroup)) {
                return chatFormat.format();
            }
        }

        // Also check all player groups (not just primary)
        Set<String> playerGroups = PermissionsModule.get().getGroupsForUser(
            // We need the UUID here, but we only have the group name
            // This is a limitation - for full group matching, use SRM-Perms
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000000") // placeholder
        );

        return configManager.getChatFallbackFormat();
    }

    /**
     * Gets the appropriate chat format for a player based on their permission groups.
     * Returns the first matching group format, or the fallback if no groups match.
     * @deprecated Use {@link #formatMessage} which handles SRM-Perms integration
     */
    @Deprecated
    @Nonnull
    private String getFormatForPlayer(@Nonnull UUID playerUuid) {
        List<ConfigManager.ChatFormat> formats = configManager.getChatFormats();

        if (formats.isEmpty()) {
            return configManager.getChatFallbackFormat();
        }

        Set<String> playerGroups = PermissionsModule.get().getGroupsForUser(playerUuid);

        // Check each configured format in order (List preserves insertion order)
        for (ConfigManager.ChatFormat chatFormat : formats) {
            // Check if player is in this group (case-insensitive)
            for (String playerGroup : playerGroups) {
                if (playerGroup.equalsIgnoreCase(chatFormat.group())) {
                    return chatFormat.format();
                }
            }
        }

        return configManager.getChatFallbackFormat();
    }

    /**
     * Checks if chat formatting is enabled.
     */
    public boolean isEnabled() {
        return configManager.isChatEnabled();
    }
}
