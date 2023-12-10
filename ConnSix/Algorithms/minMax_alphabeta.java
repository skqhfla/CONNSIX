import java.util.ArrayList;
import java.util.List;

public class GomokuAI {
    public static final int MAX_DEPTH = 3;
    public static final int SIZE = 19;
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
 
    public static void main(String[] args) {
        int[][] board = new int[SIZE][SIZE];
        for (int[] row : board) {
            java.util.Arrays.fill(row, EMPTY);
        }

        // 초기 바둑돌 배치 (예: 흑돌이 중앙에 착수)
        board[SIZE / 2][SIZE / 2] = BLACK;
        // 최적의 움직임 찾기 (백돌 입장에서)
        Move[] bestMoves = findBestMove(board, WHITE);
        System.out.println("Best moves for WHITE: " + bestMoves[0] + ", " + bestMoves[1]);
    }

    public static Move[] findBestMove(int[][] board, int player) {
        Move[] bestMoves = new Move[2];
        int bestValue = (player == BLACK) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move[] moves : getLegalMoves(board, player)) {
            applyMove(board, moves, player);
            int boardValue = alphabeta(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player == BLACK);
            undoMove(board, moves);
            if ((player == BLACK && boardValue > bestValue) || (player == WHITE && boardValue < bestValue)) {
                bestMoves[0] = moves[0];
                bestMoves[1] = moves[1];
                bestValue = boardValue;
            }
        }

        return bestMoves;
    }

    public static int alphabeta(int[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || isTerminal(board)) {
            return evaluate(board);
        }

        if (maximizingPlayer) {
            int value = Integer.MIN_VALUE;
            for (Move[] moves : getLegalMoves(board, BLACK)) {
                applyMove(board, moves, BLACK);
                value = Math.max(value, alphabeta(board, depth - 1, alpha, beta, false));
                undoMove(board, moves);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // beta cut-off
                }
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move[] moves : getLegalMoves(board, WHITE)) {
                applyMove(board, moves, WHITE);
                value = Math.min(value, alphabeta(board, depth - 1, alpha, beta, true));
                undoMove(board, moves);
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break; // alpha cut-off
                }
            }
            return value;
        }
    }

    // 플레이어가 둘 수 있는 모든 합법적인 두 개의 돌의 조합을 반환합니다.
    public static List<Move[]> getLegalMoves(int[][] board, int player) {
        List<Move[]> legalMoves = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;
                    for (int k = 0; k < SIZE; k++) {
                        for (int l = 0; l < SIZE; l++) {
                            if (board[k][l] == EMPTY && !(k == i && l == j)) {
                                legalMoves.add(new Move[]{new Move(i, j), new Move(k, l)});
                            }
                        }
                    }
                    board[i][j] = EMPTY;
                }
            }
        }
        return legalMoves;
    }

    public static void applyMove(int[][] board, Move[] moves, int player) {
        for (Move move : moves) {
            board[move.row][move.col] = player;
        }
    }

    public static void undoMove(int[][] board, Move[] moves) {
        for (Move move : moves) {
            board[move.row][move.col] = EMPTY;
        }
    }

    // 게임이 끝났는지 확인합니다.
   public static boolean isTerminal(int[][] board) {
    // 흑돌과 백돌 모두에 대해 승리 조건을 검사합니다.
    return checkLines(board, BLACK) || checkLines(board, WHITE);
   }

    private static boolean checkLines(int[][] board, int player) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == player) {
                    if (checkExactStones(board, i, j, 1, 0, player, 6) || // 가로
                        checkExactStones(board, i, j, 0, 1, player, 6) || // 세로
                        checkExactStones(board, i, j, 1, 1, player, 6) || // 대각선 (하향)
                        checkExactStones(board, i, j, -1, 1, player, 6)) { // 대각선 (상향)
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean checkExactStones(int[][] board, int x, int y, int dx, int dy, int player, int exactCount) {
        int count = 0;
        while (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == player) {
            count++;
            x += dx;
            y += dy;
        }
        // 연속된 돌의 수가 정확히 exactCount와 같은 경우에만 true를 반환합니다.
        return count == exactCount;
    }
    
    // 보드를 평가하여 점수를 반환합니다.
    public static int evaluate(int[][] board) {
        
        return 0; // 가짜 구현
    }

    public static class Move {
        int row;
        int col;

        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return "(" + row + ", " + col + ")";
        }
    }
}
