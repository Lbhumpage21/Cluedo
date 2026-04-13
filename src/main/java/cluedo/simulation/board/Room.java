package cluedo.simulation.board;

import cluedo.simulation.Player;

import javax.print.attribute.standard.Destination;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<Player> occupants;
    private Room secretPassageDestination;

    //TODO: add weapons list
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
