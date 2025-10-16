package tic.tac.toe.game;

public interface GameLogic {
    boolean makeMove(int row, int col, Player player);
    boolean isGameOver();
    Player checkWinner();
}