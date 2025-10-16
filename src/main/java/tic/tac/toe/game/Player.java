package tic.tac.toe.game;

public enum Player {
    X, O, EMPTY;

    public Player opposite() {
        return this == X ? O : (this == O ? X : EMPTY);
    }
}