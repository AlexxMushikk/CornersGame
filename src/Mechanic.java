public class Mechanic {

    static {
        System.loadLibrary("libUTPproject");
    }

    public native void initializeGame();
    public native boolean isMoveValid(int fromX, int fromY, int toX, int toY);
    public native void makeMove(int fromX, int fromY, int toX, int toY);
    public native int checkWin();
    public native int getCheckerAt(int row, int col);
    public native int getCurrentPlayer();
    public native int getWhiteWins();
    public native int getBlackWins();
}
