package cluedo.simulation;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        GameManager gm = new GameManager();

        // test driver

        System.out.println(gm.rollDiceForCurrentPlayer());
        gm.attemptMove(16,1);
        System.out.println(gm.attemptMove(16,2));
        System.out.println(gm.attemptMove(16,3));
        System.out.println(gm.attemptMove(16,4));
        System.out.println(gm.attemptMove(16,5));
        System.out.println(gm.attemptMove(17,5));

        gm.endTurn();
        gm.endTurn();
        gm.endTurn();
        gm.endTurn();
        gm.endTurn();
        gm.endTurn();
        System.out.println(gm.rollDiceForCurrentPlayer());
        System.out.println(gm.makeSuggestion(gm.getCardFromDatabase("Mr Green"),gm.getCardFromDatabase("Dagger"),gm.getCardFromDatabase("Lounge")));
        System.out.println(gm.makeAccusation(gm.getCardFromDatabase("Mr Green"), gm.getCardFromDatabase("Dagger"), gm.getCardFromDatabase("Lounge")));
        gm.endTurn();



    }
}