package cc.davyy.cduels.listeners;

import cc.davyy.cduels.managers.DatabaseManager;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final DatabaseManager databaseManager;

    @Inject
    public PlayerJoinListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        databaseManager.addPlayer(player.getUniqueId());
    }

}