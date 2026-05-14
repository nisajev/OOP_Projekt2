package com.example.demo;

/**
 * Mängulaud – sisaldab kogu mängu loogikat.
 * Haldab mängulauda, kontrollib võitjat ja viiki,
 * ning toetab käikude tagasivõtmist.
 */
public class Mängulaud {

    private char[][] laud;           // 3x3 mängulaud
    private char praeguneMängija;    // praegune mängija ('X' või 'O')
    private KäiguAjalugu ajalugu;    // käikude ajalugu

    /**
     * Konstruktor – loob tühja mängulauda ja alustab mängijaga X.
     */
    public Mängulaud() {
        laud = new char[3][3];
        praeguneMängija = 'X';
        ajalugu = new KäiguAjalugu();
        initsialiseeriLaud();
    }

    /**
     * Täidab kõik lahtrid tühimärgiga.
     */
    private void initsialiseeriLaud() {
        for (int rida = 0; rida < 3; rida++)
            for (int veerg = 0; veerg < 3; veerg++)
                laud[rida][veerg] = ' ';
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
        for (int rida = 0; rida < 3; rida++) {
            if (laud[rida][0] != ' ' && laud[rida][0] == laud[rida][1] && laud[rida][1] == laud[rida][2])
                return laud[rida][0];
        }
        // Kontrolli veerge
        for (int veerg = 0; veerg < 3; veerg++) {
            if (laud[0][veerg] != ' ' && laud[0][veerg] == laud[1][veerg] && laud[1][veerg] == laud[2][veerg])
                return laud[0][veerg];
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
        for (int rida = 0; rida < 3; rida++)
            for (int veerg = 0; veerg < 3; veerg++)
                if (laud[rida][veerg] == ' ') return false;
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