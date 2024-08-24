package cc.davyy.cduels.managers;

import cc.davyy.cduels.model.Kit;
import cc.davyy.cduels.utils.Messages;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static cc.davyy.cduels.utils.ConfigUtils.getConfig;
import static cc.davyy.cduels.utils.ConfigUtils.getMessage;
import static cc.davyy.cduels.utils.TxtUtils.of;

@Singleton
public class KitManager {

    private final ComponentLogger componentLogger = ComponentLogger.logger(KitManager.class);

    private final List<Kit> kits = new ArrayList<>();

    @Inject
    public KitManager() {
        loadKits();
    }

    public void loadKits() {
        Set<String> kitSection = getConfig().singleLayerKeySet("kits");

        kitSection.forEach(kitName -> {
            String permission = getConfig().getString("kits." + kitName + ".permission");
            List<ItemStack> items = new ArrayList<>();
            List<String> itemList = getConfig().getStringList("kits." + kitName + ".items");

            itemList.forEach(itemString -> {
                String[] itemData = itemString.split(":");
                Material material = Material.getMaterial(itemData[0]);

                if (material == null) {
                    componentLogger.info("Invalid material: {} in kit {}", itemData[0], kitName);
                    return;
                }

                int amount = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 1;
                items.add(ItemStack.of(material, amount));
            });

            kits.add(Kit.of(kitName, items, permission));
        });

    }

    public void assignKit(@NotNull Player player, @NotNull String kitName) {
        Optional<Kit> kitOptional = getKitByName(kitName);

        kitOptional.ifPresentOrElse(
                kit -> {
                    if (!player.hasPermission(kit.permission())) {
                        String noPerm = getMessage(Messages.NO_PERMISSION);
                        player.sendMessage(of(noPerm)
                                .build());
                        return;
                    }

                    player.getInventory().clear();
                    kit.items().forEach(item -> player.getInventory().addItem(item));
                    String kitReceived = getMessage(Messages.KIT_RECEIVED);
                    player.sendMessage(of(kitReceived)
                            .placeholder("kitname", kit.name())
                            .build());
                },
                () -> {
                    String kitNotFound = getMessage(Messages.KIT_NOT_FOUND);
                    player.sendMessage(of(kitNotFound)
                            .build());
                }
        );
    }

    public Optional<Kit> getKitByName(@NotNull String name) {
        return kits.stream()
                .filter(kit -> kit.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Kit> getKits() { return kits; }

}