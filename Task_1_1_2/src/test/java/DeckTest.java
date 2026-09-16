
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeckTest {
    @Test
    void deckContains52Cards() {
        Deck deck = new Deck();
        Set<String> cards = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.take();

            assertNotNull(card);
            cards.add(card.toString());
        }

        assertEquals(52, cards.size());
    }

    @Test
    void emptyDeckReturnsNull() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            deck.take();
        }

        assertNull(deck.take());
    }
}
