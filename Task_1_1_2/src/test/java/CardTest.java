import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {
    @Test
    void aceHasOneRank() {
        Card card = new Card(Suit.CLUBS, Rank.ACE);

        assertEquals(1, card.getRank());
    }

    @Test
    void numberCardHasCorrectRank() {
        Card card = new Card(Suit.SPADES, Rank.SEVEN);

        assertEquals(7, card.getRank());
    }

    @Test
    void faceCardHasTenRank() {
        Card card = new Card(Suit.HEARTS, Rank.JACK);

        assertEquals(10, card.getRank());
    }

    @Test
    void getMeaningReturnsCorrectName() {
        Card card = new Card(Suit.DIAMONDS, Rank.KING);

        assertEquals("Король", card.getMeaning());
    }

    @Test
    void getSuitReturnsCorrectName() {
        Card card = new Card(Suit.SPADES, Rank.ACE);

        assertEquals("Пики", card.getSuit());
    }

    @Test
    void toStringContainsCardInformation() {
        Card card = new Card(Suit.SPADES, Rank.QUEEN);

        assertEquals("Пики Дама (10)", card.toString());
    }
}
