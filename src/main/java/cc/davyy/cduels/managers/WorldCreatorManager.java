package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

@Singleton
public class WorldCreatorManager {

    private final CDuels instance;

    private final ComponentLogger componentLogger = ComponentLogger.logger(WorldCreatorManager.class);

    @Inject
    public WorldCreatorManager(CDuels instance) {
        this.instance = instance;
    }

    /**
     * Creates a new world for duels with specific properties.
     *
     * @param worldName The name of the world to create.
     * @return The created {@link World} instance, or {@code null} if world creation failed.
     */
    public World createDuelWorld(@NotNull String worldName) {
        WorldCreator creator = new WorldCreator(worldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generateStructures(false);

        World world = creator.createWorld();

        if (world != null) {
            setupWorldProperties(world);
        }

        return world;
    }

    /**
     * Sets up properties for the duel world.
     *
     * @param world The world to configure.
     */
    private void setupWorldProperties(@NotNull World world) {
        world.setPVP(true);
        world.setAutoSave(false);
        world.setTime(6000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(new Location(world, 0, 0, 0));
        border.setSize(500);
    }

    /**
     * Deletes a world by name.
     *
     * @param worldName The name of the world to delete.
     */
    public void deleteWorld(@NotNull String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            componentLogger.error("World '{}' not found and cannot be deleted.", worldName);
            return;
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            if (!Bukkit.unloadWorld(world, false)) {
                componentLogger.error("Failed to unload world '{}'.", worldName);
                return;
            }

            componentLogger.info("World '{}' unloaded successfully.", worldName);
            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> deleteWorldFiles(worldName));
        });
    }

    /**
     * Deletes the files associated with a world.
     *
     * @param worldName The name of the world whose files should be deleted.
     */
    private void deleteWorldFiles(@NotNull String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);

        if (!worldFolder.exists()) {
            componentLogger.error("World folder for '{}' does not exist.", worldName);
            return;
        }

        try {
            deleteFolder(worldFolder);
        } catch (IOException ex) {
            componentLogger.error("Failed to delete world files for '{}': {}", worldName, ex.getMessage());
        }
    }

    /**
     * Recursively deletes a folder and its contents.
     *
     * @param folder The folder to delete.
     * @throws IOException If an I/O error occurs while deleting files or directories.
     */
    private void deleteFolder(@NotNull File folder) throws IOException {
        File[] files = folder.listFiles();

        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            });
        }

        if (!folder.delete()) {
            throw new IOException("Failed to delete folder: " + folder.getAbsolutePath());
        }
    }

}