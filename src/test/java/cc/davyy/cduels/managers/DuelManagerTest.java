package cc.davyy.cduels.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DuelManagerTest {

    private ServerMock serverMock;
    private DuelManager duelManager;
    private WorldCreatorManager worldCreatorManager;

    @BeforeEach
    public void setUp() {
        serverMock = MockBukkit.mock();

        worldCreatorManager = mock(WorldCreatorManager.class);

        duelManager = new DuelManager(worldCreatorManager);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void startDuel() {
        PlayerMock firstPlayer = serverMock.addPlayer("FirstPlayer");
        PlayerMock secondPlayer = serverMock.addPlayer("SecondPlayer");

        WorldMock duelWorld = serverMock.addSimpleWorld("duel_mock_world");
        when(worldCreatorManager.createDuelWorld(anyString())).thenReturn(duelWorld);

        duelManager.startDuel(firstPlayer, secondPlayer);

        Location expectedFirstPlayerSpawn = new Location(duelWorld, 100, 1, 100);
        Location expectedSecondPlayerSpawn = new Location(duelWorld, -100, 1, -100);

        firstPlayer.assertTeleported(expectedFirstPlayerSpawn, 100);
        secondPlayer.assertTeleported(expectedSecondPlayerSpawn, 100);

        firstPlayer.assertSaid("Duel started! You have been teleported to the duel world.");
        secondPlayer.assertSaid("Duel started! You have been teleported to the duel world.");

        firstPlayer.assertNoMoreSaid();
        secondPlayer.assertNoMoreSaid();
    }

}