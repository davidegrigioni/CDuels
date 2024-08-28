package cc.davyy.cduels.gui;

import cc.davyy.cduels.model.Kit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KitSelectionGUI implements InventoryHolder {

    private final Inventory inventory;

    public KitSelectionGUI(@NotNull List<Kit> kits) {
        this.inventory = Bukkit.createInventory(this, 9, Component.text("Select Your Kit"));

        for (int i = 0; i < kits.size(); i++) {
            ItemStack displayItem = ItemStack.of(Material.CHEST);
            inventory.setItem(i, displayItem);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

}