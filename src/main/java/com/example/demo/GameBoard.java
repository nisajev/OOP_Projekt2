package com.example.demo;

/**
 * GameBoard – sisaldab kogu mängu loogikat.
 * Haldab mängulauda, kontrollib võitjat ja viiki,
 * ning toetab käikude tagasivõtmist.
 */
public class GameBoard {

    private char[][] board;       // 3x3 mängulaud
    private char currentPlayer;   // praegune mängija ('X' või 'O')
    private MoveHistory history;  // käikude ajalugu

    /**
     * Konstruktor – loob tühja mängulauda ja alustab mängijaga X.
     */
    public GameBoard() {
        board = new char[3][3];
        currentPlayer = 'X';
        history = new MoveHistory();
        initBoard();
    }

    /**
     * Täidab kõik lahtrid tühimärgiga.
     */
    private void initBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = ' ';
    }

    /**
     * Teeb käigu antud rea ja veeru koordinaatidel.
     * @param row  rida (0-2)
     * @param col  veerg (0-2)
     * @return true kui käik õnnestus, false kui lahter on juba täidetud
     * @throws IllegalArgumentException kui koordinaadid on vahemikust väljas
     */
    public boolean makeMove(int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            throw new IllegalArgumentException("Koordinaadid peavad olema vahemikus 0-2!");
        }
        if (board[row][col] != ' ') {
            return false; // lahter on juba täidetud
        }
        board[row][col] = currentPlayer;
        history.addMove(row, col, currentPlayer);
        switchPlayer();
        return true;
    }

    /**
     * Võtab viimase käigu tagasi.
     * @return true kui tagasivõtmine õnnestus, false kui ajalugu on tühi
     */
    public boolean undoMove() {
        int[] last = history.removeLastMove();
        if (last == null) return false;
        board[last[0]][last[1]] = ' ';
        switchPlayer(); // lülita mängija tagasi
        return true;
    }

    /**
     * Vahetab praegust mängijat.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    /**
     * Kontrollib, kas on võitja.
     * @return võitja märk ('X' või 'O') või ' ' kui võitjat pole
     */
    public char checkWinner() {
        // Kontrolli ridu
        for (int r = 0; r < 3; r++) {
            if (board[r][0] != ' ' && board[r][0] == board[r][1] && board[r][1] == board[r][2])
                return board[r][0];
        }
        // Kontrolli veerge
        for (int c = 0; c < 3; c++) {
            if (board[0][c] != ' ' && board[0][c] == board[1][c] && board[1][c] == board[2][c])
                return board[0][c];
        }
        // Kontrolli diagonaale
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];
        return ' '; // võitjat pole
    }

    /**
     * Kontrollib, kas laud on täis (viik).
     * @return true kui kõik lahtrid on täidetud
     */
    public boolean isBoardFull() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == ' ') return false;
        return true;
    }

    /**
     * Lähtestab mängu algseisu.
     */
    public void reset() {
        initBoard();
        currentPlayer = 'X';
        history.clear();
    }

    public char getCurrentPlayer() { return currentPlayer; }
    public char[][] getBoard()      { return board; }
    public MoveHistory getHistory() { return history; }
}
