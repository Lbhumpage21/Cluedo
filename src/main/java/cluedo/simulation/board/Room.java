package cluedo.simulation.board;

import cluedo.simulation.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<Player> occupants;

    //TODO: add weapons list
    public Room(String name) {
        this.name = name;
        this.occupants = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player player) {
        if (!occupants.contains(player)) {
            occupants.add(player);
        }
    }

    public void removePlayer(Player player) {
        occupants.remove(player);
    }

    public boolean containsPlayer(Player player) {
        return occupants.contains(player);
    }

    public List<Player> getOccupants() {
        return occupants;
    }

}
