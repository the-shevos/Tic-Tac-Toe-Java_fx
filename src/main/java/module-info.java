module lk.ijse.gdse.tictactoegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens tic.tac.toe.game to javafx.fxml;
    exports tic.tac.toe.game;
}