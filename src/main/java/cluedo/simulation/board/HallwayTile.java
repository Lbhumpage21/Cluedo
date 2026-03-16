package cluedo.simulation.board;

import cluedo.simulation.entities.Player;

public class HallwayTile extends Tile{
    private Player currentOccupant;

    public HallwayTile(int x, int y) {
        super(x, y);
        this.currentOccupant = null;
    }
    public boolean isOccupied() {
        return currentOccupant != null;
    }

    public void setOccupant(Player player) {
        this.currentOccupant = player;
    }

    public Player getOccupant() {
        return this.currentOccupant;
    }

    public String getConsoleChar() {
        if (isOccupied()) {
            return currentOccupant.getTokenChar() + "_";
        }
        return "//"; // Represents a hallway tile
    }
}
