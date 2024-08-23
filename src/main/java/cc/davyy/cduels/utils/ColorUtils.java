package cc.davyy.cduels.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class ColorUtils {

    private ColorUtils() {}

    public static Component colorize(@NotNull String message) { return MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false); }

}