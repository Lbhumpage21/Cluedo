package cluedo.simulation;

public class Card {

    public static final String TYPE_SUSPECT = "Suspect";
    public static final String TYPE_WEAPON = "Weapon";
    public static final String TYPE_ROOM = "Room";


    private final String name;
    private final String type;

    public Card(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }



    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
