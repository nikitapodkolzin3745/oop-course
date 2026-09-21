import java.util.Scanner;

/**
 * Представляет игрока, который вводит решения через консоль.
 */
public class Player extends Actor {
    private final Scanner scanner;

    /**
     * Создает игрока.
     *
     * @param name имя игрока
     * @param deck колода, из которой игрок получает карты
     * @param scanner источник пользовательского ввода
     */
    Player(String name, Deck deck, Scanner scanner) {
        super(name, deck);
        this.scanner = scanner;
    }

    /**
     * Запрашивает у игрока решение о взятии карты.
     *
     * @return true, если игрок хочет взять карту
     */
    @Override
    boolean shouldTake() {
        while (true) {
            System.out.println(
                "Введите “1”, чтобы взять карту, "
                + "и “0”, чтобы остановиться..."
            );

            String answer = scanner.nextLine();

            if (answer.equals("1")) {
                return true;
            }

            if (answer.equals("0")) {
                return false;
            }

            System.out.println("Введите 1 или 0.");
        }
    }
}
