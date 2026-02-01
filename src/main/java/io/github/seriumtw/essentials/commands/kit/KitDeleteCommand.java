package io.github.seriumtw.essentials.commands.kit;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.seriumtw.essentials.Essentials;
import io.github.seriumtw.essentials.managers.KitManager;
import io.github.seriumtw.essentials.util.MessageManager;
import io.github.seriumtw.essentials.util.Msg;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Subcommand to delete a kit.
 * Usage: /kit delete <name>
 */
public class KitDeleteCommand extends AbstractPlayerCommand {
    private final KitManager kitManager;
    private final MessageManager messages;
    private final RequiredArg<String> nameArg;

    public KitDeleteCommand(@Nonnull KitManager kitManager) {
        super("delete", "Delete a kit");
        this.kitManager = kitManager;
        this.messages = SRMEssentials.getInstance().getMessageManager();

        requirePermission("essentials.kit.delete");
        this.nameArg = withRequiredArg("name", "Kit name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String kitName = context.get(nameArg);

        // Check if kit exists
        if (kitManager.getKit(kitName) == null) {
            Msg.send(context, messages.get("commands.kit.delete.not-found", Map.of("name", kitName)));
            return;
        }

        // Delete the kit
        kitManager.deleteKit(kitName);

        Msg.send(context, messages.get("commands.kit.delete.success", Map.of("name", kitName)));
    }
}
