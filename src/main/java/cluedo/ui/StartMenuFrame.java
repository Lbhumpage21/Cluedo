package cluedo.ui;

import javax.swing.*;
import java.awt.*;

public class StartMenuFrame extends JFrame {

    public StartMenuFrame() {
        setTitle("Cluedo - Start Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Cluedo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton startButton = new JButton("Start Game");
        JButton rulesButton = new JButton("Rules");
        JButton exitButton = new JButton("Exit");

        startButton.addActionListener(e -> {
            MainGameFrame mainGameFrame = new MainGameFrame();
            mainGameFrame.setVisible(true);
            dispose();
        });

        rulesButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Cluedo Rules:\n\n" +
                            "1. Move around the board.\n" +
                            "2. Enter rooms to make suggestions.\n" +
                            "3. Suggest a suspect, weapon, and room.\n" +
                            "4. Use clues to work out the solution.\n" +
                            "5. Make an accusation when you are confident.\n" +
                            "6. If your accusation is wrong, you are eliminated.",
                    "Rules",
                    JOptionPane.INFORMATION_MESSAGE
            );
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

        buttonPanel.add(startButton);
        buttonPanel.add(rulesButton);
        buttonPanel.add(exitButton);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StartMenuFrame frame = new StartMenuFrame();
            frame.setVisible(true);
        });
    }
}