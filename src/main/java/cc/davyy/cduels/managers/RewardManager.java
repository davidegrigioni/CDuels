package cc.davyy.cduels.managers;

import cc.davyy.cduels.utils.Messages;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static cc.davyy.cduels.utils.ConfigUtils.getConfig;
import static cc.davyy.cduels.utils.ConfigUtils.getMessage;
import static cc.davyy.cduels.utils.TxtUtils.of;

@Singleton
public class RewardManager {

    public void applyRewardToPlayer(@NotNull Player player) {
        List<String> items = getConfig().getStringList("rewards.duel_winner.items");

        items.stream()
                .map(this::parseItemStack)
                .forEach(itemStack -> player.getInventory().addItem(itemStack));

        String duelReward = getMessage(Messages.DUEL_REWARD);
        player.sendMessage(of(duelReward)
                .build());
    }

    private ItemStack parseItemStack(@NotNull String itemString) {
        String[] parts = itemString.split(" ");
        Material material = Material.getMaterial(parts[0]);
        int amount = Integer.parseInt(parts[1]);
        return ItemStack.of(Objects.requireNonNull(material), amount);
    }

}