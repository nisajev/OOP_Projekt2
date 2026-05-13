package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * GameUI – peamine mänguliides JavaFX-is.
 * Kuvab mängulauda, inforibasid ja nuppusid.
 * Töötleb nii hiire- kui klaviatuurisündmusi.
 * Laud skaleerub akna suuruse muutmisel automaatselt.
 */
public class GameUI {

    private Stage stage;
    private GameBoard gameBoard;
    private String player1, player2;
    private int score1, score2;       // võitude skoorid

    private Button[][] cells;         // 3x3 nupud mängulaual
    private Label statusLabel;        // praegune mängija / tulemus
    private Label scoreLabel;         // skooride kuvamine
    private GridPane boardGrid;       // mängulauda hoidev paneel

    /**
     * Konstruktor – laadib varasemad skoorid failist.
     * @param player1 esimese mängija nimi
     * @param player2 teise mängija nimi
     * @param stage   peaaken
     */
    public GameUI(String player1, String player2, Stage stage) {
        this.player1 = player1;
        this.player2 = player2;
        this.stage = stage;
        this.gameBoard = new GameBoard();
        // Lae varasemad skoorid failist
        this.score1 = FileManager.loadScore(player1);
        this.score2 = FileManager.loadScore(player2);
    }

    /**
     * Kuvab mänguakna koos kõigi elementidega.
     */
    public void show() {
        // Seisu riba ülaosas
        statusLabel = new Label(player1 + " (X) käik");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        scoreLabel = new Label(getScoreText());
        scoreLabel.setStyle("-fx-font-size: 13px;");

        // Mängulaud – 3x3 nupud
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        cells = new Button[3][3];

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = new Button(" ");
                btn.setFont(Font.font("Arial", 40));
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(btn, Priority.ALWAYS);
                GridPane.setVgrow(btn, Priority.ALWAYS);

                final int row = r, col = c;
                // Hiire sündmus: klõps lahtril
                btn.setOnAction(e -> handleCellClick(row, col));
                cells[r][c] = btn;
                boardGrid.add(btn, c, r);
            }
        }

        // Nupuriba allosas
        Button undoBtn = new Button("↩ Võta tagasi");
        Button newGameBtn = new Button("🔄 Uus mäng");
        Button logBtn = new Button("📋 Vaata logi");

        undoBtn.setOnAction(e -> handleUndo());
        newGameBtn.setOnAction(e -> handleNewGame());
        logBtn.setOnAction(e -> showLog());

        HBox buttonBar = new HBox(10, undoBtn, newGameBtn, logBtn);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        // Peapaigutus
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox topBar = new VBox(5, statusLabel, scoreLabel);
        topBar.setAlignment(Pos.CENTER);
        root.setTop(topBar);

        // Paneel mis venitab mängulauda
        StackPane boardPane = new StackPane(boardGrid);
        BorderPane.setMargin(boardPane, new Insets(15, 0, 15, 0));
        root.setCenter(boardPane);
        root.setBottom(buttonBar);

        // Venitame lauda vastavalt paneeli suurusele
        boardPane.widthProperty().addListener((obs, oldW, newW) -> resizeBoard(boardPane));
        boardPane.heightProperty().addListener((obs, oldH, newH) -> resizeBoard(boardPane));

        Scene scene = new Scene(root, 420, 500);

        // Klaviatuuri otsetee: Ctrl+Z = käigu tagasivõtmine
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
            this::handleUndo
        );

        stage.setTitle("Ristkülik-Nullmäng");
        stage.setScene(scene);
        stage.setMinWidth(300);
        stage.setMinHeight(350);
        stage.show();
    }

    /**
     * Kohandab lahtri nuppude suurust vastavalt akna mõõtmetele.
     */
    private void resizeBoard(StackPane pane) {
        double size = Math.min(pane.getWidth(), pane.getHeight()) / 3 - 10;
        if (size < 60) size = 60;
        for (Button[] row : cells) {
            for (Button btn : row) {
                btn.setPrefSize(size, size);
                btn.setFont(Font.font("Arial", size * 0.45));
            }
        }
    }

    /**
     * Töötleb lahtri klõpsamisel tekkinud sündmuse.
     */
    private void handleCellClick(int row, int col) {
        if (gameBoard.checkWinner() != ' ' || gameBoard.isBoardFull()) return;

        boolean moved = gameBoard.makeMove(row, col);
        if (!moved) return; // lahter täis, ignoreeri

        // Uuenda nupu tekst
        char mark = gameBoard.getBoard()[row][col];
        cells[row][col].setText(String.valueOf(mark));
        cells[row][col].setStyle(mark == 'X'
            ? "-fx-text-fill: #1565C0; -fx-font-weight: bold;"
            : "-fx-text-fill: #B71C1C; -fx-font-weight: bold;");

        // Kontrolli mängu lõppu
        char winner = gameBoard.checkWinner();
        if (winner != ' ') {
            String winnerName = (winner == 'X') ? player1 : player2;
            statusLabel.setText("🎉 " + winnerName + " võitis!");
            // Uuenda ja salvesta skoorid
            if (winner == 'X') score1++;
            else score2++;
            FileManager.saveScore(player1, score1);
            FileManager.saveScore(player2, score2);
            scoreLabel.setText(getScoreText());
            // Salvesta logi
            FileManager.saveGameLog(player1, player2, winner,
                    gameBoard.getHistory().toLogString());
            disableBoard();
        } else if (gameBoard.isBoardFull()) {
            statusLabel.setText("🤝 Viik! Keegi ei võitnud.");
            FileManager.saveGameLog(player1, player2, ' ',
                    gameBoard.getHistory().toLogString());
        } else {
            // Järgmise mängija kord
            char next = gameBoard.getCurrentPlayer();
            String nextName = (next == 'X') ? player1 : player2;
            statusLabel.setText(nextName + " (" + next + ") käik");
        }
    }

    /**
     * Töötleb käigu tagasivõtmise (undo).
     */
    private void handleUndo() {
        if (gameBoard.checkWinner() != ' ') return; // mäng läbi, ei saa tagasi võtta
        boolean ok = gameBoard.undoMove();
        if (!ok) {
            statusLabel.setText("Pole midagi tagasi võtta!");
            return;
        }
        // Uuenda laud visuaalselt
        char[][] board = gameBoard.getBoard();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == ' ') {
                    cells[r][c].setText(" ");
                    cells[r][c].setStyle("");
                    cells[r][c].setDisable(false);
                }
            }
        }
        char curr = gameBoard.getCurrentPlayer();
        String currName = (curr == 'X') ? player1 : player2;
        statusLabel.setText(currName + " (" + curr + ") käik");
    }

    /**
     * Alustab uut mängu ilma mängijate nimesid muutmata.
     */
    private void handleNewGame() {
        gameBoard.reset();
        for (Button[] row : cells) {
            for (Button btn : row) {
                btn.setText(" ");
                btn.setStyle("");
                btn.setDisable(false);
            }
        }
        statusLabel.setText(player1 + " (X) käik");
        scoreLabel.setText(getScoreText());
    }

    /**
     * Kuvab logifaili viimased kirjed eraldi dialoogis.
     */
    private void showLog() {
        String log = FileManager.readLastLog(30);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mängu logi");
        alert.setHeaderText("Viimased mängud:");
        TextArea ta = new TextArea(log);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(380, 300);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }

    /**
     * Blokeerib kõik lahtrid pärast mängu lõppu.
     */
    private void disableBoard() {
        for (Button[] row : cells)
            for (Button btn : row)
                btn.setDisable(true);
    }

    /**
     * Tagastab skooriribale kuvatava teksti.
     */
    private String getScoreText() {
        return player1 + ": " + score1 + " võitu   |   " + player2 + ": " + score2 + " võitu";
    }
}
