package cluedo.simulation.board;

import cluedo.simulation.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the cluedo game board, consisting of a 2d grid of tiles
 * The board is 24 tiles wide and 25 tall,
 * the layout is taken from a string, where each character maps to a tile
 * Tiles are initialised on construction and do not change
 */

public class Board {
    private Tile[][] grid;
    private Map<String, Room> rooms;

    // Board dimensions
    private final int COLS = 24;
    private final int ROWS = 25;

    /**
     * Creates a 24 by 25 array of tiles and initialises the rooms
     */
    public Board() {
        //creates a 24 by 25 array of tiles
        grid = new Tile[COLS][ROWS];

        rooms = new HashMap<>();

        // Initialises the rooms
        rooms.put("Kitchen", new Room("Kitchen"));
        rooms.put("Ballroom", new Room("Ballroom"));
        rooms.put("Hall", new Room("Hall"));
        rooms.put("Study", new Room("Study"));
        rooms.put("Lounge", new Room("Lounge"));
        rooms.put("Library", new Room("Library"));
        rooms.put("Dining Room", new Room("Dining Room"));
        rooms.put("Billiard Room", new Room("Billiard Room"));
        rooms.put("Conservatory", new Room("Conservatory"));

        //setting secret passages
        rooms.get("Kitchen").setSecretPassage(rooms.get("Study"));
        rooms.get("Study").setSecretPassage(rooms.get("Kitchen"));
        rooms.get("Lounge").setSecretPassage(rooms.get("Conservatory"));
        rooms.get("Conservatory").setSecretPassage(rooms.get("Lounge"));


        setupBoard();
    }

    /**
     * Parses the layout string and populates the grid
     * <p>
     * Parser for the rooms
     * K = Kitchen, k = Kitchen door
     * B = Ballroom, b = Ballroom door
     * H = Hall , h = Hall door
     * S = Study, s = Study door
     * O = Lounge, o = Lounge door
     * L = Library, l = Library door
     * D = Dining Room, d = Dining Room door
     * I = Billiard Room, i = Billiard Room door
     * C = Conservatory, c = Conservatory door
     * _ = Hallway,     X = Wall/Inaccessible
     **/

    private void setupBoard() {
        String[] layout = {
                "SSSSSSX_XXHHHHXX_XOOOOOO",
                "SSSSSSS__HHHHHH__OOOOOOO",
                "SSSSSSS__HHHHHH__OOOOOOO",
                "SSSSSSs__HHHHHH__OOOOOOO",
                "X________hHHHHH__OOOOOOO",
                "_________HHHHHH__oOOOOOO",
                "XLLLLL___HHhhHH________X",
                "LLLLLLL_________________",
                "LLLLLLl__XXXXX_________X",
                "LLLLLLL__XXXXX__DdDDDDDD",
                "XLLlLL___XXXXX__DDDDDDDD",
                "X________XXXXX__DDDDDDDD",
                "IiIIII___XXXXX__dDDDDDDD",
                "IIIIII___XXXXX__DDDDDDDD",
                "IIIIII___XXXXX__DDDDDDDD",
                "IIIIIi_____________DDDDD",
                "IIIIII_________________X",
                "X_______BbBBBBbB________",
                "________BBBBBBBB__KkKKKX",
                "XCCCc___bBBBBBBb__KKKKKK",
                "CCCCCC__BBBBBBBB__KKKKKK",
                "CCCCCC__BBBBBBBB__KKKKKK",
                "CCCCCC__BBBBBBBB__KKKKKK",
                "CCCCCCX___BBBB___XKKKKKK",
                "XXXXXXXXX_BBBB_XXXXXXXXX",
        };

        for (int y = 0; y < layout.length; y++) {
            String row = layout[y];
            for (int x = 0; x < row.length(); x++){
                char symbol = row.charAt(x);

                switch (symbol) {
                    // Basic Tiles
                    case 'X': grid[x][y] = new InaccessibleTile(x, y);break;
                    case '_': grid[x][y] = new HallwayTile(x, y); break;

                    // Room Tiles
                    case 'K': grid[x][y] = new RoomTile(x, y, rooms.get("Kitchen"),false); break;
                    case 'B': grid[x][y] = new RoomTile(x, y, rooms.get("Ballroom"),false); break;
                    case 'H': grid[x][y] = new RoomTile(x, y, rooms.get("Hall"),false); break;
                    case 'S': grid[x][y] = new RoomTile(x, y, rooms.get("Study"),false); break;
                    case 'O': grid[x][y] = new RoomTile(x, y,  rooms.get("Lounge"),false); break;
                    case 'L': grid[x][y] = new RoomTile(x, y,  rooms.get("Library"),false); break;
                    case 'D': grid[x][y] = new RoomTile(x, y,  rooms.get("Dining Room"),false); break;
                    case 'I': grid[x][y] = new RoomTile(x, y,  rooms.get("Billiard Room"),false); break;
                    case 'C': grid[x][y] = new RoomTile(x, y,  rooms.get("Conservatory"),false); break;

                    // Door Tiles
                    case 'k': grid[x][y] = new RoomTile(x, y,  rooms.get("Kitchen"),true); break;
                    case 'b': grid[x][y] = new RoomTile(x, y,  rooms.get("Ballroom"),true); break;
                    case 'h': grid[x][y] = new RoomTile(x, y,  rooms.get("Hall"),true); break;
                    case 's': grid[x][y] = new RoomTile(x, y,  rooms.get("Study"),true); break;
                    case 'o': grid[x][y] = new RoomTile(x, y,  rooms.get("Lounge"),true); break;
                    case 'l': grid[x][y] = new RoomTile(x, y,  rooms.get("Library"),true); break;
                    case 'd': grid[x][y] = new RoomTile(x, y,  rooms.get("Dining Room"),true); break;
                    case 'i': grid[x][y] = new RoomTile(x, y,  rooms.get("Billiard Room"),true); break;
                    case 'c': grid[x][y] = new RoomTile(x, y,  rooms.get("Conservatory"),true); break;


                }
            }
        }
    }

    /**
     * Takes coordinates and returns the tile object at them
     * @param x coordinate
     * @param y coordinate
     * @return the tile at the specified coordinates
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
            return null;
        }
        return grid[x][y];
    }

    /**
     * Takes the rooms name and returns the room object
     * @param roomName String name of a room
     * @return the room object specified
     */
    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    /**
     * Checks if anything is blocking the tile,
     * if not the player is set on the tile
     * @param x coordinate
     * @param y coordinate
     * @param player Player object
     * @return whether player was set on the tile or not
     */
    public boolean setPlayerOnHallway(int x, int y, Player player) {
        Tile targetTile = getTile(x, y);

        // Rooms handle their own occupants
        if (targetTile instanceof HallwayTile) {
            HallwayTile hallway = (HallwayTile) targetTile;

            if (hallway.getOccupant() == null){
                hallway.setOccupant(player);
                return true;
            }
            else {
                System.out.println("Move rejected: Tile ["+ x +"]["+ y +"] is occupied");
                return false;
            }
        }
        else {
            if (targetTile.isDoor()){
                return true;
            }
            System.out.println("Move rejected: Tile is either a wall or inaccessible tile.");
            return false;
        }
    }

    /**
     * Removes a player from a tile
     * @param x coordinate
     * @param y coordinate
     */
    public void clearHallwayTile(int x, int y) {
        Tile targetTile = grid[x][y];
        if (targetTile instanceof HallwayTile) {
            HallwayTile hallway = (HallwayTile) targetTile;
            hallway.setOccupant(null);
        }

    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                Tile tile = getTile(x, y);
                if (tile != null) {
                    sb.append(tile.toString()).append(" ");
                }
                else {
                    sb.append("??");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
