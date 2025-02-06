package org.exp.botservice.gamelogic;

public class GameLogic {
    private static final int HUMAN = 1;
    private static final int BOT = 2;
    private static final int EMPTY = 0;

    // Bot uchun eng yaxshi yurish
    public int[] findBestMove(int[][] board) {
        // 1. Yutish imkoniyatini tekshirish
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = BOT;
                    if (checkWin(board, BOT)) {
                        board[i][j] = EMPTY;
                        return new int[]{i, j};
                    }
                    board[i][j] = EMPTY;
                }
            }
        }

        // 2. Foydalanuvchini bloklash
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = HUMAN;
                    if (checkWin(board, HUMAN)) {
                        board[i][j] = EMPTY;
                        return new int[]{i, j};
                    }
                    board[i][j] = EMPTY;
                }
            }
        }

        // 3. Markazni afzallik berish
        if (board[1][1] == EMPTY) return new int[]{1, 1};

        // 4. Burchaklarni tekshirish
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == EMPTY) {
                return corner;
            }
        }

        // 5. Qolgan joylarni tekshirish
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{-1, -1};
    }

    // Yutishni tekshirish
    private boolean checkWin(int[][] board, int player) {
        int size = board.length;

        // Gorizontal va vertikal
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < size; j++) {
                if (board[i][j] != player) rowWin = false;
                if (board[j][i] != player) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        // Diagonal
        boolean diag1Win = true;
        boolean diag2Win = true;
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) diag1Win = false;
            if (board[i][size-1-i] != player) diag2Win = false;
        }
        return diag1Win || diag2Win;
    }
}