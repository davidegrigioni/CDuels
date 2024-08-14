package cc.davyy.cduels.model;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Kit(String name, List<ItemStack> items, String permission) {}