package cc.davyy.cduels.commands;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.managers.DatabaseManager;
import cc.davyy.cduels.managers.DuelManager;
import cc.davyy.cduels.model.PlayerStats;
import cc.davyy.cduels.utils.Messages;
import com.google.inject.Inject;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.List;

import static cc.davyy.cduels.utils.ConfigUtils.getMessage;
import static cc.davyy.cduels.utils.TxtUtils.of;

@Command(name = "duel")
public class DuelCommand {

    private final CDuels instance;
    private final DuelManager duelManager;
    private final DatabaseManager databaseManager;

    @Inject
    public DuelCommand(CDuels instance, DuelManager duelManager, DatabaseManager databaseManager) {
        this.instance = instance;
        this.duelManager = duelManager;
        this.databaseManager = databaseManager;
    }

    @Execute(name = "invite")
    void inviteDuel(@Context Player player, @Arg Player target) {
        if (!target.isOnline()) {
            String playerNotOnline = getMessage(Messages.PLAYER_NOT_ONLINE);
            player.sendMessage(of(playerNotOnline)
                    .build());
            return;
        }

        if (player.equals(target)) {
            String duelYourself = getMessage(Messages.DUEL_YOURSELF);
            player.sendMessage(of(duelYourself)
                    .build());
            return;
        }

        duelManager.sendDuelRequest(player.getUniqueId(), target.getUniqueId());
        String duelInviteSend = getMessage(Messages.DUEL_INVITE_SEND);
        String duelReceived = getMessage(Messages.DUEL_INVITE_RECEIVED);
        player.sendMessage(of(duelInviteSend)
                .placeholder("target", target.getName())
                .build());
        target.sendMessage(of(duelReceived)
                .placeholder("player", player.getName())
                .build());
    }

    @Execute(name = "accept")
    void acceptDuel(@Context Player player) {
        if (!duelManager.hasDuelRequest(player.getUniqueId())) {
            player.sendMessage("You have no pending duel requests.");
            return;
        }

        Player challenger = instance.getServer().getPlayer(duelManager.getChallenger(player.getUniqueId()));
        if (challenger == null || !challenger.isOnline()) {
            player.sendMessage("The player who challenged you is no longer online.");
            duelManager.removeDuelRequest(player.getUniqueId());
            return;
        }

        duelManager.startDuel(challenger, player);

        duelManager.removeDuelRequest(player.getUniqueId());

        player.sendMessage("You have accepted the duel challenge from " + challenger.getName() + "!");
        challenger.sendMessage(player.getName() + " has accepted your duel challenge!");
    }

    @Execute(name = "top")
    void top(@Context Player player) {
        List<PlayerStats> leaderboard = databaseManager.getLeaderboard(10);
        player.sendMessage("Top Duelists:");
        leaderboard.forEach(stats -> player.sendMessage(stats.uuid() + ": Wins=" + stats.duelWon() + " Losses=" + stats.duelLost()));
    }

}