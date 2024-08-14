package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.model.Kit;
import com.google.inject.Singleton;
import de.leonhard.storage.sections.FlatFileSection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class KitManager {

    private final CDuels instance;
    private final List<Kit> kits = new ArrayList<>();

    public KitManager(CDuels instance) {
        this.instance = instance;
        loadKits();
    }

    public void loadKits() {
        FlatFileSection kitSection = instance.getConfiguration().getSection("kits");

        for (String kitName : kitSection.keySet()) {
            String permission = instance.getConfiguration().getString("kits." + kitName + ".permission");
            List<ItemStack> items = new ArrayList<>();
            List<String> itemList = instance.getConfiguration().getStringList("kits." + kitName + ".items");

            for (String itemString : itemList) {
                String[] itemData = itemString.split(":");
                Material material = Material.getMaterial(itemData[0]);

                if (material != null) {
                    int amount = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 1;
                    items.add(new ItemStack(material, amount));
                } else {
                    instance.getLogger().warning("Invalid material: " + itemData[0] + " in kit " + kitName);
                }
            }

            kits.add(new Kit(kitName, items, permission));
        }
    }

    public void assignKit(@NotNull Player player, @NotNull String kitName) {
        Kit kit = getKitByName(kitName);

        if (kit != null) {
            if (player.hasPermission(kit.permission())) {
                player.getInventory().clear();

                kit.items().forEach(item -> player.getInventory().addItem(item));

                player.sendMessage("You have received the " + kit.name() + " kit!");
            } else {
                player.sendMessage("You do not have permission to use this kit!");
            }
        } else {
            player.sendMessage("Kit not found!");
        }
    }

    public Kit getKitByName(String name) {
        return kits
                .stream()
                .filter(kit -> kit.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Kit> getKits() { return kits; }

}