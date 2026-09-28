import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealerTest {
    private Card card(Rank rank) {
        return new Card(Suit.CLUBS, rank);
    }

    @Test
    void dealerWantsCardBelow17() {
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.FIVE),
            card(Rank.TWO)
        ));

        Dealer dealer = new Dealer("Dealer", deck);
        dealer.dealInitialCards();

        assertEquals(15, dealer.getScore());
        assertTrue(dealer.shouldTake());

        dealer.takeCard();

        assertEquals(17, dealer.getScore());
        assertFalse(dealer.shouldTake());
    }

    @Test
    void dealerHidesCardByDefault() {
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.FIVE)
        ));

        Dealer dealer = new Dealer("Dealer", deck);
        dealer.dealInitialCards();

        assertTrue(dealer.isCardHidden());
        assertEquals(1, dealer.getVisibleHand().size());
        assertEquals(
            dealer.getHand().get(0),
            dealer.getVisibleHand().get(0)
        );
    }

    @Test
    void dealerRevealsCard() {
        Deck deck = new Deck(List.of(
            card(Rank.TEN),
            card(Rank.FIVE)
        ));

        Dealer dealer = new Dealer("Dealer", deck);
        dealer.dealInitialCards();
        dealer.revealCard();

        assertFalse(dealer.isCardHidden());
        assertEquals(2, dealer.getVisibleHand().size());
        assertEquals(dealer.getHand(), dealer.getVisibleHand());
    }

    @Test
    void dealerCanHideCardAgain() {
        Dealer dealer = new Dealer("Dealer", new Deck());

        dealer.dealInitialCards();
        dealer.revealCard();
        dealer.hideCard();

        assertTrue(dealer.isCardHidden());
        assertEquals(1, dealer.getVisibleHand().size());
    }
}
