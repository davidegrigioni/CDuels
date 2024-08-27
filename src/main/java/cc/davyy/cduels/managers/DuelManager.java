package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.model.PlayerStats;
import cc.davyy.cduels.utils.Messages;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DuelManager {

    private final CDuels instance;

    private final Map<UUID, UUID> duelRequests = new HashMap<>();
    private final Map<UUID, Location> playerOriginalLocations = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerOriginalInventories = new HashMap<>();

    private final WorldCreatorManager worldCreatorManager;
    private final DatabaseManager databaseManager;
    private final RewardManager rewardManager;

    @Inject
    public DuelManager(CDuels instance, WorldCreatorManager worldCreatorManager, DatabaseManager databaseManager, RewardManager rewardManager) {
        this.instance = instance;
        this.worldCreatorManager = worldCreatorManager;
        this.databaseManager = databaseManager;
        this.rewardManager = rewardManager;
    }

    /**
     * Starts a duel between two players by creating a duel world and teleporting them.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    public void startDuel(@NotNull Player player1, @NotNull Player player2) {
        World duelWorld = createDuelWorld();

        if (duelWorld == null) {
            player1.sendMessage(Messages.DUEL_WORLD_CREATION_FAILED.getMessage());
            player2.sendMessage(Messages.DUEL_WORLD_CREATION_FAILED.getMessage());
            return;
        }

        savePlayerState(player1);
        savePlayerState(player2);

        player1.getInventory().clear();
        player2.getInventory().clear();

        teleportToDuelWorld(player1, duelWorld, 100, 100);
        teleportToDuelWorld(player2, duelWorld, -100, -100);

        player1.sendMessage(Messages.DUEL_STARTED.getMessage());
        player2.sendMessage(Messages.DUEL_STARTED.getMessage());
    }

    /**
     * Ends a duel by teleporting players back to their original locations, restoring inventories, and deleting the duel world.
     *
     * @param winner The first player.
     * @param loser The second player.
     */
    public void endDuel(@NotNull Player winner, @NotNull Player loser) {
        World duelWorld = winner.getWorld();

        applyDuelStatsIfDuelWorld(duelWorld, winner.getUniqueId(), loser.getUniqueId());

        restorePlayerState(winner);
        restorePlayerState(loser);

        winner.sendMessage(Messages.DUEL_WON.getMessage());
        rewardManager.applyRewardToPlayer(winner);

        loser.sendMessage(Messages.DUEL_LOST.getMessage());
    }

    /**
     * Sends a duel request from one player to another.
     *
     * @param challenger The player who is challenging.
     * @param challenged The player being challenged.
     */
    public void sendDuelRequest(@NotNull UUID challenger, @NotNull UUID challenged) { duelRequests.put(challenged, challenger); }

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
    public void removeDuelRequest(@NotNull UUID challenged) {
        duelRequests.remove(challenged);
    }

    public CompletableFuture<List<PlayerStats>> getLeaderBoard(int limit) { return databaseManager.getLeaderboard(limit); }

    private void restorePlayerState(@NotNull Player player) {
        player.teleportAsync(playerOriginalLocations.get(player.getUniqueId()));
        player.getInventory().setContents(playerOriginalInventories.get(player.getUniqueId()));
    }

    private void applyDuelStatsIfDuelWorld(@NotNull World world, @NotNull UUID winnerUUID, @NotNull UUID loserUUID) {
        if (isDuelWorld(world)) {
            CompletableFuture<PlayerStats> winnerStatsFuture = databaseManager.getPlayerStats(winnerUUID);
            CompletableFuture<PlayerStats> loserStatsFuture = databaseManager.getPlayerStats(loserUUID);

            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> winnerStatsFuture.thenCombine(loserStatsFuture, (winnerStats, loserStats) -> {
                databaseManager.updateStats(winnerUUID, winnerStats.duelWon() + 1, winnerStats.duelLost());
                databaseManager.updateStats(loserUUID, loserStats.duelWon(), loserStats.duelLost() + 1);
                return null;
            }));

            worldCreatorManager.deleteWorld(world.getName());
        }
    }

    private World createDuelWorld() {
        String worldName = "duel_" + UUID.randomUUID().toString().substring(0, 8);
        return worldCreatorManager.createDuelWorld(worldName);
    }

    private void savePlayerState(@NotNull Player player) {
        playerOriginalLocations.put(player.getUniqueId(), player.getLocation());
        playerOriginalInventories.put(player.getUniqueId(), player.getInventory().getContents());
    }

    private void teleportToDuelWorld(@NotNull Player player, @NotNull World duelWorld, double x, double z) {
        Location spawnLocation = new Location(duelWorld, x, 1, z);
        player.teleportAsync(spawnLocation);
    }

    private boolean isDuelWorld(@NotNull World world) { return world.getName().startsWith("duel_"); }

}