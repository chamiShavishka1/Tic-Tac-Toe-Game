package lk.ijse.gdse68.service;

public class BoardImpl implements Board {
    private final BoardUI boardUI;
    private final int SIZE = 3;
    private Piece[][] board;

    public BoardImpl(BoardUI boardUI) {
        this.boardUI = boardUI;
        board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    @Override
    public BoardUI getBoardUI() {
        return this.boardUI;
    }

    @Override
    public void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
    }

    @Override
    public boolean isLegalMove(int row, int col) {
        return board[row][col] == Piece.EMPTY;
    }

    @Override
    public void updateMove(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    public void resetBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                updateMove(row, col, Piece.EMPTY);
            }
        }
    }

    @Override
    public Winner checkWinner() {
        return null;
    }

    @Override
    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public int[][] getState() {
        int[][] state = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == Piece.EMPTY) {
                    state[i][j] = 0;
                } else if (board[i][j] == Piece.X) {
                    state[i][j] = 1;
                } else {
                    state[i][j] = -1;
                }
            }
        }
        return state;
    }
}