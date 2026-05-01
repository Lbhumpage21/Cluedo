package cluedo.simulation.board;

import cluedo.simulation.Player;

/**
 * Represents a hallway tile on the board
 * Hallway tiles can be occupied by one player at a time
 */

public class HallwayTile extends Tile{
    private Player currentOccupant;

    /**
     * Creates an onoccupied hallway tile
     * @param x coordinate
     * @param y coordinate
     */
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
    @Override
    public String toString() {
        if (isOccupied()) {
            return currentOccupant.getTokenChar() + "_";
        }
        return "//"; // Represents a hallway tile
    }
}
