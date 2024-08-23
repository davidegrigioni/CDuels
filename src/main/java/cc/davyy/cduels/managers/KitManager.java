package cc.davyy.cduels.managers;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.model.Kit;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Singleton
public class KitManager {

    private final CDuels instance;
    private final List<Kit> kits = new ArrayList<>();

    @Inject
    public KitManager(CDuels instance) {
        this.instance = instance;
        loadKits();
    }

    public void loadKits() {
        Set<String> kitSection = instance.getConfiguration().singleLayerKeySet("kits");

        kitSection.forEach(kitName -> {
            String permission = instance.getConfiguration().getString("kits." + kitName + ".permission");
            List<ItemStack> items = new ArrayList<>();
            List<String> itemList = instance.getConfiguration().getStringList("kits." + kitName + ".items");

            itemList.forEach(itemString -> {
                String[] itemData = itemString.split(":");
                Material material = Material.getMaterial(itemData[0]);

                if (material != null) {
                    int amount = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 1;
                    items.add(ItemStack.of(material, amount));
                } else {
                    instance.getLogger().warning("Invalid material: " + itemData[0] + " in kit " + kitName);
                }
            });

            kits.add(Kit.of(kitName, items, permission));
        });

    }

    public void assignKit(@NotNull Player player, @NotNull String kitName) {
        Optional<Kit> kitOptional = getKitByName(kitName);

        kitOptional.ifPresentOrElse(
                kit -> {
                    if (!player.hasPermission(kit.permission())) {
                        player.sendMessage("You do not have permission to use this kit!");
                        return;
                    }

                    player.getInventory().clear();
                    kit.items().forEach(item -> player.getInventory().addItem(item));
                    player.sendMessage("You have received the " + kit.name() + " kit!");
                },
                () -> player.sendMessage("Kit not found!")
        );
    }

    public Optional<Kit> getKitByName(@NotNull String name) {
        return kits.stream()
                .filter(kit -> kit.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Kit> getKits() { return kits; }

}