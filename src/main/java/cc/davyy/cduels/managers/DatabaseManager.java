package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.model.PlayerStats;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.pigaut.lib.sql.DataTable;
import io.github.pigaut.lib.sql.Database;
import io.github.pigaut.lib.sql.SQLib;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class DatabaseManager {

    private final DataTable table;

    @Inject
    public DatabaseManager(CDuels instance) {
        Database database = SQLib.createDatabase(instance.getDataFolder(), "database");
        this.table = database.tableOf("player_stats");

        instance.getServer().getScheduler().runTaskAsynchronously(instance, this::createTable);
    }

    public CompletableFuture<Void> addPlayer(@NotNull UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            if (!playerExists(uuid)) {
                table.insertInto("uuid")
                        .withParameter(uuid.toString())
                        .executeUpdate();
            }
        });
    }

    public CompletableFuture<Void> updateStats(@NotNull UUID uuid, int duelWon, int duelLost) {
        return CompletableFuture.runAsync(() -> table.insertInto("uuid", "duels_won", "duels_lost")
                .withParameter(uuid.toString())
                .withParameter(duelWon)
                .withParameter(duelLost)
                .executeUpdate());
    }

    public CompletableFuture<List<PlayerStats>> getLeaderboard(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerStats> statsList = new ArrayList<>();
            table.select("ORDER BY duels_won DESC LIMIT ?")
                    .withParameter(limit)
                    .executeQuery(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                                int duelsWon = resultSet.getInt("duels_won");
                                int duelsLost = resultSet.getInt("duels_lost");
                                statsList.add(new PlayerStats(uuid, duelsWon, duelsLost));
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to retrieve leaderboard", e);
                        }
                    });
            return statsList;
        });
    }

    public CompletableFuture<PlayerStats> getPlayerStats(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicReference<PlayerStats> playerStats = new AtomicReference<>();
            table.select("WHERE uuid = ?")
                    .withParameter(uuid.toString())
                    .executeQuery(resultSet -> {
                        if (resultSet.next()) {
                            int duelsWon = resultSet.getInt("duels_won");
                            int duelsLost = resultSet.getInt("duels_lost");

                            PlayerStats stats = new PlayerStats(uuid, duelsWon, duelsLost);

                            playerStats.set(stats);
                        }
                    });
            return playerStats.get();
        });
    }

    private void createTable() {
        table.createIfNotExists("""
                uuid VARCHAR(36) PRIMARY KEY,
                duels_won INT DEFAULT 0,
                duels_lost INT DEFAULT 0
            """);
    }

    private boolean playerExists(@NotNull UUID uuid) {
        AtomicBoolean exists = new AtomicBoolean(false);
        table.select("WHERE uuid = ?")
                .withParameter(uuid.toString())
                .executeQuery(resultSet -> exists.set(resultSet.next()));
        return exists.get();
    }

}