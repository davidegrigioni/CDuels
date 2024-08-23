package cc.davyy.cduels;

import cc.davyy.cduels.commands.DuelCommand;
import cc.davyy.cduels.commands.KitCommand;
import cc.davyy.cduels.managers.DatabaseManager;
import cc.davyy.cduels.managers.DuelManager;
import cc.davyy.cduels.managers.KitManager;
import cc.davyy.cduels.managers.WorldCreatorManager;
import cc.davyy.cduels.module.CModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import static cc.davyy.cduels.utils.ConfigUtils.registerConfig;

public class CDuels extends JavaPlugin {

    private LiteCommands<CommandSender> liteCommands;

    private KitManager kitManager;
    private DatabaseManager databaseManager;
    private WorldCreatorManager worldCreatorManager;
    private DuelManager duelManager;

    @Override
    public void onEnable() {
        registerConfig(this);

        injectGuice();

        registerCommands();
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }
    }

    private void injectGuice() {
        Injector injector = Guice.createInjector(new CModule(this));
        injector.injectMembers(this);

        kitManager = injector.getInstance(KitManager.class);
        databaseManager = injector.getInstance(DatabaseManager.class);
        worldCreatorManager = injector.getInstance(WorldCreatorManager.class);
        duelManager = injector.getInstance(DuelManager.class);
    }

    private void registerCommands() {
        this.liteCommands = LiteBukkitFactory.builder("cduels", this)
                .commands(LiteCommandsAnnotations.of(
                        new DuelCommand(this, duelManager),
                        new KitCommand(kitManager)
                ))
                .build();
    }

}