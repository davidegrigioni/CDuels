package cc.davyy.cduels.utils;

import cc.davyy.cduels.CDuels;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;

import java.io.File;

public final class ConfigUtils {

    private static Yaml config;
    private static Yaml messages;

    private ConfigUtils() {}

    public static void registerConfig(CDuels instance) {
        config = SimplixBuilder.fromFile(
                        new File(instance.getDataFolder(), "config.yml"))
                .addInputStreamFromResource("config.yml")
                .createYaml();
        messages = SimplixBuilder.fromFile(
                new File(instance.getDataFolder(), "messages.yml"))
                .addInputStreamFromResource("messages.yml")
                .createYaml();
    }

    public static Yaml getConfig() { return config; }

    public static Yaml getMessages() { return messages; }

}