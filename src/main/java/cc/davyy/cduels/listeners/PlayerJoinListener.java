package cc.davyy.cduels.listeners;

import cc.davyy.cduels.managers.KitManager;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final KitManager kitManager;

    @Inject
    public PlayerJoinListener(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

}