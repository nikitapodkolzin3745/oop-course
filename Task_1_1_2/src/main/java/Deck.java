
import java.util.Arrays;
import java.util.Collections;

/**
 * Представляет колоду из 52 игральных карт.
 */
public class Deck {
    private Card[] deck = new Card[52];
    private int top = 0;

    /**
     * Создает колоду, заполняет ее картами и перемешивает.
     */
    Deck() {
        int i = 0;

        for (int suitId = 0; suitId < Card.suits.length; suitId++) {
            for (int meaningId = 0; meaningId < Card.meanings.length; meaningId++) {
                deck[i++] = new Card(suitId, meaningId);
            }
        }

        Collections.shuffle(Arrays.asList(deck));
    }

    /**
     * Возвращает следующую карту из колоды.
     *
     * @return карта из колоды или null, если колода пуста
     */
    public Card take() {
        if (top < deck.length) {
            return deck[top++];
        }

        return null;
    }
}