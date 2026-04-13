package cluedo.simulation;

import cluedo.simulation.board.Room;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private String tokenChar; // How they are displayed in the console map

    private List<Card> hand;

    private int x;
    private int y;
    private Room currentRoom;

    private boolean isEliminated;


    public Player(String name,String tokenChar, int startX, int startY) {
        this.name = name;
        this.tokenChar = tokenChar;
        this.x = startX;
        this.y = startY;
        this.currentRoom = null;
        this.isEliminated = false;
        this.hand = new ArrayList<>();
    }
    public String getName() {return name;}
    public String getTokenChar() {return tokenChar;}
    public boolean isEliminated() {return isEliminated;}
    public Room getCurrentRoom() {return currentRoom;}

    public int getX() {return x;}
    public int getY() {return y;}

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setRoom(Room room) {
        this.currentRoom = room;
        // Coordinates set to -1 because they technically are no longer on a position on the board, but in a room
        if (room !=null){
            this.x = -1;
            this.y = -1;
        }
    }

    public void setEliminated() {
        this.isEliminated = true;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card disproveSuggestion(Card guessedSuspect, Card guessedWeapon, Card guessedRoom) {
        for (Card card : hand) {
            if (card.equals(guessedSuspect) || card.equals(guessedWeapon) || card.equals(guessedRoom)) {
                return card;
            }
        }
        return null;
    }

}
