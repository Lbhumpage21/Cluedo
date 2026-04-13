package cluedo.simulation.board;

public class RoomTile extends Tile {
    private Room room;
    private boolean isDoor;

    public RoomTile(int x, int y, Room room, boolean isDoor) {
        super(x, y);
        this.room = room;
        this.isDoor = isDoor;
    }

    @Override
    public Room getRoom() {
        return this.room;
    }

    @Override
    public boolean isDoor() {
        return isDoor;
    }
    @Override
    public String toString() {
        if (isDoor) {
            return "[]"; // A door
        }
        // Return the first two letters of the room
        return room.getName().substring(0, 2).toUpperCase();
    }
}
