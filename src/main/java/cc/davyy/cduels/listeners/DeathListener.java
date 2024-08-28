package cc.davyy.cduels.listeners;

import cc.davyy.cduels.managers.DuelManager;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class DeathListener implements Listener {

    private final DuelManager duelManager;

    @Inject
    public DeathListener(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player winner = event.getPlayer();
        Player loser = event.getEntity();
        duelManager.endDuel(winner, loser);
    }

}