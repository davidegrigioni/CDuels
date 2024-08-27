package cc.davyy.cduels.utils;

import de.leonhard.storage.Yaml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import static cc.davyy.cduels.utils.ConfigUtils.getConfig;

public enum Messages {

    DUEL_STARTED("messages.duel-started"),
    NO_PERMISSION("messages.no-permission"),
    KIT_NOT_FOUND("messages.kit-not-found"),
    DUEL_REWARD("messages.duel-reward"),
    KIT_RECEIVED("messages.kit-received"),
    PLAYER_NOT_ONLINE("messages.player-not-online"),
    DUEL_YOURSELF("messages.duel-yourself"),
    DUEL_INVITE_SEND("messages.duel-invite-send"),
    DUEL_INVITE_RECEIVED("messages.duel-invite-received"),
    DUEL_WON("messages.duel-won"),
    DUEL_NO_PENDING_REQUEST("messages.duel-no-pending"),
    PLAYER_NO_LONGER_ONLINE("messages.player-no-longer-online"),
    DUEL_CHALLENGE_ACCEPTED("messages.duel-challenge-accepted"),
    DUEL_ACCEPTED("messages.duel-accepted"),
    DUEL_LOST("messages.duel-lost"),
    DUEL_WORLD_CREATION_FAILED("messages.dw-creation-failed");

    private final String message;
    private final static Yaml config = getConfig();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    Messages(String message) {
        this.message = message;
    }

    public @NotNull String getStringMessage() { return message; }

    public @NotNull Component getMessage() {
        return MM
                .deserialize(config.get(message, message));
    }

}