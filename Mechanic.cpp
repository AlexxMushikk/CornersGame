#include "Mechanic.h"
#include <complex>

const int BOARD_SIZE = 8;
int board[BOARD_SIZE][BOARD_SIZE];
int currentPlayer = 1;
int whiteWins = 0;
int blackWins = 0;

JNIEXPORT void JNICALL Java_Mechanic_initializeGame(JNIEnv *, jobject) {
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[row][col] = 0;
        }
    }

    for (int row = 5; row < 8; row++) {
        for (int col = 0; col < 3; col++) {
            board[row][col] = 1;
        }
    }

    for (int row = 0; row < 3; row++) {
        for (int col = 5; col < 8; col++) {
            board[row][col] = -1;
        }
    }

    currentPlayer = 1;
}

JNIEXPORT jboolean JNICALL Java_Mechanic_isMoveValid(JNIEnv *, jobject, jint fromX, jint fromY, jint toX, jint toY) {
    if (fromX < 0 || fromX >= BOARD_SIZE || fromY < 0 || fromY >= BOARD_SIZE ||
        toX < 0 || toX >= BOARD_SIZE || toY < 0 || toY >= BOARD_SIZE) {
        return JNI_FALSE;
    }

    int checker = board[fromX][fromY];
    if (checker == 0 || board[toX][toY] != 0 || checker != currentPlayer) {
        return JNI_FALSE;
    }


    if ((std::abs(fromX - toX) == 1 && fromY == toY) ||
        (fromX == toX && std::abs(fromY - toY) == 1) ||
        (std::abs(fromX - toX) == 1 && std::abs(fromY - toY) == 1)) {
        return JNI_TRUE;
    }

    if ((std::abs(fromX - toX) == 2 && fromY == toY) ||
        (fromX == toX && std::abs(fromY - toY) == 2) ||
        (std::abs(fromX - toX) == 2 && std::abs(fromY - toY) == 2)) {
        int middleX = (fromX + toX) / 2;
        int middleY = (fromY + toY) / 2;

        if (board[middleX][middleY] != 0) {
            return JNI_TRUE;
        }
    }

    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_Mechanic_makeMove(JNIEnv *, jobject, jint fromX, jint fromY, jint toX, jint toY) {
    if (Java_Mechanic_isMoveValid(nullptr, nullptr, fromX, fromY, toX, toY)) {
        std::swap(board[fromX][fromY], board[toX][toY]);
        board[fromX][fromY] = 0;

        currentPlayer = -currentPlayer;
    }
}

JNIEXPORT jint JNICALL Java_Mechanic_checkWin(JNIEnv *env, jobject obj) {
    bool whiteInPlace = true;
    bool blackInPlace = true;

    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (row >= 5 && row < 8 && col < 3) {
                if (board[row][col] != -1) blackInPlace = false;
            } else if (row < 3 && col >= 5 && col < 8) {
                if (board[row][col] != 1) whiteInPlace = false;
            }
        }
    }

    if (whiteInPlace) {
        whiteWins++;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!(row < 3 && col >= 5 && col < 8)) {
                    board[row][col] = 0;
                }
            }
        }
        return 1;
    }

    if (blackInPlace) {
        blackWins++;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!(row >= 5 && row < 8 && col < 3)) {
                    board[row][col] = 0;
                }
            }
        }
        return 2;
    }

    return 0;
}

JNIEXPORT jint JNICALL Java_Mechanic_getCheckerAt(JNIEnv *, jobject, jint row, jint col) {
    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
        return board[row][col];
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_Mechanic_getCurrentPlayer(JNIEnv *, jobject) {
    return currentPlayer;
}

JNIEXPORT jint JNICALL Java_Mechanic_getWhiteWins(JNIEnv *, jobject) {
    return whiteWins;
}

JNIEXPORT jint JNICALL Java_Mechanic_getBlackWins(JNIEnv *, jobject) {
    return blackWins;
}
