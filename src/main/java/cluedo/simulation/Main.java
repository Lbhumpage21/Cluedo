package cluedo.simulation;

import cluedo.simulation.board.Board;

public class Main {
    public static void main(String[] args) {
        //Test driver
        System.out.println("Initialising Cluedo");

        Board board = new Board();

        Player scarlett = new Player("Miss Scarlett","S", 16, 0);
        board.setPlayerOnHallway(scarlett.getX(), scarlett.getY(), scarlett);

        Player mustard = new Player("Colonel Mustard","M", 23, 7);
        board.setPlayerOnHallway(mustard.getX(), mustard.getY(), mustard);

        Player white = new Player("Mrs White","W", 14, 24);
        board.setPlayerOnHallway(white.getX(), white.getY(), white);

        Player green = new Player("Reverend Green","G", 9, 24);
        board.setPlayerOnHallway(green.getX(), green.getY(), green);

        Player peacock = new Player("Mrs Peacock","E", 0, 18);
        board.setPlayerOnHallway(peacock.getX(), peacock.getY(), peacock);

        Player plum = new Player("Professor Plum","P", 0, 5);
        board.setPlayerOnHallway(plum.getX(), plum.getY(), plum);

        System.out.println(board.toString());
    }
}