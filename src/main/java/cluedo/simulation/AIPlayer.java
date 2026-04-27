package cluedo.simulation;

import cluedo.simulation.board.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {

    private static final Random random = new Random();

    private static final String[] ALL_SUSPECTS = {
        "Miss Scarlett", "Colonel Mustard", "Mrs White",
        "Reverend Green", "Mrs Peacock", "Professor Plum"
    };

    private static final String[] ALL_WEAPONS = {
        "Dagger", "Candlestick", "Revolver",
        "Rope", "Lead Piping", "Spanner"
    };

    private static final String[] ALL_ROOMS = {
        "Study", "Hall", "Lounge", "Library",
        "Billiard Room", "Dining Room", "Conservatory", "Ballroom", "Kitchen"
    };

    // Cards confirmed NOT in the murder envelope
    private List<String> eliminatedCards;

    // Partial knowledge: each entry is a triple [suspect, weapon, room] where
    // we know some player holds at least one of these three cards
    private List<String[]> partialKnowledge;

    private boolean hasAccused;

    public AIPlayer(String name, String tokenChar, int startX, int startY) {
        super(name, tokenChar, startX, startY);
        this.eliminatedCards = new ArrayList<>();
        this.partialKnowledge = new ArrayList<>();
        this.hasAccused = false;
    }

    // -------------------------------------------------------------------------
    // Card knowledge / notebook
    // -------------------------------------------------------------------------

    @Override
    public void receiveCard(Card card) {
        super.receiveCard(card);
        eliminateCard(card.getName());
    }

    public void eliminateCard(String cardName) {
        if (cardName != null && !eliminatedCards.contains(cardName)) {
            eliminatedCards.add(cardName);
            System.out.println("[AI] Eliminated from suspicion: " + cardName);
            checkPartialKnowledge();
        }
    }

    // Called when a card is revealed to the AI directly (from AI's own suggestion)
    public void observeRevealedCard(Card revealedCard) {
        if (revealedCard != null) {
            eliminateCard(revealedCard.getName());
        }
        // null = nobody disproved, no card to learn about
    }

    // Called when AI observes another player disprove any suggestion (even between others)
    // We know the disproving player holds at least one of the three suggested cards
    public void observeDisproof(String suspect, String weapon, String room) {
        int unknownCount = 0;
        String lastUnknown = null;

        for (String card : new String[]{suspect, weapon, room}) {
            if (!eliminatedCards.contains(card)) {
                unknownCount++;
                lastUnknown = card;
            }
        }

        if (unknownCount == 1 && lastUnknown != null) {
            // Two of the three are already eliminated — the third must be the one shown
            System.out.println("[AI] Deduced from observation: " + lastUnknown);
            eliminateCard(lastUnknown);
        } else if (unknownCount > 1) {
            // Store for later — may become resolvable as more cards are eliminated
            partialKnowledge.add(new String[]{suspect, weapon, room});
        }
    }

    // Re-check stored partial knowledge after each new elimination
    private void checkPartialKnowledge() {
        List<String[]> resolved = new ArrayList<>();
        for (String[] triple : partialKnowledge) {
            int unknownCount = 0;
            String lastUnknown = null;
            for (String card : triple) {
                if (!eliminatedCards.contains(card)) {
                    unknownCount++;
                    lastUnknown = card;
                }
            }
            if (unknownCount == 1 && lastUnknown != null) {
                System.out.println("[AI] Deduced from partial knowledge: " + lastUnknown);
                eliminatedCards.add(lastUnknown);
                resolved.add(triple);
            } else if (unknownCount == 0) {
                resolved.add(triple);
            }
        }
        partialKnowledge.removeAll(resolved);
    }

    // -------------------------------------------------------------------------
    // Suggestion logic
    // -------------------------------------------------------------------------

    public Card chooseSuspect(GameManager gameManager) {
        List<String> unknown = getUnknownCards(ALL_SUSPECTS);
        String chosen = unknown.isEmpty()
                ? ALL_SUSPECTS[random.nextInt(ALL_SUSPECTS.length)]
                : unknown.get(random.nextInt(unknown.size()));
        System.out.println("[AI] Suggesting suspect: " + chosen);
        return gameManager.getCardFromDatabase(chosen);
    }

    public Card chooseWeapon(GameManager gameManager) {
        List<String> unknown = getUnknownCards(ALL_WEAPONS);
        String chosen = unknown.isEmpty()
                ? ALL_WEAPONS[random.nextInt(ALL_WEAPONS.length)]
                : unknown.get(random.nextInt(unknown.size()));
        System.out.println("[AI] Suggesting weapon: " + chosen);
        return gameManager.getCardFromDatabase(chosen);
    }

    // Room must always be the current room — fixed by game rules
    public Card chooseRoom(GameManager gameManager) {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) {
            System.out.println("[AI] Not in a room — cannot suggest");
            return null;
        }
        System.out.println("[AI] Suggesting room: " + currentRoom.getName());
        return gameManager.getCardFromDatabase(currentRoom.getName());
    }

    // -------------------------------------------------------------------------
    // Accusation logic
    // -------------------------------------------------------------------------

    public boolean shouldAccuse() {
        if (hasAccused) return false;
        return getUnknownCards(ALL_SUSPECTS).size() == 1
                && getUnknownCards(ALL_WEAPONS).size() == 1
                && getUnknownCards(ALL_ROOMS).size() == 1;
    }

    public Card[] buildAccusation(GameManager gameManager) {
        List<String> unknownSuspects = getUnknownCards(ALL_SUSPECTS);
        List<String> unknownWeapons  = getUnknownCards(ALL_WEAPONS);
        List<String> unknownRooms    = getUnknownCards(ALL_ROOMS);

        if (unknownSuspects.size() != 1 || unknownWeapons.size() != 1 || unknownRooms.size() != 1) {
            return null;
        }

        hasAccused = true;
        Card suspect = gameManager.getCardFromDatabase(unknownSuspects.get(0));
        Card weapon  = gameManager.getCardFromDatabase(unknownWeapons.get(0));
        Card room    = gameManager.getCardFromDatabase(unknownRooms.get(0));

        System.out.println("[AI] Making accusation: "
                + suspect.getName() + " | " + weapon.getName() + " | " + room.getName());
        return new Card[]{suspect, weapon, room};
    }

    // -------------------------------------------------------------------------
    // Movement — purposeful, biased towards board centre where rooms cluster
    // -------------------------------------------------------------------------

    public void performMove(GameManager gameManager) {
        // Use secret passage if destination room is still unknown (worth visiting)
        if (getCurrentRoom() != null && getCurrentRoom().hasSecretPassage()) {
            Room destination = getCurrentRoom().getSecretPassage();
            if (!eliminatedCards.contains(destination.getName())) {
                System.out.println("[AI] Using secret passage to unknown room: " + destination.getName());
                gameManager.useSecretPassage();
                return;
            }
        }

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int maxAttempts = 30;

        while (gameManager.getSpacesRemaining() > 0 && maxAttempts > 0) {
            int[] dir = chooseBiasedDirection(directions);
            int newX = getX() + dir[0];
            int newY = getY() + dir[1];

            if (newX < 0 || newX > 24 || newY < 0 || newY > 24) {
                maxAttempts--;
                continue;
            }

            boolean moved = gameManager.attemptMove(newX, newY);
            if (moved && getCurrentRoom() != null) {
                System.out.println("[AI] Entered room: " + getCurrentRoom().getName());
                return;
            }
            maxAttempts--;
        }
    }

    // 60% chance to move towards board centre (12,12) where rooms are clustered
    // 40% random — prevents AI getting stuck against walls
    private int[] chooseBiasedDirection(int[][] directions) {
        if (random.nextDouble() < 0.6) {
            int dx = Integer.compare(12, getX());
            int dy = Integer.compare(12, getY());
            if (dx != 0 && random.nextBoolean()) return new int[]{dx, 0};
            if (dy != 0) return new int[]{0, dy};
            if (dx != 0) return new int[]{dx, 0};
        }
        return directions[random.nextInt(directions.length)];
    }

    // -------------------------------------------------------------------------
    // Full turn
    // -------------------------------------------------------------------------

    public AITurnResult takeTurn(GameManager gameManager) {
        AITurnResult result = new AITurnResult();

        System.out.println("\n[AI] " + getName() + "'s turn begins");

        result.roll = gameManager.rollDiceForCurrentPlayer();
        System.out.println("[AI] Rolled: " + result.roll);

        performMove(gameManager);

        if (getCurrentRoom() != null) {
            result.roomEntered = getCurrentRoom().getName();

            Card suspect = chooseSuspect(gameManager);
            Card weapon  = chooseWeapon(gameManager);
            Card room    = chooseRoom(gameManager);

            if (suspect != null && weapon != null && room != null) {
                result.suspectSuggested = suspect.getName();
                result.weaponSuggested  = weapon.getName();
                result.roomSuggested    = room.getName();

                Card revealed = gameManager.makeSuggestion(suspect, weapon, room);
                observeRevealedCard(revealed);

                if (revealed == null) {
                    System.out.println("[AI] Nobody disproved — strong evidence for these cards");
                    result.revealedCard = null;
                }
                else{
                    result.revealedCard = revealed.getName();
                }
            }
        }

        if (shouldAccuse() && getCurrentRoom() != null) {
            Card[] accusation = buildAccusation(gameManager);
            if (accusation != null) {
                result.madeAccusation = true;
                result.accusedSuspect = accusation[0].getName();
                result.accusedWeapon  = accusation[1].getName();
                result.accusedRoom    = accusation[2].getName();
                result.wonGame = gameManager.makeAccusation(accusation[0], accusation[1], accusation[2]);
            }
        }

        gameManager.endTurn();
        System.out.println("[AI] " + getName() + "'s turn ends\n");
        return result;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<String> getUnknownCards(String[] allCards) {
        List<String> unknown = new ArrayList<>();
        for (String card : allCards) {
            if (!eliminatedCards.contains(card)) {
                unknown.add(card);
            }
        }
        return unknown;
    }

    public List<String> getEliminatedCards() {
        return eliminatedCards;
    }

    public boolean hasAccused() {
        return hasAccused;
    }

    public List<String[]> getPartialKnowledge() {
        return partialKnowledge;
    }

    // returned by taketurn(). Tells the ui what actions the ai takes on its turn
    public static class AITurnResult {
        public int roll;
        public String roomEntered;
        public String suspectSuggested;
        public String weaponSuggested;
        public String roomSuggested;
        public String revealedCard;
        public boolean madeAccusation;
        public boolean wonGame;
        public String accusedSuspect;
        public String accusedWeapon;
        public String accusedRoom;
    }
}
