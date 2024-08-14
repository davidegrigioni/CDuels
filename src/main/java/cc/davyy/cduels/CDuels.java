package cc.davyy.cduels;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class CDuels extends JavaPlugin {

    private Yaml config;

    @Override
    public void onEnable() {
        registerConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerConfig() {
        config = SimplixBuilder.fromFile(
                new File(getDataFolder(), "config.yml"))
                .addInputStreamFromResource("config.yml")
                .createYaml();
    }

    @NotNull
    public Yaml getConfiguration() { return config; }

}