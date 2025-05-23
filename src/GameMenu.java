import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class GameMenu extends JFrame {

    public GameMenu() {
        super("Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ImageIcon iconFile = new ImageIcon(Objects.requireNonNull(GameMenu.class.getResource("resources/icon.png")));
        setIconImage(iconFile.getImage());

        ImageIcon backgroundIcon = new ImageIcon(Objects.requireNonNull(GameMenu.class.getResource("resources/menu.png")));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout());

        setSize(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        JButton playButton = new JButton("Play");
        styleButton(playButton);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Game();
                dispose();
            }
        });

        // Создание кнопки Exit
        JButton exitButton = new JButton("Exit");
        styleButton(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(35, 0, 35, 0);
        backgroundLabel.add(playButton, gbc);

        gbc.gridy = 1;
        backgroundLabel.add(exitButton, gbc);

        setContentPane(backgroundLabel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void styleButton(JButton button) {
        button.setBackground(new Color(210, 105, 30));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 4));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 60));
    }
}
