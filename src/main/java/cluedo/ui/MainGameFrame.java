package cluedo.ui;

import javax.swing.*;
import java.awt.*;

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

    private String[] players = {"Jacob", "Jiapei", "Luca", "Hadinbh", "Mohammad"};
    private String[] playerTokens = {"J", "Ji", "L", "H", "M"};
    private final int[] currentPlayerIndex = {0};
    private final int[] currentDiceRoll = {0};
    private final int[][] playerPositions = {
            {1, 0},
            {1, 4},
            {1, 8},
            {7, 2},
            {7, 6}
    };

    /**
     * Refreshes the board display based on the current player positions.
     * Room tiles and player tokens are redrawn whenever the board state changes.
     */
    private void refreshBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(9, 9));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setBackground(Color.WHITE);

                JLabel roomLabel = new JLabel("", SwingConstants.CENTER);
                roomLabel.setFont(new Font("Arial", Font.BOLD, 11));

                JLabel tokenLabel = new JLabel("", SwingConstants.CENTER);
                tokenLabel.setFont(new Font("Arial", Font.BOLD, 14));
                tokenLabel.setForeground(Color.BLACK);

                if (row == 0 && col == 0) {
                    cell.setBackground(new Color(255, 230, 200));
                    roomLabel.setText("Kitchen");
                } else if (row == 0 && col == 4) {
                    cell.setBackground(new Color(255, 240, 200));
                    roomLabel.setText("Ballroom");
                } else if (row == 0 && col == 8) {
                    cell.setBackground(new Color(220, 255, 220));
                    roomLabel.setText("Conservatory");
                } else if (row == 4 && col == 0) {
                    cell.setBackground(new Color(255, 220, 220));
                    roomLabel.setText("Dining");
                } else if (row == 4 && col == 4) {
                    cell.setBackground(new Color(230, 230, 230));
                    roomLabel.setText("Center");
                } else if (row == 4 && col == 8) {
                    cell.setBackground(new Color(220, 240, 255));
                    roomLabel.setText("Library");
                } else if (row == 8 && col == 0) {
                    cell.setBackground(new Color(255, 220, 255));
                    roomLabel.setText("Lounge");
                } else if (row == 8 && col == 4) {
                    cell.setBackground(new Color(240, 220, 255));
                    roomLabel.setText("Hall");
                } else if (row == 8 && col == 8) {
                    cell.setBackground(new Color(220, 255, 255));
                    roomLabel.setText("Study");
                }

                StringBuilder tokensInCell = new StringBuilder();
                boolean currentPlayerHere = false;

                for (int i = 0; i < playerPositions.length; i++) {
                    if (playerPositions[i][0] == row && playerPositions[i][1] == col) {
                        if (tokensInCell.length() > 0) {
                            tokensInCell.append(" ");
                        }
                        tokensInCell.append(playerTokens[i]);

                        if (i == currentPlayerIndex[0]) {
                            currentPlayerHere = true;
                        }
                    }
                }

                tokenLabel.setText(tokensInCell.toString());

                if (currentPlayerHere) {
                    tokenLabel.setForeground(Color.RED);
                } else {
                    tokenLabel.setForeground(Color.BLACK);
                }

                if (currentPlayerHere) {
                    cell.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                } else {
                    cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                }

                cell.add(roomLabel, BorderLayout.NORTH);
                cell.add(tokenLabel, BorderLayout.CENTER);
                boardPanel.add(cell);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
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
     * Returns the room name for a given board position.
     * If the position does not match a room tile, "Hallway" is returned.
     */
    private String getRoomNameFromPosition(int row, int col) {
        if (row == 0 && col == 0) return "Kitchen";
        if (row == 0 && col == 4) return "Ballroom";
        if (row == 0 && col == 8) return "Conservatory";
        if (row == 4 && col == 0) return "Dining Room";
        if (row == 4 && col == 4) return "Billiard Room";
        if (row == 4 && col == 8) return "Library";
        if (row == 8 && col == 0) return "Lounge";
        if (row == 8 && col == 4) return "Hall";
        if (row == 8 && col == 8) return "Study";
        return "Hallway";
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

    private JPanel createRightSectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    public MainGameFrame() {
        setTitle("Cluedo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Board area
        boardPanel = new JPanel(new GridLayout(9, 9));
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

        playerLabel = new JLabel("Current Player: " + players[currentPlayerIndex[0]]);
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        diceLabel = new JLabel("Dice Roll: -");
        diceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        roomLabel = new JLabel("Current Room: Kitchen");
        roomLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        logArea = new JTextArea(8, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setText("- Game started\n- Current player: " + players[currentPlayerIndex[0]] + "\n");
        JScrollPane logScrollPane = new JScrollPane(logArea);
        JScrollBar logScrollBar = logScrollPane.getVerticalScrollBar();

        cardsArea = new JTextArea(6, 20);
        cardsArea.setEditable(false);
        cardsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cardsArea.setLineWrap(true);
        cardsArea.setWrapStyleWord(true);
        cardsArea.setText(
                // Temporary hardcoded cards for GUI testing.
                // This should later be replaced with the actual player hand.
                "- Miss Scarlett\n" +
                        "- Rope\n" +
                        "- Kitchen\n"
        );
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
        controlPanel.setLayout(new GridLayout(2, 4, 12, 12));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Actions"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton rollButton = createStyledButton("Roll Dice");
        JButton moveButton = createStyledButton("Move");
        JButton suggestButton = createStyledButton("Suggest");
        JButton accuseButton = createStyledButton("Accuse");
        JButton endTurnButton = createStyledButton("End Turn");
        JButton backButton = createStyledButton("Back to Menu");
        JButton exitButton = createStyledButton("Exit");

        moveButton.setEnabled(false);
        endTurnButton.setEnabled(false);

        rollButton.addActionListener(e -> {
            int roll = (int) (Math.random() * 6) + 1;
            currentDiceRoll[0] = roll;

            updateDiceLabel(roll);
            appendLog("- " + players[currentPlayerIndex[0]] + " rolled a " + roll);

            rollButton.setEnabled(false);
            moveButton.setEnabled(true);
            endTurnButton.setEnabled(true);
        });

        moveButton.addActionListener(e -> {
            // Placeholder movement logic for the GUI prototype.
            // Final movement should be controlled by backend game rules.
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

            String newRoom = rooms[(int) (Math.random() * rooms.length)];

            int newRow = 4;
            int newCol = 4;

            switch (newRoom) {
                case "Kitchen":
                    newRow = 0;
                    newCol = 0;
                    break;
                case "Ballroom":
                    newRow = 0;
                    newCol = 4;
                    break;
                case "Conservatory":
                    newRow = 0;
                    newCol = 8;
                    break;
                case "Dining Room":
                    newRow = 4;
                    newCol = 0;
                    break;
                case "Billiard Room":
                    newRow = 4;
                    newCol = 4;
                    break;
                case "Library":
                    newRow = 4;
                    newCol = 8;
                    break;
                case "Lounge":
                    newRow = 8;
                    newCol = 0;
                    break;
                case "Hall":
                    newRow = 8;
                    newCol = 4;
                    break;
                case "Study":
                    newRow = 8;
                    newCol = 8;
                    break;
            }

            playerPositions[currentPlayerIndex[0]][0] = newRow;
            playerPositions[currentPlayerIndex[0]][1] = newCol;
            updateRoomLabel(getRoomNameFromPosition(newRow, newCol));

            refreshBoard();

            appendLog("- " + players[currentPlayerIndex[0]] + " moved to " + newRoom);

            moveButton.setEnabled(false);
        });

        suggestButton.addActionListener(e -> {
            // Prototype suggestion popup for GUI testing.
            // This currently demonstrates the interface flow only.
            String[] suspects = {
                    "Miss Scarlett",
                    "Colonel Mustard",
                    "Mrs White",
                    "Mr Green",
                    "Mrs Peacock",
                    "Professor Plum"
            };

            String[] weapons = {
                    "Candlestick",
                    "Dagger",
                    "Lead Pipe",
                    "Revolver",
                    "Rope",
                    "Wrench"
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

                JOptionPane.showMessageDialog(
                        this,
                        "Suggestion made:\nSuspect: " + selectedSuspect + "\nWeapon: " + selectedWeapon,
                        "Suggestion Confirmed",
                        JOptionPane.INFORMATION_MESSAGE
                );

                logArea.append("- " + players[currentPlayerIndex[0]]
                        + " suggested " + selectedSuspect
                        + " with the " + selectedWeapon
                        + " in the " + roomLabel.getText().replace("Current Room: ", "")
                        + "\n");
                logScrollBar.setValue(logScrollBar.getMaximum());
            }
        });

        accuseButton.addActionListener(e -> {
            // Prototype accusation popup for GUI testing.
            // Final accusation handling should be validated by backend logic.
            String[] suspects = {
                    "Miss Scarlett",
                    "Colonel Mustard",
                    "Mrs White",
                    "Mr Green",
                    "Mrs Peacock",
                    "Professor Plum"
            };

            String[] weapons = {
                    "Candlestick",
                    "Dagger",
                    "Lead Pipe",
                    "Revolver",
                    "Rope",
                    "Wrench"
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

                JOptionPane.showMessageDialog(
                        this,
                        "Accusation made:\nSuspect: " + selectedSuspect
                                + "\nWeapon: " + selectedWeapon
                                + "\nRoom: " + selectedRoom,
                        "Accusation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE
                );

                logArea.append("- " + players[currentPlayerIndex[0]]
                        + " accused " + selectedSuspect
                        + " with the " + selectedWeapon
                        + " in the " + selectedRoom
                        + "\n");
                logScrollBar.setValue(logScrollBar.getMaximum());
            }
        });

        endTurnButton.addActionListener(e -> {
            String oldPlayer = players[currentPlayerIndex[0]];
            currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.length;
            String newPlayer = players[currentPlayerIndex[0]];

            currentDiceRoll[0] = 0;

            updatePlayerLabel(newPlayer);
            resetDiceLabel();

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