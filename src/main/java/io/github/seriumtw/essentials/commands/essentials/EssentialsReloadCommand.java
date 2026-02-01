package io.github.seriumtw.essentials.commands.essentials;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import io.github.seriumtw.essentials.Essentials;
import io.github.seriumtw.essentials.util.MessageManager;
import io.github.seriumtw.essentials.util.Msg;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * Subcommand to reload EssentialsCore configuration.
 * Usage: /essentials reload
 * Requires: essentials.reload permission
 * Can be executed by console or players.
 */
public class EssentialsReloadCommand extends AbstractCommand {

    public EssentialsReloadCommand() {
        super("reload", "Reload EssentialsCore configuration");
        requirePermission("essentials.reload");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        SRMEssentials.getInstance().reloadConfigs();
        
        // Get MessageManager after reload to ensure we have the fresh instance
        MessageManager messages = SRMEssentials.getInstance().getMessageManager();
        Msg.send(context, messages.get("commands.essentials.reload.success"));
        return CompletableFuture.completedFuture(null);
    }
}
