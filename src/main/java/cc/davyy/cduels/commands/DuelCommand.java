package cc.davyy.cduels.commands;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.managers.DatabaseManager;
import cc.davyy.cduels.managers.DuelManager;
import cc.davyy.cduels.model.PlayerStats;
import com.google.inject.Inject;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.List;

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
            player.sendMessage("The specified player is not online.");
            return;
        }

        if (player.equals(target)) {
            player.sendMessage("You cannot duel yourself.");
            return;
        }

        duelManager.sendDuelRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("You have invited " + target.getName() + " to a duel.");
        target.sendMessage(player.getName() + " has invited you to a duel! Use /duel accept to accept.");
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
    void top(@Context Player player, @Arg int limit) {
        List<PlayerStats> leaderboard = databaseManager.getLeaderboard(limit);
        player.sendMessage("Top Duelists:");
        leaderboard.forEach(stats -> player.sendMessage(stats.uuid() + ": Wins=" + stats.duelWon() + " Losses=" + stats.duelLost()));
    }

}