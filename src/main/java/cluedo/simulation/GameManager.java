package cluedo.simulation;

import cluedo.simulation.board.Board;
import cluedo.simulation.board.Room;
import cluedo.simulation.board.RoomTile;
import cluedo.simulation.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Game manager for cluedo
 * Manages the board, players, deck, and turn order
 * All game actions are handled here
 */
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
            return 0;
        }

        int roll = Dice.roll();
        this.spacesRemaining = roll;
        return roll;
    }

    public int getSpacesRemaining() {
        return spacesRemaining;
    }

    /**
     * Attempts to move the current player to the given coordinates
     * The target must be adjacent to the player's current position
     * Entering a door tile moves the player into that room and ends movement
     *
     * @param targetX target column
     * @param targetY target row
     * @return true if the move was successful
     */
    public boolean attemptMove(int targetX, int targetY) {
        if (spacesRemaining <= 0) {
            return false;
        }

        Player currentPlayer = getCurrentPlayer();

        if (currentPlayer.isEliminated()) {
            return false;
        }

        int oldX = currentPlayer.getX();
        int oldY = currentPlayer.getY();

        Tile targetTile = board.getTile(targetX, targetY);

        if (targetTile == null) {
            return false;
        }


        if (targetTile.isDoor()) {

            board.clearHallwayTile(oldX, oldY);
            currentPlayer.setRoom(targetTile.getRoom());


            spacesRemaining = 0;
            return true;
        }

        boolean moveAllowed = board.setPlayerOnHallway(targetX, targetY, currentPlayer);

        if (moveAllowed) {
            board.clearHallwayTile(oldX, oldY);
            currentPlayer.setCoordinates(targetX, targetY);

            spacesRemaining--;

            return true;
        }
        return false;
    }


    public boolean useSecretPassage() {
        Player currentPlayer = getCurrentPlayer();
        Room currentRoom = currentPlayer.getCurrentRoom();

        if (currentPlayer.isEliminated()) {
            return false;
        }

        if(spacesRemaining == 0) {
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
        return true;
    }

    public void endTurn() {
        spacesRemaining = 0;

        if (eliminatedCount == players.size()) {
            return;
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isEliminated() && eliminatedCount < players.size());
    }

    /**
     * Makes a suggestion from the current player
     * The suspected player is moved to the current room
     * Other players hands are checked for a matching card
     * AI players are notified of the disproof
     *
     * @param suspect the suspected character card
     * @param weapon the suspected weapon card
     * @param room the suspected room card
     * @return the card revealed to disprove the suggestion
     */
    public Card makeSuggestion(Card suspect, Card weapon, Card room) {
        Player accuser = getCurrentPlayer();
        Room currentRoom = accuser.getCurrentRoom();

        if (accuser.isEliminated()) {
            return null;
        }

        if (currentRoom == null) {
            return null;
        }

        Player suspectPlayer = getPlayerByName(suspect.getName());

        if (suspectPlayer != null) {
            if (suspectPlayer.getCurrentRoom() == null) {
                board.clearHallwayTile(suspectPlayer.getX(), suspectPlayer.getY());
            }
            suspectPlayer.setRoom(currentRoom);
        }

        int index = (currentPlayerIndex + 1) % players.size();

        while (index != currentPlayerIndex) {
            Player playerBeingChecked = players.get(index);

            Card revealedCard = playerBeingChecked.disproveSuggestion(suspect, weapon, room);
            if (revealedCard != null) {
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
            index = (index + 1) % players.size();
        }

        spacesRemaining = 0;
        return null;
    }

    /**
     * Makes an accusation from the current player
     * Checks the envelope against the accusation
     * If wrong, eliminates the player
     *
     * @param suspect the suspected character card
     * @param weapon the suspected weapon card
     * @param room the suspected room card
     * @return whether the accusation was true or not
     */
    public boolean makeAccusation(Card suspect, Card weapon, Card room) {
        Player accuser = getCurrentPlayer();
        List<Card> envelope = deck.getEnvelope();
        Room currentRoom = accuser.getCurrentRoom();

        if (accuser.isEliminated()) {
            return false;
        }

        if (currentRoom == null) {
            return false;
        }

        Card winningSuspect = envelope.get(0);
        Card winningWeapon = envelope.get(1);
        Card winningRoom = envelope.get(2);

        if (winningSuspect.getName().equals(suspect.getName())
                && winningWeapon.getName().equals(weapon.getName())
                && winningRoom.getName().equals(room.getName())) {
            isGameOver = true;
            return true;
        }

        accuser.setEliminated();
        eliminatedCount += 1;
        spacesRemaining = 0;
        return false;
    }

    public boolean isGameOver() {
        return isGameOver;
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

    private Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
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