package tic.tac.toe.game;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeBoard implements GameLogic {
    private final Player[][] board = new Player[3][3];

    public TicTacToeBoard() {
        reset();
    }

    public void reset() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = Player.EMPTY;
    }

    @Override
    public boolean makeMove(int row, int col, Player player) {
        if (board[row][col] == Player.EMPTY) {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public boolean isFull() {
        for (Player[] row : board)
            for (Player cell : row)
                if (cell == Player.EMPTY) return false;
        return true;
    }

    @Override
    public boolean isGameOver() {
        return checkWinner() != Player.EMPTY || isFull();
    }

    @Override
    public Player checkWinner() {

        for (int i = 0; i < 3; i++) {
            if (board[i][0] != Player.EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                return board[i][0];
            if (board[0][i] != Player.EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i])
                return board[0][i];
        }

        if (board[0][0] != Player.EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];
        if (board[0][2] != Player.EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];

        return Player.EMPTY;
    }

    public Player[][] getBoard() {
        return board;
    }

    public List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == Player.EMPTY)
                    moves.add(new int[]{r, c});
        return moves;
    }

    public TicTacToeBoard copy() {
        TicTacToeBoard copy = new TicTacToeBoard();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                copy.board[r][c] = this.board[r][c];
        return copy;
    }
}