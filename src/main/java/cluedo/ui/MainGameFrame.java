package cluedo.ui;

import cluedo.simulation.Card;
import cluedo.simulation.GameManager;
import cluedo.simulation.Player;
import cluedo.simulation.board.HallwayTile;
import cluedo.simulation.board.InaccessibleTile;
import cluedo.simulation.board.RoomTile;


import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main GUI window for the Cluedo prototype.
 * This frame displays the board, player information, action controls,
 * game log, and cards panel.
 */
public class MainGameFrame extends JFrame {
    private JPanel boardPanel;

    private JLabel playerLabel;
    private JLabel diceLabel;
    private JLabel roomLabel;
    private JTextArea logArea;
    private JTextArea cardsArea;

    private GameManager gameManager;

    /**
     * Maps backend board coordinates to the simplified 9x9 GUI prototype board.
     * This is still a prototype mapping, not the final real board rendering.
     */
    private Point mapBackendPositionToDisplay(int x, int y) {

        if (x == 16 && y == 0) return new Point(1, 0);  // Miss Scarlett
        if (x == 23 && y == 7) return new Point(7, 6);  // Colonel Mustard
        if (x == 14 && y == 24) return new Point(7, 2); // Mrs White
        if (x == 9 && y == 24) return new Point(1, 4);  // Reverend Green
        if (x == 0 && y == 18) return new Point(0, 8);  // Mrs Peacock
        if (x == 0 && y == 5) return new Point(8, 4);   // Professor Plum

        if (x < 8 && y < 8) return new Point(Math.max(0, x), Math.max(0, y));

        return new Point(4, 4);
    }

    /**
     * Returns a fixed display point for each room in the GUI board.
     */
    private Point getRoomDisplayPoint(String roomName) {
        switch (roomName) {
            case "Kitchen": return new Point(20, 20);
            case "Ballroom": return new Point(11, 20);
            case "Conservatory": return new Point(2, 21);
            case "Dining Room": return new Point(19, 12);
            case "Billiard Room": return new Point(2, 14);
            case "Library": return new Point(2, 8);
            case "Lounge": return new Point(20, 2);
            case "Hall": return new Point(11, 3);
            case "Study": return new Point(2, 2);
            default: return new Point(4, 4);
        }
    }

    /**
     * Refreshes the board display based on backend player data.
     * Current-player highlighting is also based on backend state.
     */
    private void refreshBoard() {
        // Uses a key to generate a board, matching the one used for backend
        String[] layout = {
                "SSSSSSX_XXHHHHXX_XOOOOOO",
                "SSSSSSS__HHHHHH__OOOOOOO",
                "SS4SSSS__HHHHHH__OOO5OOO",
                "SSSSSSs__HH3HHH__OOOOOOO",
                "X________hHHHHH__OOOOOOO",
                "_________HHHHHH__oOOOOOO",
                "XLLLLL___HHhhHH________X",
                "LLLLLLL_________________",
                "LL6LLLl__EEEEE_________X",
                "LLLLLLL__EEEEE__DdDDDDDD",
                "XLLlLL___EEEEE__DDDDDDDD",
                "X________EEEEE__DDDDDDDD",
                "IiIIII___EEEEE__dDD7DDDD",
                "IIIIII___EEEEE__DDDDDDDD",
                "II8III___EEEEE__DDDDDDDD",
                "IIIIIi_____________DDDDD",
                "IIIIII_________________X",
                "X_______BbBBBBbB________",
                "________BBBBBBBB__KkKKKX",
                "XCCCc___bBBBBBBb__KKKKKK",
                "CCCCCC__BBB2BBBB__KK1KKK",
                "CC9CCC__BBBBBBBB__KKKKKK",
                "CCCCCC__BBBBBBBB__KKKKKK",
                "CCCCCCX___BBBB___XKKKKKK",
                "XXXXXXXXX_BBBB_XXXXXXXXX",
        };

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(25, 24));

        Player currentPlayer = gameManager.getCurrentPlayer();
        List<Player> allPlayers = gameManager.getPlayers();

        for (int y = 0; y < 25; y++) {
            String row = layout[y];
            for (int x = 0; x < 24; x++){
                JPanel cell = new JPanel(new BorderLayout());
                cell.setBackground(Color.WHITE);

                JLabel boardRoomLabel = new JLabel("", SwingConstants.CENTER);
                boardRoomLabel.setFont(new Font("Arial", Font.BOLD, 11));

                JLabel tokenLabel = new JLabel("", SwingConstants.CENTER);
                tokenLabel.setFont(new Font("Arial", Font.BOLD, 14));
                tokenLabel.setForeground(Color.BLACK);

                char symbol = row.charAt(x);
                switch (symbol) {
                    // Basic Tiles
                    case 'X': cell.setBackground(Color.BLACK); cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); break;
                    case '_': cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); break;

                        // Room Tiles
                    case 'K': cell.setBackground(new Color(255, 230, 200)); boardRoomLabel.setText("K"); break;
                    case 'B': cell.setBackground(new Color(255, 240, 200)); boardRoomLabel.setText("B");  break;
                    case 'H': cell.setBackground(new Color(240, 220, 255)); boardRoomLabel.setText("H");  break;
                    case 'S': cell.setBackground(new Color(220, 255, 255)); boardRoomLabel.setText("S");  break;
                    case 'O': cell.setBackground(new Color(255, 220, 255)); boardRoomLabel.setText("LO");  break;
                    case 'L': cell.setBackground(new Color(220, 240, 255)); boardRoomLabel.setText("L");  break;
                    case 'D': cell.setBackground(new Color(255, 220, 220)); boardRoomLabel.setText("D");  break;
                    case 'I': cell.setBackground(new Color(240, 220, 255)); boardRoomLabel.setText("BI");  break;
                    case 'C': cell.setBackground(new Color(220, 255, 220)); boardRoomLabel.setText("C");  break;
                    case 'E': cell.setBackground(new Color(230, 230, 230)); boardRoomLabel.setText("E");  break;

                        // Door Tiles
                    case 'k': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'b': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'h': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 's': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'o': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'l': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'd': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'i': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;
                    case 'c': cell.setBackground(new Color(150, 75, 0)); boardRoomLabel.setText("D"); break;

                }



                StringBuilder tokensInCell = new StringBuilder();
                boolean currentPlayerHere = false;

                for (Player player : allPlayers) {
                    Point displayPoint;

                    if (player.getCurrentRoom() != null) {
                        displayPoint = getRoomDisplayPoint(player.getCurrentRoom().getName());
                    } else {
                        displayPoint = new Point(player.getX(),player.getY());
                    }

                    if (displayPoint.x == x && displayPoint.y == y) {
                        if (tokensInCell.length() > 0) {
                            tokensInCell.append(" ");
                        }
                        tokensInCell.append(player.getTokenChar());

                        if (player.getName().equals(currentPlayer.getName())) {
                            currentPlayerHere = true;
                        }
                    }
                }

                tokenLabel.setText(tokensInCell.toString());

                if (currentPlayerHere) {
                    tokenLabel.setForeground(Color.RED);
                    cell.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                } else {
                    tokenLabel.setForeground(Color.BLACK);

                }
                cell.add(boardRoomLabel, BorderLayout.NORTH);
                cell.add(tokenLabel, BorderLayout.CENTER);
                boardPanel.add(cell);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private JLabel createSectionTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return title;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 38));
        return button;
    }

    /**
     * Updates the current player label in the info panel.
     */
    private void updatePlayerLabel(String playerName) {
        playerLabel.setText("Current Player: " + playerName);
    }

    /**
     * Updates the dice roll label with the latest roll value.
     */
    private void updateDiceLabel(int roll) {
        diceLabel.setText("Dice Roll: " + roll);
    }

    /**
     * Resets the dice roll label to its default state.
     */
    private void resetDiceLabel() {
        diceLabel.setText("Dice Roll: -");
    }

    /**
     * Updates the current room label in the info panel.
     */
    private void updateRoomLabel(String roomName) {
        roomLabel.setText("Current Room: " + roomName);
    }

    /**
     * Adds a new message to the game log and scrolls to the latest entry.
     */
    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Updates the cards display for the current player.
     */
    private void updateCardsDisplay(String cardsText) {
        cardsArea.setText(cardsText);
    }

    /**
     * Builds the card display text for the current player using backend data.
     */
    private String buildCardsTextForCurrentPlayer() {
        Player currentPlayer = gameManager.getCurrentPlayer();
        List<Card> hand = currentPlayer.getHand();

        if (hand == null || hand.isEmpty()) {
            return "- No cards";
        }

        StringBuilder cardsText = new StringBuilder();
        for (Card card : hand) {
            cardsText.append("- ").append(card.getName()).append("\n");
        }
        return cardsText.toString();
    }

    /**
     * Updates the player label, room label, and cards area from backend data.
     */
    private void refreshCurrentPlayerInfo() {
        Player currentPlayer = gameManager.getCurrentPlayer();

        updatePlayerLabel(currentPlayer.getName());

        if (currentPlayer.getCurrentRoom() != null) {
            updateRoomLabel(currentPlayer.getCurrentRoom().getName());
        } else {
            updateRoomLabel("Hallway");
        }

        updateCardsDisplay(buildCardsTextForCurrentPlayer());
    }

    /**
     * Lets the player choose one of the four adjacent backend coordinates.
     */
    private void handleMove(JButton moveButton) {
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (currentPlayer.getCurrentRoom() != null) {
            handleLeaveRoom(moveButton);
            return;
        }
        int currentX = currentPlayer.getX();
        int currentY = currentPlayer.getY();

        String[] options = {
                "Up (" + currentX + "," + (currentY - 1) + ")",
                "Down (" + currentX+ "," + (currentY + 1) + ")",
                "Left (" + (currentX - 1) + "," + currentY + ")",
                "Right (" + (currentX + 1) + "," + currentY + ")"
        };

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Current player: " + currentPlayer.getName() + "\n" +
                        "Current backend position: (" + currentX + ", " + currentY + ")\n" +
                        "Spaces remaining: " + gameManager.getSpacesRemaining() + "\n\n" +
                        "Choose a direction to try:",
                "Move",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) {
            return;
        }

        int targetX = currentX;
        int targetY = currentY;

        if (choice.startsWith("Up")) {
            targetY = currentY - 1;
        } else if (choice.startsWith("Down")) {
            targetY = currentY + 1;
        } else if (choice.startsWith("Left")) {
            targetX = currentX - 1;
        } else if (choice.startsWith("Right")) {
            targetX = currentX + 1;
        }

        boolean moved = gameManager.attemptMove(targetX, targetY);

        if (moved) {
            refreshCurrentPlayerInfo();
            refreshBoard();

            if (gameManager.getCurrentPlayer().getCurrentRoom() != null) {
                appendLog("- " + currentPlayer.getName() + " moved into " +
                        gameManager.getCurrentPlayer().getCurrentRoom().getName());


            } else {
                appendLog("- " + currentPlayer.getName() + " moved to (" + targetX + ", " + targetY + ")");
            }

            if (gameManager.getSpacesRemaining() == 0) {
                moveButton.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Move not allowed.\n" +
                            "Try another adjacent direction.\n" +
                            "Current position: (" + currentPlayer.getX() + ", " + currentPlayer.getY() + ")\n" +
                            "Spaces remaining: " + gameManager.getSpacesRemaining()
            );
        }
    }

    private void handleLeaveRoom(JButton moveButton) {
        Player currentPlayer = gameManager.getCurrentPlayer();
        String roomName = currentPlayer.getCurrentRoom().getName();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "You are in the " + roomName + " Would you like to leave?",
                "Leave Room",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean left = gameManager.leaveRoom();
            if (left) {
            refreshCurrentPlayerInfo();
            refreshBoard();}
            appendLog("- " + currentPlayer.getName() + " left the " + roomName);
            if (gameManager.getSpacesRemaining() == 0) {
                moveButton.setEnabled(false);
            }

        } else {
            appendLog("- " + currentPlayer.getName() + " could not leave " + roomName);
        }




    }

    /**
     * Testing helper: instantly puts the current player into a selected room.
     */
    private void handleEnterTestRoom() {
        String[] rooms = {
                "Kitchen",
                "Ballroom",
                "Conservatory",
                "Dining Room",
                "Billiard Room",
                "Library",
                "Lounge",
                "Hall",
                "Study"
        };

        String selectedRoom = (String) JOptionPane.showInputDialog(
                this,
                "Select a room for testing:",
                "Enter Test Room",
                JOptionPane.PLAIN_MESSAGE,
                null,
                rooms,
                rooms[0]
        );

        if (selectedRoom == null) {
            return;
        }

        boolean success = gameManager.forceCurrentPlayerIntoRoom(selectedRoom);

        if (success) {
            refreshCurrentPlayerInfo();
            refreshBoard();
            appendLog("- " + gameManager.getCurrentPlayer().getName() + " was placed into " + selectedRoom + " for testing.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not place player into room.");
        }
    }

    /**
     * Uses backend suggestion logic.
     */
    private void handleSuggestion() {
        Player currentPlayer = gameManager.getCurrentPlayer();

        if (currentPlayer.getCurrentRoom() == null) {
            JOptionPane.showMessageDialog(this, "You must be in a room to make a suggestion.");
            return;
        }

        String[] suspects = {
                "Miss Scarlett",
                "Colonel Mustard",
                "Mrs White",
                "Reverend Green",
                "Mrs Peacock",
                "Professor Plum"
        };

        String[] weapons = {
                "Candlestick",
                "Dagger",
                "Lead Pipe",
                "Revolver",
                "Rope",
                "Spanner"
        };

        JComboBox<String> suspectBox = new JComboBox<>(suspects);
        JComboBox<String> weaponBox = new JComboBox<>(weapons);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Select a suspect:"));
        panel.add(suspectBox);
        panel.add(new JLabel("Select a weapon:"));
        panel.add(weaponBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Make a Suggestion",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedSuspect = (String) suspectBox.getSelectedItem();
            String selectedWeapon = (String) weaponBox.getSelectedItem();
            String currentRoomName = currentPlayer.getCurrentRoom().getName();

            Card suspectCard = gameManager.getCardFromDatabase(selectedSuspect);
            Card weaponCard = gameManager.getCardFromDatabase(selectedWeapon);
            Card roomCard = gameManager.getCardFromDatabase(currentRoomName);

            Card revealedCard = gameManager.makeSuggestion(suspectCard, weaponCard, roomCard);

            if (revealedCard != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Suggestion made.\nRevealed card: " + revealedCard.getName(),
                        "Suggestion Result",
                        JOptionPane.INFORMATION_MESSAGE
                );

                appendLog("- " + currentPlayer.getName()
                        + " suggested " + selectedSuspect
                        + " with the " + selectedWeapon
                        + " in the " + currentRoomName
                        + ". Revealed: " + revealedCard.getName());
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Suggestion made.\nNobody could disprove it.",
                        "Suggestion Result",
                        JOptionPane.INFORMATION_MESSAGE
                );

                appendLog("- " + currentPlayer.getName()
                        + " suggested " + selectedSuspect
                        + " with the " + selectedWeapon
                        + " in the " + currentRoomName
                        + ". Nobody could disprove it.");
            }

            refreshCurrentPlayerInfo();
            refreshBoard();
        }
    }

    /**
     * Uses backend accusation logic.
     */
    private void handleAccusation() {
        Player currentPlayer = gameManager.getCurrentPlayer();

        if (currentPlayer.getCurrentRoom() == null) {
            JOptionPane.showMessageDialog(this, "You must be in a room to make an accusation.");
            return;
        }

        String[] suspects = {
                "Miss Scarlett",
                "Colonel Mustard",
                "Mrs White",
                "Reverend Green",
                "Mrs Peacock",
                "Professor Plum"
        };

        String[] weapons = {
                "Candlestick",
                "Dagger",
                "Lead Pipe",
                "Revolver",
                "Rope",
                "Spanner"
        };

        String[] rooms = {
                "Kitchen",
                "Ballroom",
                "Conservatory",
                "Dining Room",
                "Billiard Room",
                "Library",
                "Lounge",
                "Hall",
                "Study"
        };

        JComboBox<String> suspectBox = new JComboBox<>(suspects);
        JComboBox<String> weaponBox = new JComboBox<>(weapons);
        JComboBox<String> roomBox = new JComboBox<>(rooms);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Select a suspect:"));
        panel.add(suspectBox);
        panel.add(new JLabel("Select a weapon:"));
        panel.add(weaponBox);
        panel.add(new JLabel("Select a room:"));
        panel.add(roomBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Make an Accusation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedSuspect = (String) suspectBox.getSelectedItem();
            String selectedWeapon = (String) weaponBox.getSelectedItem();
            String selectedRoom = (String) roomBox.getSelectedItem();

            Card suspectCard = gameManager.getCardFromDatabase(selectedSuspect);
            Card weaponCard = gameManager.getCardFromDatabase(selectedWeapon);
            Card roomCard = gameManager.getCardFromDatabase(selectedRoom);

            boolean correct = gameManager.makeAccusation(suspectCard, weaponCard, roomCard);

            if (correct) {
                JOptionPane.showMessageDialog(
                        this,
                        currentPlayer.getName() + " solved the murder and won the game!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE
                );
                appendLog("- " + currentPlayer.getName() + " made a correct accusation and won the game.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        currentPlayer.getName() + " made an incorrect accusation and was eliminated.",
                        "Accusation Result",
                        JOptionPane.WARNING_MESSAGE
                );
                appendLog("- " + currentPlayer.getName() + " made an incorrect accusation and was eliminated.");
            }

            refreshCurrentPlayerInfo();
            refreshBoard();
        }
    }

    public MainGameFrame() {
        gameManager = new GameManager();

        setTitle("Cluedo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Board area
        boardPanel = new JPanel(new GridLayout(25, 24));
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Game Board"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        refreshBoard();

        // Info area
        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(280, 700));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Game Info"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        playerLabel = new JLabel("Current Player: " + gameManager.getCurrentPlayer().getName());
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        diceLabel = new JLabel("Dice Roll: -");
        diceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        if (gameManager.getCurrentPlayer().getCurrentRoom() != null) {
            roomLabel = new JLabel("Current Room: " + gameManager.getCurrentPlayer().getCurrentRoom().getName());
        } else {
            roomLabel = new JLabel("Current Room: Hallway");
        }
        roomLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        logArea = new JTextArea(8, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setText("- Game started\n- Current player: " + gameManager.getCurrentPlayer().getName() + "\n");
        JScrollPane logScrollPane = new JScrollPane(logArea);

        cardsArea = new JTextArea(6, 20);
        cardsArea.setEditable(false);
        cardsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cardsArea.setLineWrap(true);
        cardsArea.setWrapStyleWord(true);
        cardsArea.setText(buildCardsTextForCurrentPlayer());
        JScrollPane cardsScrollPane = new JScrollPane(cardsArea);

        infoPanel.add(playerLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(diceLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(roomLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(createSectionTitle("Game Log:"));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(logScrollPane);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(createSectionTitle("My Cards:"));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(cardsScrollPane);

        // Control area
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 5, 12, 12));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Actions"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton rollButton = createStyledButton("Roll Dice");
        JButton moveButton = createStyledButton("Move");
        JButton testRoomButton = createStyledButton("Enter Test Room");
        JButton suggestButton = createStyledButton("Suggest");
        JButton accuseButton = createStyledButton("Accuse");
        JButton endTurnButton = createStyledButton("End Turn");
        JButton backButton = createStyledButton("Back to Menu");
        JButton exitButton = createStyledButton("Exit");

        moveButton.setEnabled(false);
        endTurnButton.setEnabled(false);

        rollButton.addActionListener(e -> {
            int roll = gameManager.rollDiceForCurrentPlayer();

            updateDiceLabel(roll);
            appendLog("- " + gameManager.getCurrentPlayer().getName() + " rolled a " + roll);

            rollButton.setEnabled(false);
            moveButton.setEnabled(true);
            endTurnButton.setEnabled(true);
        });

        moveButton.addActionListener(e -> handleMove(moveButton));
        testRoomButton.addActionListener(e -> handleEnterTestRoom());
        suggestButton.addActionListener(e -> handleSuggestion());
        accuseButton.addActionListener(e -> handleAccusation());

        endTurnButton.addActionListener(e -> {
            String oldPlayer = gameManager.getCurrentPlayer().getName();
            gameManager.endTurn();
            String newPlayer = gameManager.getCurrentPlayer().getName();

            refreshCurrentPlayerInfo();
            resetDiceLabel();
            refreshBoard();

            appendLog("- " + oldPlayer + " ended their turn");
            appendLog("- It is now " + newPlayer + "'s turn");

            rollButton.setEnabled(true);
            moveButton.setEnabled(false);
            endTurnButton.setEnabled(false);
        });

        backButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Return to the start menu?",
                    "Back to Menu",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                StartMenuFrame startMenuFrame = new StartMenuFrame();
                startMenuFrame.setVisible(true);
                dispose();
            }
        });

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        controlPanel.add(rollButton);
        controlPanel.add(moveButton);
        controlPanel.add(testRoomButton);
        controlPanel.add(suggestButton);
        controlPanel.add(accuseButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(backButton);
        controlPanel.add(exitButton);

        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGameFrame frame = new MainGameFrame();
            frame.setVisible(true);
        });
    }
}