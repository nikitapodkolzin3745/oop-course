
 /**
  * Управляет ходом игры в блэкджек, раундами и счетом участников.
  */
public class Game {
    private Deck deck = new Deck();

    private Player player = new Player("Игрок", deck);
    private Dealer dealer = new Dealer("Дилер", deck);

    private int playerWins = 0;
    private int dealerWins = 0;

    /**
     * Проводит один раунд игры.
     *
     * @param round номер текущего раунда
     */
    public void turn(int round) {
        player.resetHand();
        dealer.resetHand();
        dealer.hideCard();

        player.dealInitialCards();
        dealer.dealInitialCards();

        System.out.println("Раунд " + round);
        System.out.println("Дилер раздал карты");
        printHands();

        if (player.getScore() == 21) {
            System.out.println("У вас блэкджек!");
            playerWins++;
            printResult();
            return;
        }

        if (dealer.getScore() == 21) {
            System.out.println("У дилера блэкджек!");
            dealerWins++;
            printResult();
            return;
        }

        System.out.println();
        System.out.println("Ваш ход");
        System.out.println("-------");

        while (true) {
            Card card = player.takeACard();

            if (card == null) {
                break;
            }

            System.out.println(
                "Вы открыли карту " + card
            );

            printHands();

            if (player.isBusted()) {
                System.out.println("Вы проиграли раунд!");
                dealerWins++;
                printResult();
                return;
            }

            if (player.getScore() == 21) {
                break;
            }
        }

        System.out.println();
        System.out.println("Ход дилера");
        System.out.println("-------");

        dealer.revealCard();

        System.out.println(
            "Дилер открывает закрытую карту "
            + dealer.getHand().get(1)
        );

        printHands();

        while (dealer.getScore() < 17) {
            Card card = dealer.takeACard();

            System.out.println();
            System.out.println(
                "Дилер открывает карту " + card
            );

            printHands();

            if (dealer.isBusted()) {
                break;
            }
        }

        decideWinner();
    }

    /**
     * Выводит в консоль карты и очки игрока и дилера.
     */
    private void printHands() {
        System.out.println(
            "    Ваши карты: "
            + player.getHand()
            + " => " + player.getScore()
        );

        if (dealer.isCardHidden()) {
            System.out.println(
                "    Карты дилера: "
                + dealer.getVisibleHand()
                + ", <закрытая карта>"
            );
        } else {
            System.out.println(
                "    Карты дилера: "
                + dealer.getHand()
                + " => " + dealer.getScore()
            );
        }
    }

    /**
     * Определяет победителя раунда и обновляет счет.
     */
    private void decideWinner() {
        if (player.isBusted()) {
            System.out.println("Вы проиграли раунд!");
            dealerWins++;
        } else if (dealer.isBusted()) {
            System.out.println("Вы выиграли раунд!");
            playerWins++;
        } else if (player.getScore() > dealer.getScore()) {
            System.out.println("Вы выиграли раунд!");
            playerWins++;
        } else if (player.getScore() < dealer.getScore()) {
            System.out.println("Вы проиграли раунд!");
            dealerWins++;
        } else {
            System.out.println("Ничья!");
        }

        printResult();
    }

    /**
     * Выводит текущий счет игры.
     */
    private void printResult() {
        System.out.println(
            "Счет " + playerWins + ":" + dealerWins
            + " в вашу пользу."
        );

        System.out.println();
    }
}