package cc.davyy.cduels.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class DuelManager {

    private final WorldCreatorManager worldCreatorManager;

    @Inject
    public DuelManager(WorldCreatorManager worldCreatorManager) {
        this.worldCreatorManager = worldCreatorManager;
    }

    public void startDuel(@NotNull Player player1, @NotNull Player player2) {
        String worldName = "duel_" + UUID.randomUUID().toString().substring(0, 8);
        World duelWorld = worldCreatorManager.createDuelWorld(worldName);

        if (duelWorld != null) {
            Location player1Spawn = new Location(duelWorld, 100, 70, 100);
            Location player2Spawn = new Location(duelWorld, -100, 70, -100);

            player1.teleportAsync(player1Spawn);
            player2.teleportAsync(player2Spawn);

            player1.sendMessage("Duel started! You have been teleported to the duel world.");
            player2.sendMessage("Duel started! You have been teleported to the duel world.");
        } else {
            player1.sendMessage("Failed to create the duel world.");
            player2.sendMessage("Failed to create the duel world.");
        }
    }

    public void endDuel(@NotNull String worldName, @NotNull Player player1, @NotNull Player player2) {
        World mainWorld = Bukkit.getWorld("world");

        if (mainWorld != null) {
            player1.teleportAsync(mainWorld.getSpawnLocation());
            player2.teleportAsync(mainWorld.getSpawnLocation());
        }

        worldCreatorManager.deleteWorld(worldName);

        player1.sendMessage("The duel has ended. You have been teleported back.");
        player2.sendMessage("The duel has ended. You have been teleported back.");
    }

}