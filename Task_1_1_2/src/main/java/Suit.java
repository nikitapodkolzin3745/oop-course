/**
 * Представляет масть игральной карты.
 */
public enum Suit {
    CLUBS("Трефы"),
    SPADES("Пики"),
    HEARTS("Червы"),
    DIAMONDS("Бубны");

    private final String name;

    Suit(String name) {
        this.name = name;
    }

    /**
     * Возвращает название масти.
     *
     * @return название масти
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
