import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorTest {
    private static class TestActor extends Actor {
        TestActor(Deck deck) {
            super("Test", deck);
        }

        @Override
        boolean shouldTake() {
            return true;
        }

        void addCard(Card card) {
            hand.add(card);
        }
    }

    private Card card(Rank rank) {
        return new Card(Suit.CLUBS, rank);
    }

    @Test
    void emptyHandHasZeroScore() {
        Actor actor = new TestActor(new Deck());

        assertEquals(0, actor.getScore());
    }

    @Test
    void scoreAddsCardRanks() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.TWO));
        actor.addCard(card(Rank.FIVE));

        assertEquals(7, actor.getScore());
    }

    @Test
    void aceCountsAsElevenWhenPossible() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.ACE));
        actor.addCard(card(Rank.FIVE));

        assertEquals(16, actor.getScore());
    }

    @Test
    void aceCountsAsOneWhenElevenWouldBust() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.ACE));
        actor.addCard(card(Rank.TEN));
        actor.addCard(card(Rank.TEN));

        assertEquals(21, actor.getScore());
    }

    @Test
    void multipleAcesUseCorrectValues() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.ACE));
        actor.addCard(card(Rank.ACE));
        actor.addCard(card(Rank.EIGHT));

        assertEquals(20, actor.getScore());
    }

    @Test
    void actorBustsAbove21() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.TEN));
        actor.addCard(card(Rank.TEN));
        actor.addCard(card(Rank.TEN));

        assertTrue(actor.isBusted());
    }

    @Test
    void actorDoesNotBustAt21() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.TEN));
        actor.addCard(card(Rank.TEN));
        actor.addCard(card(Rank.ACE));

        assertFalse(actor.isBusted());
    }

    @Test
    void resetHandRemovesAllCards() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.TWO));
        actor.resetHand();

        assertEquals(0, actor.getHand().size());
        assertEquals(0, actor.getScore());
    }

    @Test
    void takeCardAddsCardToHand() {
        Card expected = card(Rank.ACE);
        Deck deck = new Deck(List.of(expected));
        TestActor actor = new TestActor(deck);

        Card actual = actor.takeCard();

        assertEquals(expected, actual);
        assertEquals(1, actor.getHand().size());
        assertEquals(expected, actor.getHand().get(0));
    }

    @Test
    void dealInitialCardsAddsTwoCards() {
        Deck deck = new Deck(List.of(
            card(Rank.TWO),
            card(Rank.THREE)
        ));
        TestActor actor = new TestActor(deck);

        actor.dealInitialCards();

        assertEquals(2, actor.getHand().size());
        assertEquals(5, actor.getScore());
    }

    @Test
    void visibleHandContainsAllCardsByDefault() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(card(Rank.TWO));
        actor.addCard(card(Rank.THREE));

        assertEquals(actor.getHand(), actor.getVisibleHand());
    }
}
