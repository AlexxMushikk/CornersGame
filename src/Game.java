import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class Game extends JFrame {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8 * TILE_SIZE;
    private static final int BORDER_SIZE = 30;
    private static final int TRANSITION_SIZE = 5;
    private static final int OUTER_BORDER_SIZE = 10;
    private static final int PADDING = 5;
    private static final Color DARK_COLOR = new Color(115, 115, 115);
    private final Mechanic mechanic = new Mechanic();
    private int[] selectedChecker = null;
    private final JLabel statusLabel;
    private final JTextField fromField;
    private final JTextField toField;

    public Game() {
        super("Corners");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setSize(BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE + OUTER_BORDER_SIZE),
                BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE + OUTER_BORDER_SIZE) + 50);
        setResizable(false);

        Image iconImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/icon.png"))).getImage();
        setIconImage(iconImage);

        mechanic.initializeGame();

        ChessBoard board = new ChessBoard();
        add(board, BorderLayout.CENTER);

        statusLabel = new JLabel();
        updateStatusLabel();

        fromField = new JTextField(5);
        toField = new JTextField(5);

        fromField.addActionListener(e -> processInput());
        toField.addActionListener(e -> processInput());
        fromField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    toField.requestFocus();
                }
            }
        });

        toField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    fromField.requestFocus();
                }
            }
        });

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        statusPanel.add(new JLabel("From:"));
        statusPanel.add(fromField);
        statusPanel.add(new JLabel("To:"));
        statusPanel.add(toField);

        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    exitToMainMenu();
                }
            }
        });
    }

    private void processInput() {
        String fromText = fromField.getText().trim().toUpperCase();
        String toText = toField.getText().trim().toUpperCase();

        if (isValidInput(fromText) && isValidInput(toText)) {
            int[] fromCoords = parsePosition(fromText);
            int[] toCoords = parsePosition(toText);

            if (fromCoords != null && toCoords != null) {
                if (mechanic.isMoveValid(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1])) {
                    mechanic.makeMove(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
                    updateStatusLabel();
                    repaint();

                    fromField.setText("");
                    toField.setText("");
                    fromField.requestFocus();
                } else {
                    clearFieldsAndFocus();
                }
            }
        } else {
            clearFieldsAndFocus();
        }
    }

    private void exitToMainMenu() {
        this.dispose();
        new GameMenu();
    }

    private void updateStatusLabel() {
        int currentPlayer = mechanic.getCurrentPlayer();
        String currentTurn = (currentPlayer == 1) ? "White's Turn" : "Black's Turn";
        int whiteWins = mechanic.getWhiteWins();
        int blackWins = mechanic.getBlackWins();
        statusLabel.setText(String.format("%s | Score - White: %d, Black: %d", currentTurn, whiteWins, blackWins));
    }

    private boolean isValidInput(String input) {
        return input.matches("[A-H][1-8]");
    }

    private int[] parsePosition(String position) {
        if (position.length() != 2) return null;
        int col = position.charAt(0) - 'A';
        int row = 8 - (position.charAt(1) - '0');
        return new int[]{row, col};
    }

    private void clearFieldsAndFocus() {
        fromField.setText("");
        toField.setText("");
        fromField.requestFocus();
    }

    private class ChessBoard extends JPanel {
        private Image whiteChecker;
        private Image blackChecker;

        public ChessBoard() {
            try {
                whiteChecker = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/whiteChecker.png"))).getImage();
                blackChecker = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/blackChecker.png"))).getImage();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouseClick(e);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawOuterBorder(g);
            drawBorder(g);
            drawTransition(g);
            drawBoard(g);
            drawCoordinates(g);
            drawCheckers(g);

            if (selectedChecker != null) {
                int row = selectedChecker[0];
                int col = selectedChecker[1];
                int x = col * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE;
                int y = row * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE;

                g.setColor(new Color(0, 255, 0, 128));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                g.setColor(Color.GREEN);
                ((Graphics2D) g).setStroke(new BasicStroke(5));
                ((Graphics2D) g).drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        private void drawOuterBorder(Graphics g) {
            g.setColor(DARK_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        private void drawBorder(Graphics g) {
            g.setColor(Color.WHITE);
            int start = OUTER_BORDER_SIZE;
            g.fillRect(start, start, BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE),
                    BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE));
        }

        private void drawTransition(Graphics g) {
            g.setColor(DARK_COLOR);
            int transitionStart = OUTER_BORDER_SIZE + BORDER_SIZE;
            g.fillRect(transitionStart, transitionStart, BOARD_SIZE + 2 * TRANSITION_SIZE,
                    BOARD_SIZE + 2 * TRANSITION_SIZE);
        }

        private void drawBoard(Graphics g) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if ((row + col) % 2 == 0) {
                        g.setColor(Color.WHITE);
                    } else {
                        g.setColor(DARK_COLOR);
                    }
                    g.fillRect(col * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE,
                            row * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        private void drawCheckers(Graphics g) {
            int checkerSize = (int) (TILE_SIZE * 0.8);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int checker = mechanic.getCheckerAt(row, col);
                    if (checker == 1) {
                        g.drawImage(whiteChecker,
                                col * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + (TILE_SIZE - checkerSize) / 2,
                                row * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + (TILE_SIZE - checkerSize) / 2,
                                checkerSize, checkerSize, null);
                    } else if (checker == -1) {
                        g.drawImage(blackChecker,
                                col * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + (TILE_SIZE - checkerSize) / 2,
                                row * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + (TILE_SIZE - checkerSize) / 2,
                                checkerSize, checkerSize, null);
                    }
                }
            }
        }

        private void drawCoordinates(Graphics g) {
            g.setColor(DARK_COLOR);
            g.setFont(new Font("Arial", Font.PLAIN, 16));

            for (int col = 0; col < 8; col++) {
                String letter = String.valueOf((char) ('A' + col));
                int xPosition = col * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + TILE_SIZE / 2 - 5;
                g.drawString(letter, xPosition, BOARD_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + 25 + PADDING);
                g.drawString(letter, xPosition, OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE - 10 - PADDING);
            }

            for (int row = 0; row < 8; row++) {
                String number = String.valueOf(8 - row);
                int yPosition = row * TILE_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + TILE_SIZE / 2 + 5;
                g.drawString(number, OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE - 20 - PADDING, yPosition);
                g.drawString(number, BOARD_SIZE + OUTER_BORDER_SIZE + BORDER_SIZE + TRANSITION_SIZE + 10 + PADDING, yPosition);
            }
        }

        private void handleMouseClick(MouseEvent e) {
            int col = (e.getX() - OUTER_BORDER_SIZE - BORDER_SIZE - TRANSITION_SIZE) / TILE_SIZE;
            int row = (e.getY() - OUTER_BORDER_SIZE - BORDER_SIZE - TRANSITION_SIZE) / TILE_SIZE;

            if (selectedChecker != null) {
                int fromRow = selectedChecker[0];
                int fromCol = selectedChecker[1];

                if (mechanic.isMoveValid(fromRow, fromCol, row, col)) {
                    mechanic.makeMove(fromRow, fromCol, row, col);
                    selectedChecker = null;

                    int winStatus = mechanic.checkWin();
                    if (winStatus == 1 || winStatus == 2) {
                        JOptionPane.showMessageDialog(this, (winStatus == 1 ? "White" : "Black") + " wins!");
                        mechanic.initializeGame();
                    }

                    updateStatusLabel();
                } else {
                    selectedChecker = null;
                }
                repaint();
            } else if (mechanic.getCheckerAt(row, col) != 0) {
                selectedChecker = new int[]{row, col};
                repaint();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE + OUTER_BORDER_SIZE),
                    BOARD_SIZE + 2 * (BORDER_SIZE + TRANSITION_SIZE + OUTER_BORDER_SIZE));
        }
    }
}