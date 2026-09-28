import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный участник игры, имеющий имя, колоду и набор карт.
 */
public abstract class Actor {
    protected String name;
    protected Deck deck;
    protected List<Card> hand = new ArrayList<>();

    /**
     * Создает участника игры.
     *
     * @param name имя участника
     * @param deck колода, из которой участник получает карты
     */
    Actor(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
    }

    /**
     * Определяет, должен ли участник взять карту.
     *
     * @return true, если участник должен взять карту
     */
    abstract boolean shouldTake();

    /**
     * Раздает участнику две начальные карты.
     */
    public void dealInitialCards() {
        hand.add(deck.take());
        hand.add(deck.take());
    }

    /**
     * Пытается взять карту в соответствии с решением участника.
     *
     * @return взятая карта или null, если карта не была взята
     */
    public Card takeCard() {
        if (shouldTake()) {
            Card current = deck.take();

            if (current != null) {
                hand.add(current);
            }

            return current;
        }

        return null;
    }

    /**
     * Вычисляет сумму очков карт на руках.
     *
     * @return сумма очков комбинации
     */
    public int getScore() {
        int score = 0;
        int aces = 0;

        for (Card card : hand) {
            score += card.getRank();

            if (card.getRank() == 1) {
                aces++;
            }
        }

        score += aces * 10;

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    /**
     * Возвращает карты участника.
     *
     * @return список карт на руках
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Возвращает карты, доступные для показа.
     *
     * @return список видимых карт
     */
    public List<Card> getVisibleHand() {
        return hand;
    }

    /**
     * Возвращает имя участника.
     *
     * @return имя участника
     */
    public String getName() {
        return name;
    }

    /**
     * Проверяет, превышает ли сумма очков 21.
     *
     * @return true, если участник перебрал
     */
    public boolean isBusted() {
        return getScore() > 21;
    }

    /**
     * Очищает карты участника перед новым раундом.
     */
    public void resetHand() {
        hand.clear();
    }
}
