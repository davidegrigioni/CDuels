package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.model.PlayerStats;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.pigaut.lib.sql.DataTable;
import io.github.pigaut.lib.sql.Database;
import io.github.pigaut.lib.sql.SQLib;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    public void addPlayer(@NotNull UUID uuid) {
        if (!playerExists(uuid)) {
            table.insertInto("uuid")
                    .withParameter(uuid.toString())
                    .executeUpdate();
        }
    }

    protected void updateStats(@NotNull UUID uuid, int duelWon, int duelLost) {
        table.insertInto("uuid", "duels_won", "duels_lost")
                .withParameter(uuid.toString())
                .withParameter(duelWon)
                .withParameter(duelLost)
                .executeUpdate();
    }

    public List<PlayerStats> getLeaderboard(int limit) {
        List<PlayerStats> statsList = new ArrayList<>();
        table.select("ORDER BY duels_won DESC LIMIT ?")
                .withParameter(limit)
                .executeQuery(resultSet -> {
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        int duelsWon = resultSet.getInt("duels_won");
                        int duelsLost = resultSet.getInt("duels_lost");
                        statsList.add(new PlayerStats(uuid, duelsWon, duelsLost));
                    }
                });
        return statsList;
    }

    public PlayerStats getPlayerStats(@NotNull UUID uuid) {
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