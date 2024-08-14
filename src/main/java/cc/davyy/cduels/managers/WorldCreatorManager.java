package cc.davyy.cduels.managers;

import com.google.inject.Singleton;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

@Singleton
public class WorldCreatorManager {

    public World createDuelWorld(String worldName) {
        WorldCreator creator = new WorldCreator(worldName);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        World world = creator.createWorld();

        if (world != null) {
            setupWorldProperties(world);
        }

        return world;
    }

    private void setupWorldProperties(World world) {
        world.setPVP(true);
        world.setAutoSave(false);
        world.setTime(6000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(new Location(world, 0, 0, 0));
        border.setSize(500);
    }

    public void deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            deleteWorldFiles(worldName);
        }
    }

    private void deleteWorldFiles(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (worldFolder.exists()) {
            try {
                deleteFolder(worldFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFolder(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }

}