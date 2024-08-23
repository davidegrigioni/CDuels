package cc.davyy.cduels.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DuelManager {

    private final Map<UUID, UUID> duelRequests = new HashMap<>();

    private final WorldCreatorManager worldCreatorManager;

    @Inject
    public DuelManager(WorldCreatorManager worldCreatorManager) {
        this.worldCreatorManager = worldCreatorManager;
    }

    public void startDuel(@NotNull Player player1, @NotNull Player player2) {
        String worldName = "duel_" + UUID.randomUUID().toString().substring(0, 8);
        World duelWorld = worldCreatorManager.createDuelWorld(worldName);

        if (duelWorld != null) {
            Location player1Spawn = new Location(duelWorld, 100, 1, 100);
            Location player2Spawn = new Location(duelWorld, -100, 1, -100);

            player1.teleport(player1Spawn);
            player2.teleport(player2Spawn);

            player1.sendMessage("Duel started! You have been teleported to the duel world.");
            player2.sendMessage("Duel started! You have been teleported to the duel world.");
        } else {
            player1.sendMessage("Failed to create the duel world.");
            player2.sendMessage("Failed to create the duel world.");
        }
    }

    public void sendDuelRequest(@NotNull UUID challenger, @NotNull UUID challenged) {
        duelRequests.put(challenged, challenger);
    }

    public boolean hasDuelRequest(@NotNull UUID challenged) { return duelRequests.containsKey(challenged); }

    public UUID getChallenger(@NotNull UUID challenged) { return duelRequests.get(challenged); }

    public void removeDuelRequest(UUID challenged) { duelRequests.remove(challenged); }

}