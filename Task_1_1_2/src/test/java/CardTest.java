
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {
    @Test
    void aceHasOneRank() {
        Card card = new Card(0, 0);

        assertEquals(1, card.getRank());
    }

    @Test
    void numberCardHasCorrectRank() {
        Card card = new Card(1, 6);

        assertEquals(7, card.getRank());
    }

    @Test
    void faceCardHasTenRank() {
        Card card = new Card(2, 10);

        assertEquals(10, card.getRank());
    }

    @Test
    void getMeaningReturnsCorrectName() {
        Card card = new Card(3, 12);

        assertEquals("Король", card.getMeaning());
    }

    @Test
    void getSuitReturnsCorrectName() {
        Card card = new Card(1, 0);

        assertEquals("Пики", card.getSuit());
    }

    @Test
    void toStringContainsCardInformation() {
        Card card = new Card(1, 11);

        assertEquals("Пики Дама (10)", card.toString());
    }
}
