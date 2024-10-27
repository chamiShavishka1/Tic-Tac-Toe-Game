package lk.ijse.gdse68.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lk.ijse.gdse68.service.*;

public class BoardController implements BoardUI {

    private static final int GRID_SIZE = 3;
    private String currentPlayer = "X";
    private String playerName;
    private AIPlayer aiPlayer;
    private HumanPlayer humanPlayer;

    @FXML
    private JFXButton btnPlayAgain;

    @FXML
    private VBox col0, col1, col2;

    @FXML
    private Group grpCols;

    @FXML
    private Label lblStatus;

    @FXML
    private Pane pneOver;

    @FXML
    private AnchorPane root;

    private Pane[][] grid;
    private boolean isGameOver;
    private boolean isAiPlaying = false;

    @FXML
    public void initialize() {
        createGrid();
        initializeGame();
        isGameOver = false;
    }

    public void initData(String playerName) {
        this.playerName = playerName;
        lblStatus.setText(playerName + " is playing...");
    }

    private void initializeGame() {
        Board board = new BoardImpl(this);
        humanPlayer = new HumanPlayer(board);
        aiPlayer = new AIPlayer(board);
    }

    private void createGrid() {
        grid = new Pane[GRID_SIZE][GRID_SIZE];
        VBox[] columns = {col0, col1, col2};

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Pane cell = createCell();
                columns[i].getChildren().add(cell);
                grid[i][j] = cell;
            }
        }
    }

    private Pane createCell() {
        Pane cell = new Pane();
        cell.setMinSize(100, 100);
        cell.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        cell.setOnMouseEntered(event -> handleMouseEnter(cell));
        cell.setOnMouseExited(event -> handleMouseExit(cell));
        cell.setOnMouseClicked(this::handleCellClick);
        return cell;
    }

    private void handleMouseEnter(Pane cell) {
        if (!isGameOver && cell.getChildren().isEmpty()) {
            cell.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 2;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), cell);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        }
    }

    private void handleMouseExit(Pane cell) {
        if (!isGameOver && cell.getChildren().isEmpty()) {
            cell.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 2;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), cell);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        }
    }

    int row = -1, col = -1;
    private void handleCellClick(MouseEvent event) {
        Pane cell = (Pane) event.getSource();
        row = -1;
        col = -1;

        outerLoop:
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == cell) {
                    row = i;
                    col = j;
                    break outerLoop;
                }
            }
        }

        if (!isGameOver && cell.getChildren().isEmpty()) {
            humanPlayer.move(row, col);
            if (!isGameOver) {
                Platform.runLater(() -> {
                    int[] bestMove = aiPlayer.getBestMove();
                    aiPlayer.move(bestMove[0], bestMove[1]);
                });
            }
        }
    }

    @Override
    public void update(int row, int col, boolean isHuman) {
        if (isGameOver) return;
        Pane cell = grid[row][col];

        if (cell.getChildren().isEmpty()) {
            Label label = new Label(currentPlayer);
            label.setStyle("-fx-font-size: 36px; -fx-text-fill: black;");
            cell.getChildren().add(label);

            checkWinner();

            if (!isGameOver) {
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                lblStatus.setText("Current Player: " + (isHuman ? playerName : "AI"));
            }
        }
    }

    public void checkWinner() {
        String winner = null;
        for (int row = 0; row < GRID_SIZE; row++) {
            if (checkLine(grid[row][0], grid[row][1], grid[row][2])) {
                winner = ((Label) grid[row][0].getChildren().get(0)).getText();
            }
        }

        for (int col = 0; col < GRID_SIZE; col++) {
            if (checkLine(grid[0][col], grid[1][col], grid[2][col])) {
                winner = ((Label) grid[0][col].getChildren().get(0)).getText();
            }
        }

        if (checkLine(grid[0][0], grid[1][1], grid[2][2]) || checkLine(grid[0][2], grid[1][1], grid[2][0])) {
            winner = ((Label) grid[1][1].getChildren().get(0)).getText();
        }

        if (winner != null) {
            notifyWinner(winner.equals("X") ? Piece.X : Piece.O);
        } else if (isBoardFull()) {
            notifyWinner(Piece.EMPTY);
        }
    }

    private boolean checkLine(Pane cell1, Pane cell2, Pane cell3) {
        if (!cell1.getChildren().isEmpty() && !cell2.getChildren().isEmpty() && !cell3.getChildren().isEmpty()) {
            String text1 = ((Label) cell1.getChildren().get(0)).getText();
            String text2 = ((Label) cell2.getChildren().get(0)).getText();
            String text3 = ((Label) cell3.getChildren().get(0)).getText();
            return text1.equals(text2) && text2.equals(text3);
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].getChildren().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void notifyWinner(Piece winningPiece) {
        isGameOver = true;
        lblStatus.getStyleClass().clear();
        lblStatus.getStyleClass().add("final");

        if (winningPiece == Piece.X) {
            lblStatus.setText(playerName + ", you have won the game!");
        } else if (winningPiece == Piece.O) {
            lblStatus.setText("Game over, AI has won the game!");
        } else {
            lblStatus.setText("It's a tie!");
        }

        pneOver.setVisible(true);
        pneOver.toFront();
        Platform.runLater(btnPlayAgain::requestFocus);
    }

    @FXML
    void btnPlayAgainOnAction(ActionEvent event) {
        resetBoard();
    }

    private void resetBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j].getChildren().clear();
                grid[i][j].setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 2;");
            }
        }

        currentPlayer = "X";
        isGameOver = false;

        lblStatus.setText(playerName + ", it's your turn");
        pneOver.setVisible(false);
        initializeGame();
    }
}