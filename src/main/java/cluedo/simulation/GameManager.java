package cluedo.simulation;

import cluedo.simulation.board.Board;
import cluedo.simulation.board.Room;
import cluedo.simulation.board.RoomTile;
import cluedo.simulation.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private Board board;
    private Deck deck;
    private List<Player> players;

    private int currentPlayerIndex;
    private int spacesRemaining;
    private boolean isGameOver;
    public int eliminatedCount;

    public GameManager() {
        this.board = new Board();
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isGameOver = false;
        this.eliminatedCount = 0;
        setupPlayers();
        dealCards();
    }
    private void setupPlayers() {
        Player scarlett = new Player("Miss Scarlett","S", 16, 0);
        Player mustard = new Player("Colonel Mustard","M", 23, 7);
        Player white = new Player("Mrs White","W", 14, 24);
        Player green = new Player("Reverend Green","G", 9, 24);
        Player peacock = new Player("Mrs Peacock","E", 0, 18);
        Player plum = new Player("Professor Plum","P", 0, 5);

        players.add(scarlett);
        players.add(mustard);
        players.add(white);
        players.add(green);
        players.add(peacock);
        players.add(plum);

        for (Player p : players) {
            board.setPlayerOnHallway(p.getX(), p.getY(), p);
        }
        System.out.println(board.toString());
    }

    private void dealCards() {
        List<Card> mainDeck = deck.getMainDeck();
        int index = 0;

        for (Card card : mainDeck) {
            players.get(index).receiveCard(card);
            index++;

            // loops back to first player until all cards are dealt
            if (index >= players.size()) {
                index = 0;
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int rollDiceForCurrentPlayer() {

        if (getCurrentPlayer().isEliminated()) {
            System.out.println("You are eliminated and cannot roll the dice");
            return 0;
        }
        int roll = Dice.roll();
        this.spacesRemaining = roll;
        return roll;
    }

    public int getSpacesRemaining() {
        return spacesRemaining;
    }

    public boolean attemptMove(int targetX, int targetY) {


        if (spacesRemaining <= 0) {
            return false;
        }

        Player currentPlayer = getCurrentPlayer();

        if (currentPlayer.isEliminated()) {
            System.out.println("You are eliminated and cannot move");
            return false;
        }

        int oldX = currentPlayer.getX();
        int oldY = currentPlayer.getY();

        Tile targetTile = board.getTile(targetX, targetY);
        Tile currentTile = board.getTile(oldX, oldY);

        //finds if new space is right next to old one
        int distanceX = Math.abs(targetX - oldX);
        int distanceY = Math.abs(targetY - oldY);
        boolean isAdjacent = (distanceX + distanceY) == 1;

        if (!isAdjacent) {
            //if player is in a room, and target tile is a door,
            if (currentPlayer.getCurrentRoom() != null && targetTile.isDoor()) {
                RoomTile targetDoor = (RoomTile) targetTile;

                System.out.println("Target Door Object: " + targetDoor.getRoom());

                if (currentPlayer.getCurrentRoom().getName().equals(targetDoor.getRoom().getName())) {
                    currentPlayer.setRoom(null);
                    currentPlayer.setCoordinates(targetX, targetY);
                    return true;
                }
                return false;
            }
            return false;
        }

        //teleports player to room if the tile is a door
        if (targetTile.isDoor()) {
            System.out.println(currentPlayer.getName() + " has entered " + targetTile.getRoom().getName());

            board.clearHallwayTile(oldX,oldY);
            currentPlayer.setRoom(targetTile.getRoom());

            System.out.println(board.toString());

            spacesRemaining = 0;
            return true;
        }



        boolean moveAllowed = board.setPlayerOnHallway(targetX, targetY, currentPlayer);

        if (moveAllowed){
            board.clearHallwayTile(oldX,oldY);
            currentPlayer.setCoordinates(targetX, targetY);

            spacesRemaining--;

            System.out.println(board.toString());
            return true;
        }
        return false;
    }

    public boolean useSecretPassage() {
        Player currentPlayer = getCurrentPlayer();
        Room currentRoom = currentPlayer.getCurrentRoom();

        if (currentPlayer.isEliminated()) {
            System.out.println("You are eliminated and cannot move");
            return false;
        }

        if (currentRoom == null) {return false;}
        if (!currentRoom.hasSecretPassage()) {return false;}

        Room destination = currentRoom.getSecretPassage();
        currentPlayer.setRoom(destination);
        spacesRemaining = 0;
        System.out.println(board.toString());
        System.out.println(currentPlayer.getName() + " has used the secret passage to " + destination.getName());
        return true;

    }

    public void endTurn() {
        currentPlayerIndex++;
        spacesRemaining = 0;
        if (eliminatedCount == players.size()){
            System.out.println("All players have been eliminated");
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

    }

    public Card makeSuggestion(Card suspect, Card weapon, Card room) {

        Player accuser = getCurrentPlayer();
        Room currentRoom = accuser.getCurrentRoom();

        if (accuser.isEliminated()) {
            System.out.println("You are eliminated and cannot make a suggestion");
            return null;
        }

        //checks if player is in a room
        if (currentRoom == null) {
            System.out.println("You are not in a room");
            return null;
        }

        Player suspectPlayer = getPlayerByName(suspect.getName());
        //transports suspected player to the room
        if (suspectPlayer != null) {
            board.clearHallwayTile(suspectPlayer.getX(), suspectPlayer.getY());
            suspectPlayer.setRoom(currentRoom);
            suspectPlayer.setCoordinates(suspectPlayer.getX(), suspectPlayer.getY());

            System.out.println(suspect.getName() + " has been dragged to the " + currentRoom.getName());

        }

        //loops through every player, until one has a matching card
        int index = (currentPlayerIndex + 1) % players.size();

        while (index != currentPlayerIndex) {
            Player playerBeingChecked = players.get(index);

            Card revealedCard = playerBeingChecked.disproveSuggestion(suspect, weapon, room);
            if (revealedCard != null) {
                System.out.println(playerBeingChecked.getName() + " has revealed " + revealedCard.getName());
                spacesRemaining = 0;
                return revealedCard;
            }
            System.out.println(playerBeingChecked.getName() + " has not revealed a card");
            index = (index + 1) % players.size();
        }
        System.out.println("Nobody could disprove the suggestion");
        spacesRemaining = 0;
        return null;
    }

    public boolean makeAccusation(Card suspect, Card weapon, Card room) {
        Player accuser = getCurrentPlayer();
        List<Card> envelope = deck.getEnvelope();
        Room currentRoom = accuser.getCurrentRoom();

        if (accuser.isEliminated()) {
            System.out.println("You are eliminated and cannot make an accusation");
            return false;
        }

        if (currentRoom == null){
            System.out.println(accuser.getName() + " must be in a room to make an accusation");
            return false;
        }

        System.out.println(accuser.getName() + " has accused " + suspect.getName() + " of committing the murder with a " + weapon.getName() + " in the " + room.getName());

        Card winningSuspect = envelope.get(0);
        Card winningWeapon = envelope.get(1);
        Card winningRoom = envelope. get(2);

        if (winningSuspect.getName() == suspect.getName() && winningWeapon.getName() == weapon.getName() && winningRoom.getName() == room.getName()){
            System.out.println(accuser + " has solved the murder and won the game");
            return true;
        }
        System.out.println("That accusation was wrong " + accuser.getName() + " has been eliminated from the game");
        accuser.setEliminated();
        eliminatedCount += 1;
        spacesRemaining = 0;
        return false;
    }

    //helper method to find player object from name
    private Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {return p;}
        }
        return null;
    }

    public Card getCardFromDatabase(String cardName) {
        return deck.getCard(cardName);
    }
}