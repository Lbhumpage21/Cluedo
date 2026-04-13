package cluedo.simulation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck {
    private List<Card> mainDeck;
    private Map<String, Card> cardDatabase;


    //Solution envelope
    private List<Card> envelope;

    public Deck() {
        this.mainDeck = new ArrayList<>();
        this.envelope = new ArrayList<>();
        this.cardDatabase = new HashMap<>();

        List<Card> suspects = generateSuspects();
        List<Card> weapons = generateWeapons();
        List<Card> rooms = generateRooms();

        registerCardsToDatabase(suspects,weapons,rooms);

        createEnvelope(suspects, weapons, rooms);
        assembleMainDeck(suspects, weapons, rooms);
    }

    //creates a database with card objects and their names
    private void registerCardsToDatabase(List<Card> suspects, List<Card> weapons, List<Card> rooms) {
        for(Card suspect : suspects) cardDatabase.put(suspect.getName(), suspect);
        for(Card weapon : weapons) cardDatabase.put(weapon.getName(), weapon);
        for(Card room : rooms) cardDatabase.put(room.getName(), room);

    }

    public Card getCard(String cardName) {
        return cardDatabase.get(cardName);
    }

    private List<Card> generateSuspects() {
        List<Card> list = new ArrayList<>();
        list.add(new Card("Miss Scarlett", Card.TYPE_SUSPECT));
        list.add(new Card("Colonel Mustard", Card.TYPE_SUSPECT));
        list.add(new Card("Mrs White", Card.TYPE_SUSPECT));
        list.add(new Card("Mr Green", Card.TYPE_SUSPECT));
        list.add(new Card("Mrs Peacock", Card.TYPE_SUSPECT));
        list.add(new Card("Professor Plum", Card.TYPE_SUSPECT));
        return list;
    }

    private List<Card> generateWeapons() {
        List<Card> list = new ArrayList<>();
        list.add(new Card("Candlestick", Card.TYPE_WEAPON));
        list.add(new Card("Dagger", Card.TYPE_WEAPON));
        list.add(new Card("Revolver", Card.TYPE_WEAPON));
        list.add(new Card("Rope", Card.TYPE_WEAPON));
        list.add(new Card("Lead piping", Card.TYPE_WEAPON));
        list.add(new Card("Spanner", Card.TYPE_WEAPON));
        return list;
    }

    private List<Card> generateRooms() {
        List<Card> list = new ArrayList<>();
        list.add(new Card("Kitchen", Card.TYPE_ROOM));
        list.add(new Card("Ballroom", Card.TYPE_ROOM));
        list.add(new Card("Hall", Card.TYPE_ROOM));
        list.add(new Card("Study", Card.TYPE_ROOM));
        list.add(new Card("Lounge", Card.TYPE_ROOM));
        list.add(new Card("Library", Card.TYPE_ROOM));
        list.add(new Card("Dining Room", Card.TYPE_ROOM));
        list.add(new Card("Billiard Room", Card.TYPE_ROOM));
        list.add(new Card("Conservatory", Card.TYPE_ROOM));
        return list;
    }

    private void createEnvelope(List<Card> suspects, List<Card> weapons, List<Card> rooms) {
        Collections.shuffle(suspects);
        Collections.shuffle(weapons);
        Collections.shuffle(rooms);

        // Takes the top card from each pile and adds to envelope
        envelope.add(suspects.remove(0));
        envelope.add(weapons.remove(0));
        envelope.add(rooms.remove(0));
    }
    private void assembleMainDeck(List<Card> remainingSuspects, List<Card> remainingWeapons, List<Card> remainingRooms) {
        mainDeck.addAll(remainingSuspects);
        mainDeck.addAll(remainingWeapons);
        mainDeck.addAll(remainingRooms);

        Collections.shuffle(mainDeck);
    }

    public List<Card> getMainDeck() { return mainDeck; }

    public List<Card> getEnvelope() { return envelope; }
}
