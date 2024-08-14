package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.kits.Kit;
import de.leonhard.storage.sections.FlatFileSection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private final CDuels instance;
    private final List<Kit> kits = new ArrayList<>();

    public KitManager(CDuels instance) {
        this.instance = instance;
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

    public List<Kit> getKits() { return kits; }

}