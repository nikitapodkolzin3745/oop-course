
import org.junit.jupiter.api.Test;

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

    @Test
    void emptyHandHasZeroScore() {
        Actor actor = new TestActor(new Deck());

        assertEquals(0, actor.getScore());
    }

    @Test
    void scoreAddsCardRanks() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 1));
        actor.addCard(new Card(0, 4));

        assertEquals(7, actor.getScore());
    }

    @Test
    void aceCountsAsElevenWhenPossible() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 0));
        actor.addCard(new Card(0, 4));

        assertEquals(16, actor.getScore());
    }

    @Test
    void aceCountsAsOneWhenElevenWouldBust() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 0));
        actor.addCard(new Card(0, 9));
        actor.addCard(new Card(0, 9));

        assertEquals(21, actor.getScore());
    }

    @Test
    void multipleAcesUseCorrectValues() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 0));
        actor.addCard(new Card(0, 0));
        actor.addCard(new Card(0, 7));

        assertEquals(20, actor.getScore());
    }

    @Test
    void actorBustsAbove21() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 9));
        actor.addCard(new Card(0, 9));
        actor.addCard(new Card(0, 9));

        assertTrue(actor.isBusted());
    }

    @Test
    void actorDoesNotBustAt21() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 9));
        actor.addCard(new Card(0, 9));
        actor.addCard(new Card(0, 0));

        assertFalse(actor.isBusted());
    }

    @Test
    void resetHandRemovesAllCards() {
        TestActor actor = new TestActor(new Deck());

        actor.addCard(new Card(0, 1));
        actor.resetHand();

        assertEquals(0, actor.getHand().size());
        assertEquals(0, actor.getScore());
    }

    @Test
    void takeACardAddsCardToHand() {
        TestActor actor = new TestActor(new Deck());

        Card card = actor.takeACard();

        assertEquals(1, actor.getHand().size());
        assertEquals(card, actor.getHand().get(0));
    }

    @Test
    void dealInitialCardsAddsTwoCards() {
        TestActor actor = new TestActor(new Deck());

        actor.dealInitialCards();

        assertEquals(2, actor.getHand().size());
    }
}
