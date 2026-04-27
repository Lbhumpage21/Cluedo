package cluedo.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AIPlayerTest {

    private AIPlayer aiPlayer;

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

    @BeforeEach
    void setUp() {
        aiPlayer = new AIPlayer("Professor Plum", "P", 0, 5);
    }

    // =========================================================================
    // Card elimination tests
    // =========================================================================

    @Test
    void testInitialStateHasNoEliminatedCards() {
        assertTrue(aiPlayer.getEliminatedCards().isEmpty(),
                "AI should start with an empty eliminated list");
    }

    @Test
    void testReceiveCardEliminatesItFromSuspicion() {
        Card card = new Card("Dagger", Card.TYPE_WEAPON);
        aiPlayer.receiveCard(card);
        assertTrue(aiPlayer.getEliminatedCards().contains("Dagger"),
                "Received card should be immediately eliminated from suspicion");
    }

    @Test
    void testReceiveMultipleCardsEliminatesAll() {
        aiPlayer.receiveCard(new Card("Miss Scarlett", Card.TYPE_SUSPECT));
        aiPlayer.receiveCard(new Card("Rope", Card.TYPE_WEAPON));
        aiPlayer.receiveCard(new Card("Kitchen", Card.TYPE_ROOM));

        assertTrue(aiPlayer.getEliminatedCards().contains("Miss Scarlett"));
        assertTrue(aiPlayer.getEliminatedCards().contains("Rope"));
        assertTrue(aiPlayer.getEliminatedCards().contains("Kitchen"));
        assertEquals(3, aiPlayer.getEliminatedCards().size());
    }

    @Test
    void testEliminateDuplicateCardNoDuplicates() {
        aiPlayer.eliminateCard("Candlestick");
        aiPlayer.eliminateCard("Candlestick");
        assertEquals(1, aiPlayer.getEliminatedCards().size(),
                "Eliminating a card twice should not add duplicates");
    }

    // =========================================================================
    // Revealed card observation tests
    // =========================================================================

    @Test
    void testObserveRevealedCardEliminatesIt() {
        Card revealed = new Card("Revolver", Card.TYPE_WEAPON);
        aiPlayer.observeRevealedCard(revealed);
        assertTrue(aiPlayer.getEliminatedCards().contains("Revolver"),
                "Observed revealed card should be eliminated");
    }

    @Test
    void testObserveNullRevealedCardDoesNothing() {
        aiPlayer.observeRevealedCard(null);
        assertTrue(aiPlayer.getEliminatedCards().isEmpty(),
                "Null revealed card should not affect eliminated list");
    }

    // =========================================================================
    // Inter-player inference (observeDisproof) tests
    // =========================================================================

    @Test
    void testObserveDisproofWithTwoKnownEliminatesThird() {
        // If two of the three suggested cards are already eliminated,
        // the AI can deduce the third must be the one shown
        aiPlayer.eliminateCard("Miss Scarlett");
        aiPlayer.eliminateCard("Dagger");
        // "Kitchen" is the only unknown in this triple
        aiPlayer.observeDisproof("Miss Scarlett", "Dagger", "Kitchen");

        assertTrue(aiPlayer.getEliminatedCards().contains("Kitchen"),
                "AI should deduce and eliminate the only unknown card in the triple");
    }

    @Test
    void testObserveDisproofStoresPartialKnowledgeWhenMultipleUnknown() {
        // All three cards are unknown — store as partial knowledge
        aiPlayer.observeDisproof("Colonel Mustard", "Rope", "Study");

        assertEquals(1, aiPlayer.getPartialKnowledge().size(),
                "Unresolved triple should be stored as partial knowledge");
    }

    @Test
    void testPartialKnowledgeResolvedAfterLaterElimination() {
        // Store partial knowledge with two unknowns
        aiPlayer.observeDisproof("Colonel Mustard", "Rope", "Study");
        assertEquals(1, aiPlayer.getPartialKnowledge().size());

        // Later eliminate one of them
        aiPlayer.eliminateCard("Colonel Mustard");

        // Now only "Rope" and "Study" remain unknown in that triple
        // Still two unknowns — partial knowledge remains
        assertEquals(1, aiPlayer.getPartialKnowledge().size());

        // Eliminate another
        aiPlayer.eliminateCard("Rope");

        // Now only "Study" unknown — AI should deduce it automatically
        assertTrue(aiPlayer.getEliminatedCards().contains("Study"),
                "AI should resolve partial knowledge and eliminate Study");
        assertEquals(0, aiPlayer.getPartialKnowledge().size(),
                "Resolved triple should be removed from partial knowledge");
    }

    // =========================================================================
    // Accusation readiness tests
    // =========================================================================

    @Test
    void testShouldNotAccuseWhenManyCardsUnknown() {
        assertFalse(aiPlayer.shouldAccuse(),
                "AI should not accuse when no elimination has occurred");
    }

    @Test
    void testShouldAccuseWhenOneOfEachCategoryRemains() {
        aiPlayer.eliminateCard("Miss Scarlett");
        aiPlayer.eliminateCard("Colonel Mustard");
        aiPlayer.eliminateCard("Mrs White");
        aiPlayer.eliminateCard("Reverend Green");
        aiPlayer.eliminateCard("Mrs Peacock");
        // "Professor Plum" remains

        aiPlayer.eliminateCard("Dagger");
        aiPlayer.eliminateCard("Candlestick");
        aiPlayer.eliminateCard("Revolver");
        aiPlayer.eliminateCard("Rope");
        aiPlayer.eliminateCard("Lead Piping");
        // "Spanner" remains

        aiPlayer.eliminateCard("Study");
        aiPlayer.eliminateCard("Hall");
        aiPlayer.eliminateCard("Lounge");
        aiPlayer.eliminateCard("Library");
        aiPlayer.eliminateCard("Billiard Room");
        aiPlayer.eliminateCard("Dining Room");
        aiPlayer.eliminateCard("Conservatory");
        aiPlayer.eliminateCard("Ballroom");
        // "Kitchen" remains

        assertTrue(aiPlayer.shouldAccuse(),
                "AI should accuse when exactly one of each category remains");
    }

    @Test
    void testShouldNotAccuseWhenTwoSuspectsRemain() {
        aiPlayer.eliminateCard("Miss Scarlett");
        aiPlayer.eliminateCard("Colonel Mustard");
        aiPlayer.eliminateCard("Mrs White");
        aiPlayer.eliminateCard("Reverend Green");
        // Two suspects remain

        aiPlayer.eliminateCard("Dagger");
        aiPlayer.eliminateCard("Candlestick");
        aiPlayer.eliminateCard("Revolver");
        aiPlayer.eliminateCard("Rope");
        aiPlayer.eliminateCard("Lead Piping");

        aiPlayer.eliminateCard("Study");
        aiPlayer.eliminateCard("Hall");
        aiPlayer.eliminateCard("Lounge");
        aiPlayer.eliminateCard("Library");
        aiPlayer.eliminateCard("Billiard Room");
        aiPlayer.eliminateCard("Dining Room");
        aiPlayer.eliminateCard("Conservatory");
        aiPlayer.eliminateCard("Ballroom");

        assertFalse(aiPlayer.shouldAccuse(),
                "AI should not accuse when two suspects remain");
    }

    @Test
    void testHasAccusedStartsFalse() {
        assertFalse(aiPlayer.hasAccused(),
                "AI should not have accused at the start");
    }

    // =========================================================================
    // disproveSuggestion tests (inherited from Player)
    // =========================================================================

    @Test
    void testAIDisprovesSuggestionWhenItHasCard() {
        Card dagger = new Card("Dagger", Card.TYPE_WEAPON);
        aiPlayer.receiveCard(dagger);

        Card suspect = new Card("Miss Scarlett", Card.TYPE_SUSPECT);
        Card weapon  = new Card("Dagger", Card.TYPE_WEAPON);
        Card room    = new Card("Kitchen", Card.TYPE_ROOM);

        Card revealed = aiPlayer.disproveSuggestion(suspect, weapon, room);
        assertNotNull(revealed, "AI should reveal a card it holds");
        assertEquals("Dagger", revealed.getName());
    }

    @Test
    void testAICannotDisproveWhenItHasNoMatchingCard() {
        Card suspect = new Card("Miss Scarlett", Card.TYPE_SUSPECT);
        Card weapon  = new Card("Dagger", Card.TYPE_WEAPON);
        Card room    = new Card("Kitchen", Card.TYPE_ROOM);

        Card revealed = aiPlayer.disproveSuggestion(suspect, weapon, room);
        assertNull(revealed, "AI should return null when it cannot disprove");
    }

    // =========================================================================
    // Boundary tests
    // =========================================================================

    @Test
    void testEliminateAllCardsNoCrash() {
        for (String s : ALL_SUSPECTS) aiPlayer.eliminateCard(s);
        for (String w : ALL_WEAPONS)  aiPlayer.eliminateCard(w);
        for (String r : ALL_ROOMS)    aiPlayer.eliminateCard(r);

        assertEquals(21, aiPlayer.getEliminatedCards().size(),
                "All 21 unique cards should be eliminated without duplicates");
    }

    @Test
    void testAccusationTriggersAfterEliminating20Cards() {
        // Eliminate all but one in each category (20 total eliminated)
        aiPlayer.eliminateCard("Miss Scarlett");
        aiPlayer.eliminateCard("Colonel Mustard");
        aiPlayer.eliminateCard("Mrs White");
        aiPlayer.eliminateCard("Reverend Green");
        aiPlayer.eliminateCard("Mrs Peacock");

        aiPlayer.eliminateCard("Dagger");
        aiPlayer.eliminateCard("Candlestick");
        aiPlayer.eliminateCard("Revolver");
        aiPlayer.eliminateCard("Rope");
        aiPlayer.eliminateCard("Lead Piping");

        aiPlayer.eliminateCard("Study");
        aiPlayer.eliminateCard("Hall");
        aiPlayer.eliminateCard("Lounge");
        aiPlayer.eliminateCard("Library");
        aiPlayer.eliminateCard("Billiard Room");
        aiPlayer.eliminateCard("Dining Room");
        aiPlayer.eliminateCard("Conservatory");
        aiPlayer.eliminateCard("Ballroom");

        assertEquals(18, aiPlayer.getEliminatedCards().size(),
                "Should have 18 cards eliminated");
        assertTrue(aiPlayer.shouldAccuse(),
                "AI should be ready to accuse after eliminating 18 cards (one per category remains)");
    }
}
