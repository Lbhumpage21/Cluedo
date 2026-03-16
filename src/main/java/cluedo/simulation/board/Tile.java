package cluedo.simulation.board;

public abstract class Tile {
    private int x;
    private int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {return x;}
    public int getY() {return y;}

    // Forces subclasses to define how they look as text
    // Just to visualise the board at an early stage, can remove later
    public abstract String toString();
}
