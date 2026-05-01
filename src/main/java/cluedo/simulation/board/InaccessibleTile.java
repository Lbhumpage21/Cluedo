package cluedo.simulation.board;

/**
 * Represents the spaces on the board a player cannot move to
 */
public class InaccessibleTile extends Tile{

    public InaccessibleTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "XX"; // Represents a wall
    }
}
