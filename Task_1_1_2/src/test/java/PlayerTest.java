import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {
    @Test
    void playerTakesCardOnInputOne() {
        Scanner scanner = new Scanner("1\n");
        Player player = new Player(
            "Player",
            new Deck(),
            scanner
        );

        assertTrue(player.shouldTake());
    }

    @Test
    void playerStopsOnInputZero() {
        Scanner scanner = new Scanner("0\n");
        Player player = new Player(
            "Player",
            new Deck(),
            scanner
        );

        assertFalse(player.shouldTake());
    }

    @Test
    void playerRepeatsAfterInvalidInput() {
        Scanner scanner = new Scanner("x\n1\n");
        Player player = new Player(
            "Player",
            new Deck(),
            scanner
        );

        assertTrue(player.shouldTake());
    }
}
