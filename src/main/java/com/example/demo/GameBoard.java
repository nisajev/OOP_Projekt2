package com.example.demo;

/**
 * Mängulaud – sisaldab kogu mängu loogikat.
 * Haldab mängulauda, kontrollib võitjat ja viiki,
 * ning toetab käikude tagasivõtmist.
 */
public class GameBoard {

    private char[][] laud;           // 3x3 mängulaud
    private char praeguneMängija;    // praegune mängija ('X' või 'O')
    private KäiguAjalugu ajalugu;    // käikude ajalugu

    /**
     * Konstruktor – loob tühja mängulauda ja alustab mängijaga X.
     */
    public GameBoard() {
        laud = new char[3][3];
        praeguneMängija = 'X';
        ajalugu = new KäiguAjalugu();
        initsialiseeriLaud();
    }

    /**
     * Täidab kõik lahtrid tühimärgiga.
     */
    private void initsialiseeriLaud() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                laud[r][c] = ' ';
    }

    /**
     * Teeb käigu antud rea ja veeru koordinaatidel.
     * @param rida  rida (0-2)
     * @param veerg  veerg (0-2)
     * @return true kui käik õnnestus, false kui lahter on juba täidetud
     * @throws IllegalArgumentException kui koordinaadid on vahemikust väljas
     */
    public boolean teeKäik(int rida, int veerg) {
        if (rida < 0 || rida > 2 || veerg < 0 || veerg > 2) {
            throw new IllegalArgumentException("Koordinaadid peavad olema vahemikus 0-2!");
        }
        if (laud[rida][veerg] != ' ') {
            return false; // lahter on juba täidetud
        }
        laud[rida][veerg] = praeguneMängija;
        ajalugu.lisaKäik(rida, veerg, praeguneMängija);
        vahetaMängijat();
        return true;
    }

    /**
     * Võtab viimase käigu tagasi.
     * @return true kui tagasivõtmine õnnestus, false kui ajalugu on tühi
     */
    public boolean võtaKäikTagasi() {
        int[] viimane = ajalugu.eemaldaViimaneKäik();
        if (viimane == null) return false;
        laud[viimane[0]][viimane[1]] = ' ';
        vahetaMängijat(); // lülita mängija tagasi
        return true;
    }

    /**
     * Vahetab praegust mängijat.
     */
    private void vahetaMängijat() {
        praeguneMängija = (praeguneMängija == 'X') ? 'O' : 'X';
    }

    /**
     * Kontrollib, kas on võitja.
     * @return võitja märk ('X' või 'O') või ' ' kui võitjat pole
     */
    public char kontrolliVõitjat() {
        // Kontrolli ridu
        for (int r = 0; r < 3; r++) {
            if (laud[r][0] != ' ' && laud[r][0] == laud[r][1] && laud[r][1] == laud[r][2])
                return laud[r][0];
        }
        // Kontrolli veerge
        for (int c = 0; c < 3; c++) {
            if (laud[0][c] != ' ' && laud[0][c] == laud[1][c] && laud[1][c] == laud[2][c])
                return laud[0][c];
        }
        // Kontrolli diagonaale
        if (laud[0][0] != ' ' && laud[0][0] == laud[1][1] && laud[1][1] == laud[2][2])
            return laud[0][0];
        if (laud[0][2] != ' ' && laud[0][2] == laud[1][1] && laud[1][1] == laud[2][0])
            return laud[0][2];
        return ' '; // võitjat pole
    }

    /**
     * Kontrollib, kas laud on täis (viik).
     * @return true kui kõik lahtrid on täidetud
     */
    public boolean onLaudTäis() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (laud[r][c] == ' ') return false;
        return true;
    }

    /**
     * Lähtestab mängu algseisu.
     */
    public void lähtesta() {
        initsialiseeriLaud();
        praeguneMängija = 'X';
        ajalugu.tühjenda();
    }

    public char getPraeguneMängija() { return praeguneMängija; }
    public char[][] getLaud()        { return laud; }
    public KäiguAjalugu getAjalugu() { return ajalugu; }
}
