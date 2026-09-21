import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Представляет колоду из 52 игральных карт.
 */
public class Deck {
    private final Deque<Card> deck;

    /**
     * Создает обычную перемешанную колоду из 52 карт.
     */
    public Deck() {
        List<Card> cards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }

        Collections.shuffle(cards);
        deck = new ArrayDeque<>(cards);
    }

    /**
     * Создает колоду из заранее заданного набора карт.
     * Используется в том числе для тестирования.
     *
     * @param cards карты колоды в порядке их выдачи
     */
    public Deck(List<Card> cards) {
        deck = new ArrayDeque<>(cards);
    }

    /**
     * Возвращает следующую карту из колоды.
     *
     * @return карта из колоды или null, если колода пуста
     */
    public Card take() {
        return deck.pollFirst();
    }
}
