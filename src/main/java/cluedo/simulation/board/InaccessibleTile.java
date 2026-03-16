package cluedo.simulation.board;

//Where the player can't move on the board
public class InaccessibleTile extends Tile{

    public InaccessibleTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "XX"; // Represents a wall
    }
}
