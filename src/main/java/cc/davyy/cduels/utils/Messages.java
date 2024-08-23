package cc.davyy.cduels.utils;

public enum Messages {

    DUEL_STARTED("messages.duel-started"),
    NO_PERMISSION("messages.no-permission"),
    KIT_NOT_FOUND("messages.kit-not-found"),
    DUEL_ENDED("messages.duel-end"),
    DUEL_WORLD_CREATION_FAILED("messages.dw-creation-failed");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

}