package cluedo.simulation.board;

public class RoomTile extends Tile {
    private String roomName;
    private boolean isDoor;

    public RoomTile(int x, int y, String roomName, boolean isDoor) {
        super(x, y);
        this.roomName = roomName;
        this.isDoor = isDoor;
    }
    public String getRoomName() {
        return roomName;
    }

    public boolean getIsDoor() {
        return isDoor;
    }
    @Override
    public String toString() {
        if (isDoor) {
            return "[]"; // A door
        }
        // Return the first two letters of the room
        return roomName.substring(0, 2).toUpperCase();
    }
}
