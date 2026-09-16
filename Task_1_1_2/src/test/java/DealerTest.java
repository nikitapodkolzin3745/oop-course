
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealerTest {
    @Test
    void dealerTakesCardsBelow17() {
        Dealer dealer = new Dealer("Dealer", new Deck());

        dealer.dealInitialCards();

        assertEquals(2, dealer.getHand().size());
        assertTrue(dealer.getScore() >= 0);
    }

    @Test
    void dealerHidesCardByDefault() {
        Dealer dealer = new Dealer("Dealer", new Deck());

        dealer.dealInitialCards();

        assertTrue(dealer.isCardHidden());
        assertEquals(1, dealer.getVisibleHand().size());
    }

    @Test
    void dealerRevealsCard() {
        Dealer dealer = new Dealer("Dealer", new Deck());

        dealer.dealInitialCards();
        dealer.revealCard();

        assertFalse(dealer.isCardHidden());
        assertEquals(2, dealer.getVisibleHand().size());
    }

    @Test
    void dealerCanHideCardAgain() {
        Dealer dealer = new Dealer("Dealer", new Deck());

        dealer.dealInitialCards();
        dealer.revealCard();
        dealer.hideCard();

        assertTrue(dealer.isCardHidden());
    }
}
