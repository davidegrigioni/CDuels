package cc.davyy.cduels.managers;

import cc.davyy.cduels.utils.Messages;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cc.davyy.cduels.utils.ConfigUtils.getMessage;
import static cc.davyy.cduels.utils.TxtUtils.of;

@Singleton
public class DuelManager {

    private final Map<UUID, UUID> duelRequests = new HashMap<>();
    private final Map<UUID, Location> playerOriginalLocations = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerOriginalInventories = new HashMap<>();

    private final WorldCreatorManager worldCreatorManager;

    @Inject
    public DuelManager(WorldCreatorManager worldCreatorManager) {
        this.worldCreatorManager = worldCreatorManager;
    }

    /**
     * Starts a duel between two players by creating a duel world and teleporting them.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    public void startDuel(@NotNull Player player1, @NotNull Player player2) {
        String worldName = "duel_" + UUID.randomUUID().toString().substring(0, 8);

        World duelWorld = worldCreatorManager.createDuelWorld(worldName);

        if (duelWorld == null) {
            String dwFailed = getMessage(Messages.DUEL_WORLD_CREATION_FAILED);
            player1.sendMessage(of(dwFailed)
                    .build());
            player2.sendMessage(of(dwFailed)
                    .build());
            return;
        }

        playerOriginalLocations.put(player1.getUniqueId(), player1.getLocation());
        playerOriginalLocations.put(player2.getUniqueId(), player2.getLocation());

        playerOriginalInventories.put(player1.getUniqueId(), player1.getInventory().getContents());
        playerOriginalInventories.put(player2.getUniqueId(), player2.getInventory().getContents());

        player1.getInventory().clear();
        player2.getInventory().clear();

        Location player1Spawn = new Location(duelWorld, 100, 1, 100);
        Location player2Spawn = new Location(duelWorld, -100, 1, -100);

        player1.teleportAsync(player1Spawn);
        player2.teleportAsync(player2Spawn);

        String duelStarted = getMessage(Messages.DUEL_STARTED);
        player1.sendMessage(of(duelStarted)
                .build());
        player2.sendMessage(of(duelStarted)
                .build());
    }

    /**
     * Ends a duel by teleporting players back to their original locations, restoring inventories, and deleting the duel world.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    public void endDuel(@NotNull Player player1, @NotNull Player player2) {
        Location player1OriginalLocation = playerOriginalLocations.remove(player1.getUniqueId());
        Location player2OriginalLocation = playerOriginalLocations.remove(player2.getUniqueId());

        if (player1OriginalLocation != null && player2OriginalLocation != null) {
            String duelEnd = getMessage(Messages.DUEL_ENDED);

            player1.teleportAsync(player1OriginalLocation);
            player1.sendMessage(of(duelEnd)
                    .build());

            player2.teleportAsync(player2OriginalLocation);
            player2.sendMessage(of(duelEnd)
                    .build());
        }

        ItemStack[] player1OriginalInventory = playerOriginalInventories.remove(player1.getUniqueId());
        ItemStack[] player2OriginalInventory = playerOriginalInventories.remove(player2.getUniqueId());

        if (player1OriginalInventory != null && player2OriginalInventory != null) {
            player1.getInventory().setContents(player1OriginalInventory);

            player2.getInventory().setContents(player2OriginalInventory);
        }
    }

    /**
     * Sends a duel request from one player to another.
     *
     * @param challenger The player who is challenging.
     * @param challenged The player being challenged.
     */
    public void sendDuelRequest(@NotNull UUID challenger, @NotNull UUID challenged) {
        duelRequests.put(challenged, challenger);
    }

    /**
     * Checks if there is a duel request for a given player.
     *
     * @param challenged The player being checked.
     * @return {@code true} if a request exists, {@code false} otherwise.
     */
    public boolean hasDuelRequest(@NotNull UUID challenged) {
        return duelRequests.containsKey(challenged);
    }

    /**
     * Gets the UUID of the player who challenged the given player.
     *
     * @param challenged The player being checked.
     * @return The UUID of the challenger, or {@code null} if no request exists.
     */
    public UUID getChallenger(@NotNull UUID challenged) {
        return duelRequests.get(challenged);
    }

    /**
     * Removes a duel request for a given player.
     *
     * @param challenged The player whose request should be removed.
     */
    public void removeDuelRequest(UUID challenged) {
        duelRequests.remove(challenged);
    }

}