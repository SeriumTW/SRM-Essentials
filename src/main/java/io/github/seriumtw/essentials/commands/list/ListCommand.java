package io.github.seriumtw.essentials.commands.list;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.seriumtw.essentials.SRMEssentials;
import io.github.seriumtw.essentials.util.MessageManager;
import io.github.seriumtw.essentials.util.Msg;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command to list all online players.
 * Usage: /list
 */
public class ListCommand extends AbstractCommand {
    private final MessageManager messages;

    public ListCommand() {
        super("list", "List all online players");
        this.messages = SRMEssentials.getInstance().getMessageManager();
        requirePermission("essentials.list");
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        List<PlayerRef> players = Universe.get().getPlayers();
        
        String playerNames = players.stream()
                .map(PlayerRef::getUsername)
                .collect(Collectors.joining(", "));
        
        if (playerNames.isEmpty()) {
            playerNames = "None";
        }
        
        Msg.send(context, messages.get("commands.list.prefix", Map.of("count", String.valueOf(players.size()))) + ": " + playerNames);
        return CompletableFuture.completedFuture(null);
    }
}
