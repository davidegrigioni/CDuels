package cc.davyy.cduels.commands;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.managers.DuelManager;
import cc.davyy.cduels.model.PlayerStats;
import cc.davyy.cduels.utils.Messages;
import com.google.inject.Inject;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cc.davyy.cduels.utils.TxtUtils.of;

@Command(name = "duel")
public class DuelCommand {

    private final CDuels instance;
    private final DuelManager duelManager;

    @Inject
    public DuelCommand(CDuels instance, DuelManager duelManager) {
        this.instance = instance;
        this.duelManager = duelManager;
    }

    @Execute(name = "invite")
    void inviteDuel(@Context Player player, @Arg Player target) {
        if (!target.isOnline()) {
            player.sendMessage(Messages.PLAYER_NOT_ONLINE.getMessage());
            return;
        }

        if (player.equals(target)) {
            player.sendMessage(Messages.DUEL_YOURSELF.getMessage());
            return;
        }

        duelManager.sendDuelRequest(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(of(Messages.DUEL_INVITE_SEND.getStringMessage())
                .placeholder("target", target.getName())
                .build());
        target.sendMessage(of(Messages.DUEL_INVITE_RECEIVED.getStringMessage())
                .placeholder("player", player.getName())
                .build());
    }

    @Execute(name = "accept")
    void acceptDuel(@Context Player player) {
        if (!duelManager.hasDuelRequest(player.getUniqueId())) {
            player.sendMessage(Messages.DUEL_NO_PENDING_REQUEST.getMessage());
            return;
        }

        Player challenger = instance.getServer().getPlayer(duelManager.getChallenger(player.getUniqueId()));
        if (challenger == null || !challenger.isOnline()) {
            player.sendMessage(Messages.PLAYER_NO_LONGER_ONLINE.getMessage());
            duelManager.removeDuelRequest(player.getUniqueId());
            return;
        }

        duelManager.startDuel(challenger, player);
        duelManager.removeDuelRequest(player.getUniqueId());

        player.sendMessage(of(Messages.DUEL_CHALLENGE_ACCEPTED.getStringMessage())
                .placeholder("challenger", challenger.getName())
                .build());
        challenger.sendMessage(of(Messages.DUEL_ACCEPTED.getStringMessage())
                .placeholder("player", player.getName())
                .build());
    }

    @Execute(name = "top")
    void top(@Context Player player) {
        CompletableFuture<List<PlayerStats>> leaderboardFuture = duelManager.getLeaderBoard(10);

        player.sendMessage(Component.text("Top Duelists:")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));

        leaderboardFuture.thenAccept(leaderboard -> {
            for (PlayerStats stats : leaderboard) {
                String playerName = Bukkit.getOfflinePlayer(stats.uuid()).getName();
                int wins = stats.duelWon();
                int losses = stats.duelLost();

                Component message = Component.text()
                        .append(Component.text("[")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(wins)
                                .color(NamedTextColor.YELLOW))
                        .append(Component.text("-")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(losses)
                                .color(NamedTextColor.RED))
                        .append(Component.text("] ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text(playerName)
                                .color(NamedTextColor.AQUA))
                        .build();

                player.sendMessage(message);
            }
        }).exceptionally(ex -> {
            player.sendMessage(Component.text("Failed to retrieve leaderboard: " + ex.getMessage())
                    .color(NamedTextColor.RED));
            return null;
        });
    }

}