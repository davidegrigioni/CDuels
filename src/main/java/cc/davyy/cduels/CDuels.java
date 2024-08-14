package cc.davyy.cduels;

import cc.davyy.cduels.commands.KitCommand;
import cc.davyy.cduels.managers.KitManager;
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
        kitManager = new KitManager(this);

        registerConfig();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }
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