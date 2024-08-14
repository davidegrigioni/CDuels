package cc.davyy.cduels;

import cc.davyy.cduels.commands.KitCommand;
import cc.davyy.cduels.managers.KitManager;
import cc.davyy.cduels.model.CModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class CDuels extends JavaPlugin {

    private LiteCommands<CommandSender> liteCommands;
    private Yaml config;

    private KitManager kitManager;

    @Override
    public void onEnable() {
        injectGuice();

        registerConfig();

        registerCommands();
    }

    @Override
    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }
    }

    private void injectGuice() {
        Injector injector = Guice.createInjector(new CModule(this));
        injector.injectMembers(this);

        kitManager = injector.getInstance(KitManager.class);
    }

    private void registerConfig() {
        config = SimplixBuilder.fromFile(
                new File(getDataFolder(), "config.yml"))
                .addInputStreamFromResource("config.yml")
                .createYaml();
    }

    private void registerCommands() {
        liteCommands = LiteBukkitFactory.builder("cduels", this)
                .commands(LiteCommandsAnnotations.of(
                        new KitCommand(kitManager)
                ))
                .build();
    }

    @NotNull
    public Yaml getConfiguration() { return config; }

}