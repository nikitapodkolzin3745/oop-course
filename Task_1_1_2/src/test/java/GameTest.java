import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GameTest {

    private static class TestPlayer extends Player {
        TestPlayer(Deck deck) {
            super("Test", deck, new Scanner(""));
        }

        @Override
        boolean shouldTake() {
            return false;
        }
    }

    private Card card(Rank rank) {
        return new Card(Suit.CLUBS, rank);
    }

    @Test
    void gameCanComplete() {
        Deck deck = new Deck(List.of(
                card(Rank.TEN),
                card(Rank.SEVEN),
                card(Rank.TEN),
                card(Rank.SEVEN)
        ));

        Player player = new TestPlayer(deck);
        Dealer dealer = new Dealer("Dealer", deck);
        Game game = new Game(player, dealer);

        assertDoesNotThrow(() -> game.turn(1));
    }

    @Test
    void severalRoundsCanComplete() {
        Deck deck = new Deck(List.of(
                // Раунд 1:
                // игрок: 10 + 7 = 17
                // дилер: 10 + 7 = 17
                card(Rank.TEN),
                card(Rank.SEVEN),
                card(Rank.TEN),
                card(Rank.SEVEN),

                // Раунд 2:
                // игрок: 9 + 8 = 17
                // дилер: 10 + 8 = 18
                card(Rank.NINE),
                card(Rank.EIGHT),
                card(Rank.TEN),
                card(Rank.EIGHT),

                // Раунд 3:
                // игрок: 10 + 9 = 19
                // дилер: 9 + 8 = 17
                card(Rank.TEN),
                card(Rank.NINE),
                card(Rank.NINE),
                card(Rank.EIGHT)
        ));

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
