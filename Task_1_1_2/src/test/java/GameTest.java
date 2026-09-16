import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GameTest {
    private static class TestPlayer extends Player {
        TestPlayer(Deck deck) {
            super("Test", deck);
        }

        @Override
        boolean shouldTake() {
            return false;
        }
    }

    @Test
    void gameCanComplete() {
        Deck deck = new Deck();
        Player player = new TestPlayer(deck);
        Dealer dealer = new Dealer("Dealer", deck);

        Game game = new Game(player, dealer);

        assertDoesNotThrow(() -> game.turn(1));
    }
    
    @Test
    void severalRoundsCanComplete() {
        Deck deck = new Deck();
        Player player = new TestPlayer(deck);
        Dealer dealer = new Dealer("Dealer", deck);

        Game game = new Game(player, dealer);

        assertDoesNotThrow(() -> {
            game.turn(1);
            game.turn(2);
            game.turn(3);
        });
    }
}
