/**
 * Точка входа в консольную игру «Блэкджек».
 */
public class Main {
    /**
     * Запускает игру.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в Блэкджек!");
        System.out.println();

        Game game = new Game();
        game.play();
    }
}
