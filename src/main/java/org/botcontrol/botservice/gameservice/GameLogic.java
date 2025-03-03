package org.botcontrol.botservice.gameservice;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import static org.botcontrol.botservice.msgservice.Constant.*;

public class GameLogic {
    private static final int HUMAN = 1;
    private static final int BOT = 2;
    private static final int EMPTY = 0;
    private final Random random = new Random();

    // A method that takes logic based on level
    public int[] findBestMove(int[][] board, String difficulty) {
        if (isBoardFull(board)) return new int[]{-1, -1};
        return switch ("level_" + difficulty) {
            case LEVEL_EASY -> findVeryEasyMove(board);
            case LEVEL_AVERAGE -> findAverageMove(board);
            case LEVEL_DIFFICULT -> findDifficultMove(board);
            case LEVEL_EXTREME -> findExtremeMove(board);
            default -> findEasyMove(board);
        };
    }

    // Very easy move mode
    private int[] findVeryEasyMove(int[][] board) {
        List<int[]> emptyCells = getEmptyCells(board);
        return emptyCells.isEmpty() ?
                new int[]{-1, -1} :
                emptyCells.get(random.nextInt(emptyCells.size()));
    }

    // Easy move mode
    private int[] findEasyMove(int[][] board) {
        // Win immediately if possible
        int[] winMove = findWinningMove(board, BOT);
        if (winMove != null) return winMove;

        // Block human win
        int[] blockMove = findWinningMove(board, HUMAN);
        if (blockMove != null) return blockMove;

        // Take center
        if (board[1][1] == EMPTY) return new int[]{1, 1};

        // Take corners
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == EMPTY) {
                return corner;
            }
        }

        // Take any remaining
        return getFirstEmptyCell(board);
    }

    // Average move mode
    private int[] findAverageMove(int[][] board) {
        return random.nextBoolean() ?
                findEasyMove(board) : findDifficultMove(board)
        ;
    }

    // Difficult move mode
    private int[] findDifficultMove(int[][] board) {
        // Win/block immediate threats
        int[] winMove = findWinningMove(board, BOT);
        if (winMove != null) return winMove;
        int[] blockMove = findWinningMove(board, HUMAN);
        if (blockMove != null) return blockMove;

        // Create forks
        int[] forkMove = createFork(board, BOT);
        if (forkMove != null) return forkMove;

        // Block opponent forks
        int[] blockFork = createFork(board, HUMAN);
        if (blockFork != null) return blockFork;

        // Continue with basic strategy
        if (board[1][1] == EMPTY) return new int[]{1, 1};
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == EMPTY) {
                return corner;
            }
        }
        return getFirstEmptyCell(board);
    }

    // Extreme move mode
    private int[] findExtremeMove(int[][] board) {
        int[] bestMove = new int[]{-1, -1};
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = BOT;
                    int score = minimax(board, 0, false);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    // MiniMax algorithm
    private int minimax(int[][] board, int depth, boolean isMaximizing) {
        if (checkWin(board, BOT)) return 1;
        if (checkWin(board, HUMAN)) return -1;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = BOT;
                        int currentScore = minimax(board, depth + 1, false);
                        maxScore = Math.max(maxScore, currentScore);
                        board[i][j] = EMPTY;
                    }
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = HUMAN;
                        int currentScore = minimax(board, depth + 1, true);
                        minScore = Math.min(minScore, currentScore);
                        board[i][j] = EMPTY;
                    }
                }
            }
            return minScore;
        }
    }

    // Auxiliary methods
    private int[] findWinningMove(int[][] board, int player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;
                    boolean win = checkWin(board, player);
                    board[i][j] = EMPTY;
                    if (win) return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private int[] createFork(int[][] board, int player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;
                    int potentialWins = countPotentialWins(board, player);
                    board[i][j] = EMPTY;
                    if (potentialWins >= 2) return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private int countPotentialWins(int[][] board, int player) {
        int count = 0;
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (isLineWinnable(board[i][0], board[i][1], board[i][2], player)) count++;
            if (isLineWinnable(board[0][i], board[1][i], board[2][i], player)) count++;
        }
        // Check diagonals
        if (isLineWinnable(board[0][0], board[1][1], board[2][2], player)) count++;
        if (isLineWinnable(board[0][2], board[1][1], board[2][0], player)) count++;
        return count;
    }

    private boolean isLineWinnable(int a, int b, int c, int player) {
        return (a == player || b == player || c == player)
                && (a == EMPTY || b == EMPTY || c == EMPTY);
    }

    private List<int[]> getEmptyCells(int[][] board) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }

    private int[] getFirstEmptyCell(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private boolean checkWin(int[][] board, int player) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        // Check diagonals
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == EMPTY) return false;
            }
        }
        return true;
    }
}