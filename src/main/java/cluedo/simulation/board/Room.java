package cluedo.simulation.board;

import cluedo.simulation.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the occupants of a room
 */
public class Room {
    private String name;
    private List<Player> occupants;
    private Room secretPassageDestination;

    public Room(String name) {
        this.name = name;
        this.occupants = new ArrayList<>();
        this.secretPassageDestination = null;
    }

    public String getName() {
        return name;
    }

    public void setSecretPassage(Room destination) {
        this.secretPassageDestination = destination;
    }

    public Room getSecretPassage() {
        return secretPassageDestination;
    }

    public boolean hasSecretPassage() {
        return secretPassageDestination != null;
    }

}
