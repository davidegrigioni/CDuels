package cc.davyy.cduels.model;

import java.util.UUID;

public record PlayerStats(UUID uuid, int duelWon, int duelLost) {}