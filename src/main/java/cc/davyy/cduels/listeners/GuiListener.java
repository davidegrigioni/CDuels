package cc.davyy.cduels.listeners;

import cc.davyy.cduels.gui.KitSelectionGUI;
import cc.davyy.cduels.managers.KitManager;
import cc.davyy.cduels.model.Kit;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiListener implements Listener {

    private final KitManager kitManager;

    @Inject
    public GuiListener(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof KitSelectionGUI) {
            event.setCancelled(true);

            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();

            Kit selectedKit = kitManager.getKits().get(slot);

            kitManager.assignKit(player, selectedKit);

            player.closeInventory();
            player.sendMessage("You selected the " + selectedKit.name() + " kit!");
        }
    }

}