import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {

    private Card card(Rank rank) {
        return new Card(Suit.CLUBS, rank);
    }

    /**
     * Создаёт игру с настоящим игроком, читающим ввод из строки.
     * Порядок выдачи карт: игрок (2 карты), дилер (2 карты), далее по мере добора.
     */
    private Game createGame(Deck deck, String playerInput) {
        Player player = new Player("Player", deck, new Scanner(playerInput));
        Dealer dealer = new Dealer("Dealer", deck);
        return new Game(player, dealer);
    }

    /**
     * Проигрывает раунд, подавляя вывод игры в консоль.
     */
    private void playRound(Game game, int round) {
        PrintStream original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try {
            game.turn(round);
        } finally {
            System.setOut(original);
        }
    }

    @Test
    void playerBustsAndDealerWins() {
        // Игрок: 10 + 10 = 20, затем берёт 10 -> 30 (перебор).
        // Дилер: 10 + 7 = 17.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.TEN),
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN)
        ));
        Game game = createGame(deck, "1\n");

        playRound(game, 1);

        assertEquals(0, game.getPlayerWins());
        assertEquals(1, game.getDealerWins());
    }

    @Test
    void playerStandsAndWinsWithHigherScore() {
        // Игрок: 10 + 9 = 19. Дилер: 10 + 7 = 17.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.NINE),
            card(Rank.TEN),
            card(Rank.SEVEN)
        ));
        Game game = createGame(deck, "0\n");

        playRound(game, 1);

        assertEquals(1, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
    }

    @Test
    void playerStandsAndLosesWithLowerScore() {
        // Игрок: 10 + 7 = 17. Дилер: 10 + 9 = 19.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.NINE)
        ));
        Game game = createGame(deck, "0\n");

        playRound(game, 1);

        assertEquals(0, game.getPlayerWins());
        assertEquals(1, game.getDealerWins());
    }

    @Test
    void playerStandsAndDrawsWithEqualScore() {
        // Игрок: 10 + 7 = 17. Дилер: 10 + 7 = 17.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.SEVEN)
        ));
        Game game = createGame(deck, "0\n");

        playRound(game, 1);

        assertEquals(0, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
    }

    @Test
    void dealerTakesCardWithoutBusting() {
        // Игрок: 10 + 7 = 17 (стоит).
        // Дилер: 10 + 5 = 15 -> берёт 2 -> 17 (не перебрал).
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.FIVE),
            card(Rank.TWO)
        ));
        Game game = createGame(deck, "0\n");

        playRound(game, 1);

        // 17:17 -> ничья, никто не получил победу
        assertEquals(0, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
    }

    @Test
    void dealerTakesCardAndBusts() {
        // Игрок: 10 + 7 = 17 (стоит).
        // Дилер: 10 + 6 = 16 -> берёт 10 -> 26 (перебор).
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.SIX),
            card(Rank.TEN)
        ));
        Game game = createGame(deck, "0\n");

        playRound(game, 1);

        assertEquals(1, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
    }

    @Test
    void playerBlackjackAtDealWins() {
        // Игрок: Туз + 10 = 21. Дилер: 10 + 7.
        Deck deck = new Deck(List.of(
            card(Rank.ACE),
            card(Rank.TEN),
            card(Rank.TEN),
            card(Rank.SEVEN)
        ));
        Game game = createGame(deck, "");

        playRound(game, 1);

        assertEquals(1, game.getPlayerWins());
        assertEquals(0, game.getDealerWins());
    }

    @Test
    void dealerBlackjackAtDealWins() {
        // Игрок: 10 + 7 = 17. Дилер: Туз + 10 = 21.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.ACE),
            card(Rank.TEN)
        ));
        Game game = createGame(deck, "");

        playRound(game, 1);

        assertEquals(0, game.getPlayerWins());
        assertEquals(1, game.getDealerWins());
    }

    @Test
    void severalRoundsUpdateScore() {
        // Раунд 1: игрок 19, дилер 17 -> победа игрока.
        // Раунд 2: игрок 17, дилер 19 -> победа дилера.
        // Раунд 3: 17 : 17 -> ничья.
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.NINE),
            card(Rank.TEN),
            card(Rank.SEVEN),

            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.NINE),

            card(Rank.TEN),
            card(Rank.SEVEN),
            card(Rank.TEN),
            card(Rank.SEVEN)
        ));
        Player player = new Player("Player", deck, new Scanner("0\n0\n0\n"));
        Dealer dealer = new Dealer("Dealer", deck);
        Game game = new Game(player, dealer);

        PrintStream original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try {
            game.turn(1);
            game.turn(2);
            game.turn(3);
        } finally {
            System.setOut(original);
        }

        assertEquals(1, game.getPlayerWins());
        assertEquals(1, game.getDealerWins());
    }
}
