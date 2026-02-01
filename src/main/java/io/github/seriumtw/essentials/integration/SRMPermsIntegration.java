package io.github.seriumtw.essentials.integration;

import io.github.seriumtw.essentials.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Integration bridge for SRM-Perms API.
 * Uses reflection to avoid hard dependency - SRM-Essentials works with or without SRM-Perms.
 * 
 * When SRM-Perms is available, this class provides access to:
 * - Player prefix (from group metadata)
 * - Player suffix (from group metadata)
 * - Player's primary group name
 * - Custom meta values
 */
public final class SRMPermsIntegration {
    
    private static boolean available = false;
    private static boolean initialized = false;
    
    // Cached API instance and methods for performance
    private static Object api = null;
    private static Method getUserManagerMethod = null;
    private static Method getUserMethod = null;
    
    private SRMPermsIntegration() {
        // Utility class
    }
    
    /**
     * Initializes the SRM-Perms integration.
     * Should be called during plugin startup (in start() method, not setup()).
     * Safe to call multiple times - will only initialize once.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        
        try {
            // Try to load the SRM-Perms provider class
            Class<?> providerClass = Class.forName("io.github.seriumtw.perms.api.SRMPermsProvider");
            
            // Get the API instance
            Method getMethod = providerClass.getMethod("get");
            api = getMethod.invoke(null);
            
            if (api == null) {
                available = false;
                Log.info("SRM-Perms found but API not ready. Using fallback chat formatting.");
                return;
            }
            
            // Cache method references for better performance
            getUserManagerMethod = api.getClass().getMethod("getUserManager");
            Object userManager = getUserManagerMethod.invoke(api);
            getUserMethod = userManager.getClass().getMethod("getUser", UUID.class);
            
            available = true;
            Log.info("SRM-Perms integration enabled! Chat will use prefix/suffix from SRM-Perms.");
            
        } catch (ClassNotFoundException e) {
            // SRM-Perms is not installed - this is fine
            available = false;
            Log.info("SRM-Perms not found. Using fallback chat formatting from config.toml.");
            
        } catch (IllegalStateException e) {
            // API not loaded yet (shouldn't happen if called in start())
            available = false;
            Log.warning("SRM-Perms API not loaded: " + e.getMessage());
            
        } catch (Exception e) {
            // Unexpected error
            available = false;
            Log.warning("Failed to initialize SRM-Perms integration: " + e.getMessage());
        }
    }
    
    /**
     * Checks if SRM-Perms integration is available.
     * @return true if SRM-Perms is installed and the API is accessible
     */
    public static boolean isAvailable() {
        return available;
    }
    
    /**
     * Gets the prefix for a player from SRM-Perms.
     * @param uuid the player's UUID
     * @return the prefix, or empty string if not available
     */
    @Nonnull
    public static String getPrefix(@Nonnull UUID uuid) {
        if (!available) {
            return "";
        }
        
        try {
            Object metaData = getMetaData(uuid);
            if (metaData == null) {
                return "";
            }
            
            Method getPrefixMethod = metaData.getClass().getMethod("getPrefix");
            String prefix = (String) getPrefixMethod.invoke(metaData);
            return prefix != null ? prefix : "";
            
        } catch (Exception e) {
            Log.warning("Failed to get prefix from SRM-Perms: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Gets the suffix for a player from SRM-Perms.
     * @param uuid the player's UUID
     * @return the suffix, or empty string if not available
     */
    @Nonnull
    public static String getSuffix(@Nonnull UUID uuid) {
        if (!available) {
            return "";
        }
        
        try {
            Object metaData = getMetaData(uuid);
            if (metaData == null) {
                return "";
            }
            
            Method getSuffixMethod = metaData.getClass().getMethod("getSuffix");
            String suffix = (String) getSuffixMethod.invoke(metaData);
            return suffix != null ? suffix : "";
            
        } catch (Exception e) {
            Log.warning("Failed to get suffix from SRM-Perms: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Gets the primary group name for a player from SRM-Perms.
     * @param uuid the player's UUID
     * @return the primary group name, or empty string if not available
     */
    @Nonnull
    public static String getPrimaryGroup(@Nonnull UUID uuid) {
        if (!available) {
            return "";
        }
        
        try {
            Object metaData = getMetaData(uuid);
            if (metaData == null) {
                return "";
            }
            
            Method getPrimaryGroupMethod = metaData.getClass().getMethod("getPrimaryGroup");
            String group = (String) getPrimaryGroupMethod.invoke(metaData);
            return group != null ? group : "";
            
        } catch (Exception e) {
            Log.warning("Failed to get primary group from SRM-Perms: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Gets a custom meta value for a player from SRM-Perms.
     * @param uuid the player's UUID
     * @param key the meta key to look up
     * @return the meta value, or null if not found
     */
    @Nullable
    public static String getMetaValue(@Nonnull UUID uuid, @Nonnull String key) {
        if (!available) {
            return null;
        }
        
        try {
            Object metaData = getMetaData(uuid);
            if (metaData == null) {
                return null;
            }
            
            Method getMetaValueMethod = metaData.getClass().getMethod("getMetaValue", String.class);
            return (String) getMetaValueMethod.invoke(metaData, key);
            
        } catch (Exception e) {
            Log.warning("Failed to get meta value '" + key + "' from SRM-Perms: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Internal method to get the CachedMetaData for a user.
     */
    @Nullable
    private static Object getMetaData(@Nonnull UUID uuid) throws Exception {
        // Get UserManager
        Object userManager = getUserManagerMethod.invoke(api);
        if (userManager == null) {
            return null;
        }
        
        // Get User (may be null if player is offline/not loaded)
        Object user = getUserMethod.invoke(userManager, uuid);
        if (user == null) {
            return null;
        }
        
        // Get CachedData
        Method getCachedDataMethod = user.getClass().getMethod("getCachedData");
        Object cachedData = getCachedDataMethod.invoke(user);
        if (cachedData == null) {
            return null;
        }
        
        // Get MetaData
        Method getMetaDataMethod = cachedData.getClass().getMethod("getMetaData");
        return getMetaDataMethod.invoke(cachedData);
    }
}
