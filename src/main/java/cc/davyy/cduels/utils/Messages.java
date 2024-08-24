package cc.davyy.cduels.utils;

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
    DUEL_ENDED("messages.duel-end"),
    DUEL_WORLD_CREATION_FAILED("messages.dw-creation-failed");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

}