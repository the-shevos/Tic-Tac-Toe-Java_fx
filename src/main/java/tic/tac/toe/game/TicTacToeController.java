package tic.tac.toe.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TicTacToeController {

    private final TicTacToeBoard board = new TicTacToeBoard();
    private final Button[][] buttons = new Button[3][3];
    private final Label status = new Label("You are (X)");
    private Line winLine;
    private Pane animationLayer;

    public void start(Stage stage) {
        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.web("#e8e8ed"), CornerRadii.EMPTY, Insets.EMPTY)));

        animationLayer = new Pane();
        animationLayer.setMouseTransparent(true);

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setMaxWidth(500);
        container.setMinWidth(350);

        Label title = new Label("Tic Tac Toe");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#2e2e2e"));

        status.setFont(Font.font("Poppins", FontWeight.MEDIUM, 20));
        status.setTextFill(Color.web("#555"));

        GridPane grid = createResponsiveGrid();

        Button restart = new Button("New Game");
        restart.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        restart.setPrefWidth(200);
        restart.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);"
                + "-fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;");
        restart.setOnMouseEntered(e -> restart.setStyle("-fx-background-color: linear-gradient(to right, #5a67d8, #6b46c1);"
                + "-fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;"));
        restart.setOnMouseExited(e -> restart.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);"
                + "-fx-text-fill: white; -fx-background-radius: 12; -fx-cursor: hand;"));
        restart.setOnAction(e -> resetGame());

        container.getChildren().addAll(title, status, grid, restart);
        VBox.setVgrow(grid, Priority.ALWAYS);
        root.getChildren().addAll(container, animationLayer);

        Scene scene = new Scene(root, 800, 800);

        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe - Human vs AI");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();

        grid.prefWidthProperty().bind(container.widthProperty());
        grid.prefHeightProperty().bind(container.widthProperty());
    }

    private GridPane createResponsiveGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33.33);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(33.33);
        for (int i = 0; i < 3; i++) {
            grid.getColumnConstraints().add(cc);
            grid.getRowConstraints().add(rc);
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = createGameButton();
                final int row = r, col = c;
                btn.setOnAction(e -> handleMove(row, col));
                buttons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        return grid;
    }

    private Button createGameButton() {
        Button btn = new Button();
        btn.setFont(Font.font("Poppins", FontWeight.BOLD, 48));
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setStyle(baseButtonStyle());
        btn.setOnMouseEntered(e -> { if (btn.getText().isEmpty()) btn.setStyle(hoverButtonStyle()); });
        btn.setOnMouseExited(e -> { if (btn.getText().isEmpty()) btn.setStyle(baseButtonStyle()); });
        return btn;
    }

    private String baseButtonStyle() {
        return "-fx-background-color: white; -fx-border-color: #0c0c0d; -fx-border-width: 2;"
                + "-fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    }

    private String hoverButtonStyle() {
        return "-fx-background-color: #b4b4b8; -fx-border-color: #0c0c0d; -fx-border-width: 2;"
                + "-fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    }

    private void handleMove(int row, int col) {
        if (board.isGameOver() || !board.makeMove(row, col, Player.X)) return;

        updateBoard();

        if (board.isGameOver()) {
            showWinnerLine();
            disableAllButtons();
            return;
        }

        status.setText("AI is thinking...");
        PauseTransition pause = new PauseTransition(Duration.millis(450));
        pause.setOnFinished(e -> aiMove());
        pause.play();
    }

    private void aiMove() {
        if (board.isGameOver()) return;

        int[] move = findBestMove();
        if (move != null) {
            board.makeMove(move[0], move[1], Player.O);
            updateBoard();
        }

        if (board.isGameOver()) {
            showWinnerLine();
            disableAllButtons();
        } else {
            status.setText("Your turn (X)");
        }
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        for (int[] move : board.getAvailableMoves()) {
            TicTacToeBoard copy = board.copy();
            copy.makeMove(move[0], move[1], Player.O);
            int score = minimax(copy, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(TicTacToeBoard state, boolean isMaximizing) {
        Player winner = state.checkWinner();
        if (winner == Player.O) return 10;
        if (winner == Player.X) return -10;
        if (state.isFull()) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] move : state.getAvailableMoves()) {
                TicTacToeBoard copy = state.copy();
                copy.makeMove(move[0], move[1], Player.O);
                best = Math.max(best, minimax(copy, false));
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] move : state.getAvailableMoves()) {
                TicTacToeBoard copy = state.copy();
                copy.makeMove(move[0], move[1], Player.X);
                best = Math.min(best, minimax(copy, true));
            }
            return best;
        }
    }

    private void updateBoard() {
        Player[][] cells = board.getBoard();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Player p = cells[r][c];
                Button btn = buttons[r][c];
                btn.setText(p == Player.EMPTY ? "" : (p == Player.X ? "X" : "O"));
                if (p != Player.EMPTY) {
                    btn.setDisable(true);
                    btn.setStyle("-fx-background-color: " + (p == Player.X ? "#abb5c4" : "#ebe0b5") +
                            "; -fx-border-color: #232324; -fx-border-width: 2; -fx-background-radius: 10;");
                }
            }
        }
    }

    private void showWinnerLine() {
        Player winner = board.checkWinner();
        if (winner == Player.EMPTY) {
            status.setText("Draw!");
            return;
        }

        status.setText((winner == Player.X ? "You" : "AI") + " Win!");

        int[] start = new int[2], end = new int[2];
        Player[][] cells = board.getBoard();

        for (int r = 0; r < 3; r++)
            if (cells[r][0] == winner && cells[r][1] == winner && cells[r][2] == winner)
            { start[0] = r; start[1] = 0; end[0] = r; end[1] = 2; }
        for (int c = 0; c < 3; c++)
            if (cells[0][c] == winner && cells[1][c] == winner && cells[2][c] == winner)
            { start[0] = 0; start[1] = c; end[0] = 2; end[1] = c; }
        if (cells[0][0] == winner && cells[1][1] == winner && cells[2][2] == winner)
        { start[0] = 0; start[1] = 0; end[0] = 2; end[1] = 2; }
        if (cells[0][2] == winner && cells[1][1] == winner && cells[2][0] == winner)
        { start[0] = 0; start[1] = 2; end[0] = 2; end[1] = 0; }

        Button startBtn = buttons[start[0]][start[1]];
        Button endBtn = buttons[end[0]][end[1]];

        double startX = Bindings.createDoubleBinding(
                () -> animationLayer.sceneToLocal(startBtn.localToScene(startBtn.getWidth() / 2, startBtn.getHeight() / 2)).getX(),
                startBtn.localToSceneTransformProperty()).get();

        double startY = Bindings.createDoubleBinding(
                () -> animationLayer.sceneToLocal(startBtn.localToScene(startBtn.getWidth() / 2, startBtn.getHeight() / 2)).getY(),
                startBtn.localToSceneTransformProperty()).get();

        double endX = Bindings.createDoubleBinding(
                () -> animationLayer.sceneToLocal(endBtn.localToScene(endBtn.getWidth() / 2, endBtn.getHeight() / 2)).getX(),
                endBtn.localToSceneTransformProperty()).get();

        double endY = Bindings.createDoubleBinding(
                () -> animationLayer.sceneToLocal(endBtn.localToScene(endBtn.getWidth() / 2, endBtn.getHeight() / 2)).getY(),
                endBtn.localToSceneTransformProperty()).get();

        winLine = new Line(startX, startY, startX, startY);
        winLine.setStroke(Color.web("#e87368"));
        winLine.setStrokeWidth(6);
        animationLayer.getChildren().add(winLine);

        Timeline draw = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(winLine.endXProperty(), startX),
                        new KeyValue(winLine.endYProperty(), startY)),
                new KeyFrame(Duration.seconds(0.8),
                        new KeyValue(winLine.endXProperty(), endX),
                        new KeyValue(winLine.endYProperty(), endY))
        );
        draw.play();
    }

    private void disableAllButtons() {
        for (Button[] row : buttons)
            for (Button b : row)
                b.setDisable(true);
    }

    private void resetGame() {
        board.reset();
        animationLayer.getChildren().clear();
        for (Button[] row : buttons)
            for (Button b : row) {
                b.setText("");
                b.setDisable(false);
                b.setStyle(baseButtonStyle());
            }
        status.setText("You are (X)");
    }
}