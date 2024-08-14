package cc.davyy.cduels.commands;

import cc.davyy.cduels.managers.KitManager;
import com.google.inject.Inject;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "kit")
public class KitCommand {

    private final KitManager kitManager;

    @Inject
    public KitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Execute(name = "claim")
    void claimKit(@Context Player player, @Arg String name) {
        kitManager.assignKit(player, name);
    }

}