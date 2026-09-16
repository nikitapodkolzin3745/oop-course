
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {
    @Test
    void playerTakesCardOnInputOne() {
        InputStream original = System.in;

        try {
            System.setIn(new ByteArrayInputStream("1\n".getBytes()));

            Player player = new Player("Player", new Deck());

            assertTrue(player.shouldTake());
        } finally {
            System.setIn(original);
        }
    }

    @Test
    void playerStopsOnInputZero() {
        InputStream original = System.in;

        try {
            System.setIn(new ByteArrayInputStream("0\n".getBytes()));

            Player player = new Player("Player", new Deck());

            assertFalse(player.shouldTake());
        } finally {
            System.setIn(original);
        }
    }

    @Test
    void playerRepeatsAfterInvalidInput() {
        InputStream original = System.in;

        try {
            System.setIn(new ByteArrayInputStream("x\n1\n".getBytes()));

            Player player = new Player("Player", new Deck());

            assertTrue(player.shouldTake());
        } finally {
            System.setIn(original);
        }
    }
}
