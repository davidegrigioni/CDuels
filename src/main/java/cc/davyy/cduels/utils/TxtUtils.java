package cc.davyy.cduels.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TxtUtils {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private String text;
    private static final Pattern legacyRegex = Pattern.compile("[§&][0-9a-fk-or]");
    private static final Map<String, String> legacyToMiniMessage = Map.ofEntries(
            Map.entry("§0", "<black>"),
            Map.entry("§1", "<dark_blue>"),
            Map.entry("§2", "<dark_green>"),
            Map.entry("§3", "<dark_aqua>"),
            Map.entry("§4", "<dark_red>"),
            Map.entry("§5", "<dark_purple>"),
            Map.entry("§6", "<gold>"),
            Map.entry("§7", "<gray>"),
            Map.entry("§8", "<dark_gray>"),
            Map.entry("§9", "<blue>"),
            Map.entry("§a", "<green>"),
            Map.entry("§b", "<aqua>"),
            Map.entry("§c", "<red>"),
            Map.entry("§d", "<light_purple>"),
            Map.entry("§e", "<yellow>"),
            Map.entry("§f", "<white>"),
            Map.entry("§k", "<obfuscated>"),
            Map.entry("§l", "<bold>"),
            Map.entry("§m", "<strikethrough>"),
            Map.entry("§n", "<underlined>"),
            Map.entry("§o", "<italic>"),
            Map.entry("§r", "<reset>"),
            Map.entry("&0", "<black>"),
            Map.entry("&1", "<dark_blue>"),
            Map.entry("&2", "<dark_green>"),
            Map.entry("&3", "<dark_aqua>"),
            Map.entry("&4", "<dark_red>"),
            Map.entry("&5", "<dark_purple>"),
            Map.entry("&6", "<gold>"),
            Map.entry("&7", "<gray>"),
            Map.entry("&8", "<dark_gray>"),
            Map.entry("&9", "<blue>"),
            Map.entry("&a", "<green>"),
            Map.entry("&b", "<aqua>"),
            Map.entry("&c", "<red>"),
            Map.entry("&d", "<light_purple>"),
            Map.entry("&e", "<yellow>"),
            Map.entry("&f", "<white>"),
            Map.entry("&k", "<obfuscated>"),
            Map.entry("&l", "<bold>"),
            Map.entry("&m", "<strikethrough>"),
            Map.entry("&n", "<underlined>"),
            Map.entry("&o", "<italic>"),
            Map.entry("&r", "<reset>")
    );
    private final List<TagResolver> minimessagePlaceholders = new ArrayList<>();

    private TxtUtils() {}

    /**
     * Creates a new instance of ColorUtils with the provided text.
     *
     * @param text the text to be formatted
     * @return a new instance of ColorUtils
     */
    public static @NotNull TxtUtils of(String text) { return new TxtUtils().setText(text); }

    /**
     * Adds a placeholder to the text.
     *
     * @param key         the key of the placeholder
     * @param value the replacement text for the placeholder
     * @return this ColorUtils instance
     */
    public @NotNull TxtUtils placeholder(@Subst("test_placeholder") @NotNull String key, String value) {
        this.minimessagePlaceholders.add(
                Placeholder.component(
                        key,
                        of(value).parseLegacy().build()
                )
        );

        return this;
    }

    public @NotNull TxtUtils placeholder(@Subst("test_placeholder") @NotNull String key, @NotNull ComponentLike value) {
        this.minimessagePlaceholders.add(
                Placeholder.component(
                        key,
                        value
                )
        );

        return this;
    }

    /**
     * Parse legacy color codes and formatting, including <code>{@literal &}</code> and
     * <code>{@literal §}</code> into their minimessage equivalents.
     *
     * @return the color parser object
     */
    public @NotNull TxtUtils parseLegacy() {
        String textParsed = getText();
        final @NotNull Matcher matcher = legacyRegex.matcher(textParsed);

        while (matcher.find()) {
            final String match = matcher.group();
            textParsed = textParsed.replace(match, legacyToMiniMessage.getOrDefault(match, match));
        }

        setText(textParsed);

        return this;
    }

    private String getText() { return text; }

    private @NotNull TxtUtils setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Builds the formatted Adventure component.
     *
     * @return the formatted Adventure component
     */
    public @NotNull Component build() {
        return mm.deserialize(getText(), this.minimessagePlaceholders.toArray(new TagResolver[0]))
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

}