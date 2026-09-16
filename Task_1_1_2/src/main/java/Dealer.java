
/**
 * Представляет дилера, который автоматически принимает решения о взятии карт.
 */
public class Dealer extends Actor {
    private boolean hiddenCard = true;

    /**
     * Создает дилера.
     *
     * @param name имя дилера
     * @param deck колода, из которой дилер получает карты
     */
    Dealer(String name, Deck deck) {
        super(name, deck);
    }

    /**
     * Определяет, должен ли дилер взять карту.
     *
     * @return true, если сумма очков дилера меньше 17
     */
    @Override
    boolean shouldTake() {
        return getScore() < 17;
    }

    /**
     * Скрывает вторую карту дилера.
     */
    public void hideCard() {
        hiddenCard = true;
    }

    /**
     * Открывает закрытую карту дилера.
     */
    public void revealCard() {
        hiddenCard = false;
    }

    /**
     * Проверяет, скрыта ли карта дилера.
     *
     * @return true, если карта скрыта
     */
    public boolean isCardHidden() {
        return hiddenCard;
    }

    /**
     * Возвращает карты дилера, доступные для просмотра.
     *
     * @return список видимых карт дилера
     */
    @Override
    public java.util.List<Card> getVisibleHand() {
        if (!hiddenCard) {
            return hand;
        }

        return java.util.Collections.singletonList(hand.get(0));
    }
}