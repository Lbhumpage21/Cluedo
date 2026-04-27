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
        Player scarlett = new Player("Miss Scarlett", "S", 16, 0);
        Player mustard = new Player("Colonel Mustard", "M", 23, 7);
        Player white = new Player("Mrs White", "W", 14, 24);
        Player green = new Player("Reverend Green", "G", 9, 24);
        Player peacock = new Player("Mrs Peacock", "E", 0, 18);
        Player plum = new Player("Professor Plum", "P", 0, 5);

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

            if (index >= players.size()) {
                index = 0;
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
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

        if (targetTile == null) {
            return false;
        }


        if (targetTile.isDoor()) {
            System.out.println(currentPlayer.getName() + " has entered " + targetTile.getRoom().getName());

            board.clearHallwayTile(oldX, oldY);
            currentPlayer.setRoom(targetTile.getRoom());

            System.out.println(board.toString());

            spacesRemaining = 0;
            return true;
        }

        boolean moveAllowed = board.setPlayerOnHallway(targetX, targetY, currentPlayer);

        if (moveAllowed) {
            board.clearHallwayTile(oldX, oldY);
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

        if (currentRoom == null) {
            return false;
        }
        if (!currentRoom.hasSecretPassage()) {
            return false;
        }

        Room destination = currentRoom.getSecretPassage();
        currentPlayer.setRoom(destination);
        spacesRemaining = 0;
        System.out.println(board.toString());
        System.out.println(currentPlayer.getName() + " has used the secret passage to " + destination.getName());
        return true;
    }

    public void endTurn() {
        spacesRemaining = 0;

        if (eliminatedCount == players.size()) {
            System.out.println("All players have been eliminated");
            return;
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isEliminated() && eliminatedCount < players.size());
    }

    public Card makeSuggestion(Card suspect, Card weapon, Card room) {
        Player accuser = getCurrentPlayer();
        Room currentRoom = accuser.getCurrentRoom();

        if (accuser.isEliminated()) {
            System.out.println("You are eliminated and cannot make a suggestion");
            return null;
        }

        if (currentRoom == null) {
            System.out.println("You are not in a room");
            return null;
        }

        Player suspectPlayer = getPlayerByName(suspect.getName());

        if (suspectPlayer != null) {
            if (suspectPlayer.getCurrentRoom() == null) {
                board.clearHallwayTile(suspectPlayer.getX(), suspectPlayer.getY());
            }
            suspectPlayer.setRoom(currentRoom);

            System.out.println(suspect.getName() + " has been dragged to the " + currentRoom.getName());
        }

        int index = (currentPlayerIndex + 1) % players.size();

        while (index != currentPlayerIndex) {
            Player playerBeingChecked = players.get(index);

            Card revealedCard = playerBeingChecked.disproveSuggestion(suspect, weapon, room);
            if (revealedCard != null) {
                System.out.println(playerBeingChecked.getName() + " has revealed " + revealedCard.getName());
                spacesRemaining = 0;

                // Notify all AI players that a disproof occurred
                for (Player p : players) {
                    if (p instanceof AIPlayer && !p.getName().equals(accuser.getName())) {
                        ((AIPlayer) p).observeDisproof(
                                suspect.getName(), weapon.getName(), room.getName());
                    }
                }

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

        if (currentRoom == null) {
            System.out.println(accuser.getName() + " must be in a room to make an accusation");
            return false;
        }

        System.out.println(accuser.getName() + " has accused " + suspect.getName()
                + " of committing the murder with a " + weapon.getName()
                + " in the " + room.getName());

        Card winningSuspect = envelope.get(0);
        Card winningWeapon = envelope.get(1);
        Card winningRoom = envelope.get(2);

        if (winningSuspect.getName().equals(suspect.getName())
                && winningWeapon.getName().equals(weapon.getName())
                && winningRoom.getName().equals(room.getName())) {
            System.out.println(accuser.getName() + " has solved the murder and won the game");
            isGameOver = true;
            return true;
        }

        System.out.println("That accusation was wrong. " + accuser.getName() + " has been eliminated from the game");
        accuser.setEliminated();
        eliminatedCount += 1;
        spacesRemaining = 0;
        return false;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Testing helper: directly places the current player into a named room.
     */
    public boolean forceCurrentPlayerIntoRoom(String roomName) {
        Player currentPlayer = getCurrentPlayer();
        Room room = board.getRoom(roomName);

        if (room == null) {
            return false;
        }

        if (currentPlayer.getCurrentRoom() == null) {
            board.clearHallwayTile(currentPlayer.getX(), currentPlayer.getY());
        }

        currentPlayer.setRoom(room);
        spacesRemaining = 0;
        return true;
    }

    private Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    // Decides where a player goes when exiting a room
    public boolean leaveRoom() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.getCurrentRoom() == null) {return false;}

        int[] door = getDoorForRoom(currentPlayer.getCurrentRoom().getName());
        if (door == null) {return false;}
            boolean moveAllowed = board.setPlayerOnHallway(door[0], door[1], currentPlayer);
            if (moveAllowed) {
                currentPlayer.setRoom(null);
                currentPlayer.setCoordinates(door[0], door[1]);
                spacesRemaining--;
                return true;
            }
            return false;
    }

    // Replaces the human players with number of AI players specified
    public void setupAIPlayers  (int aiCount) {
        for (int i = players.size() - aiCount; i <players.size(); i++) {
            Player human = players.get(i);
            AIPlayer ai = new AIPlayer(human.getName(), human.getTokenChar(), human.getX(), human.getY());

            // Gives them the same cards
            for (Card card: human.getHand()){
                ai.receiveCard(card);
            }
            players.set(i, ai);

        }
    }


    private int[] getDoorForRoom(String roomName) {
        switch (roomName) {
            case "Study":         return new int[]{6, 3};
            case "Hall":          return new int[]{9, 4};
            case "Lounge":        return new int[]{17, 5};
            case "Library":       return new int[]{6, 8};
            case "Billiard Room": return new int[]{1, 12};
            case "Dining Room":   return new int[]{17, 9};
            case "Ballroom":      return new int[]{7, 17};
            case "Kitchen":       return new int[]{18, 18};
            case "Conservatory":  return new int[]{3, 19};
            default: return null;
        }
    }




    public Card getCardFromDatabase(String cardName) {
        return deck.getCard(cardName);
    }



}