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
 * MänguKasutajaliides on peamine mänguliides JavaFX-is.
 * Kuvab mängulauda, inforibasid ja nuppusid.
 * Töötleb nii hiire- kui klaviatuurisündmusi.
 * Laud skaleerub akna suuruse muutmisel automaatselt.
 */
public class MänguKasutajaliides {

    private Stage lava;
    private Mängulaud mängulaud;
    private String mängija1, mängija2;
    private int skoor1, skoor2;       // võitude skoorid

    private Button[][] lahtrid;       // 3x3 nupud mängulaual
    private Label olekuSilt;          // praegune mängija / tulemus
    private Label skooriSilt;         // skooride kuvamine
    private GridPane lauaVõrk;        // mängulauda hoidev paneel

    /**
     * Konstruktor laadib varasemad skoorid failist.
     * @param mängija1 esimese mängija nimi
     * @param mängija2 teise mängija nimi
     * @param lava     peaaken
     */
    public MänguKasutajaliides(String mängija1, String mängija2, Stage lava) {
        this.mängija1 = mängija1;
        this.mängija2 = mängija2;
        this.lava = lava;
        this.mängulaud = new Mängulaud();
        // Lae varasemad skoorid failist
        this.skoor1 = FailiHaldur.laadiSkoor(mängija1);
        this.skoor2 = FailiHaldur.laadiSkoor(mängija2);
    }

    /**
     * Kuvab mänguakna koos kõigi elementidega.
     */
    public void näita() {
        // Seisu riba ülaosas
        olekuSilt = new Label(mängija1 + " (X) käik");
        olekuSilt.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        skooriSilt = new Label(getSkooriTekst());
        skooriSilt.setStyle("-fx-font-size: 13px;");

        // Mängulaud – 3x3 nupud
        lauaVõrk = new GridPane();
        lauaVõrk.setAlignment(Pos.CENTER);
        lauaVõrk.setHgap(5);
        lauaVõrk.setVgap(5);
        lahtrid = new Button[3][3];

        for (int rida = 0; rida < 3; rida++) {
            for (int veerg = 0; veerg < 3; veerg++) {
                Button nupp = new Button(" ");
                nupp.setFont(Font.font("Arial", 40));
                nupp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(nupp, Priority.ALWAYS);
                GridPane.setVgrow(nupp, Priority.ALWAYS);

                // Vajalik lambdas kasutamiseks (effectively final)
                final int ridaF = rida, veergF = veerg;
                // Hiire sündmus: klõps lahtril
                nupp.setOnAction(e -> töötleLahtriKlõpsu(ridaF, veergF));
                lahtrid[rida][veerg] = nupp;
                lauaVõrk.add(nupp, veerg, rida);
            }
        }

        // Nupuriba allosas
        Button tagasivõtuNupp = new Button("↩ Võta tagasi");
        Button uusMänguNupp = new Button("🔄 Uus mäng");
        Button logiNupp = new Button("📋 Vaata logi");

        tagasivõtuNupp.setOnAction(e -> töötleTagasivõtmist());
        uusMänguNupp.setOnAction(e -> töötleUutMängu());
        logiNupp.setOnAction(e -> näitaLogi());

        HBox nupuriba = new HBox(10, tagasivõtuNupp, uusMänguNupp, logiNupp);
        nupuriba.setAlignment(Pos.CENTER);
        nupuriba.setPadding(new Insets(10, 0, 0, 0));

        // Peapaigutus
        BorderPane juur = new BorderPane();
        juur.setPadding(new Insets(15));

        VBox ülemineRiba = new VBox(5, olekuSilt, skooriSilt);
        ülemineRiba.setAlignment(Pos.CENTER);
        juur.setTop(ülemineRiba);

        // Paneel mis venitab mängulauda
        StackPane lauaPaneel = new StackPane(lauaVõrk);
        BorderPane.setMargin(lauaPaneel, new Insets(15, 0, 15, 0));
        juur.setCenter(lauaPaneel);
        juur.setBottom(nupuriba);

        // Venitame lauda vastavalt paneeli suurusele
        lauaPaneel.widthProperty().addListener((obs, oldW, newW) -> muudaLaudaSuurust(lauaPaneel));
        lauaPaneel.heightProperty().addListener((obs, oldH, newH) -> muudaLaudaSuurust(lauaPaneel));

        Scene stseen = new Scene(juur, 420, 500);

        // Klaviatuuri otsetee: Ctrl+Z = käigu tagasivõtmine
        stseen.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
                this::töötleTagasivõtmist
        );

        lava.setTitle("Ristkülik-Nullmäng");
        lava.setScene(stseen);
        lava.setMinWidth(300);
        lava.setMinHeight(350);
        lava.show();
    }

    /**
     * Kohandab lahtri nuppude suurust vastavalt akna mõõtmetele.
     */
    private void muudaLaudaSuurust(StackPane paneel) {
        double suurus = Math.min(paneel.getWidth(), paneel.getHeight()) / 3 - 10;
        if (suurus < 60) suurus = 60;
        for (Button[] rida : lahtrid) {
            for (Button nupp : rida) {
                nupp.setPrefSize(suurus, suurus);
                nupp.setFont(Font.font("Arial", suurus * 0.45));
            }
        }
    }

    /**
     * Töötleb lahtri klõpsamisel tekkinud sündmuse.
     */
    private void töötleLahtriKlõpsu(int rida, int veerg) {
        if (mängulaud.kontrolliVõitjat() != ' ' || mängulaud.onLaudTäis()) return;

        boolean liigutusTehtud = mängulaud.teeKäik(rida, veerg);
        if (!liigutusTehtud) return; // lahter täis, ignoreeri

        // Uuenda nupu tekst
        char märk = mängulaud.getLaud()[rida][veerg];
        lahtrid[rida][veerg].setText(String.valueOf(märk));
        lahtrid[rida][veerg].setStyle(märk == 'X'
                ? "-fx-text-fill: #1565C0; -fx-font-weight: bold;"
                : "-fx-text-fill: #B71C1C; -fx-font-weight: bold;");

        // Kontrolli mängu lõppu
        char võitja = mängulaud.kontrolliVõitjat();
        if (võitja != ' ') {
            String võitjaNimi = (võitja == 'X') ? mängija1 : mängija2;
            olekuSilt.setText("🎉 " + võitjaNimi + " võitis!");
            // Uuenda ja salvesta skoorid
            if (võitja == 'X') skoor1++;
            else skoor2++;
            FailiHaldur.salvestaSkoor(mängija1, skoor1);
            FailiHaldur.salvestaSkoor(mängija2, skoor2);
            skooriSilt.setText(getSkooriTekst());
            // Salvesta logi
            FailiHaldur.salvestaMänguLogi(mängija1, mängija2, võitja,
                    mängulaud.getAjalugu().toLogiString());
            keelustaLaud();
        } else if (mängulaud.onLaudTäis()) {
            olekuSilt.setText("🤝 Viik! Keegi ei võitnud.");
            FailiHaldur.salvestaMänguLogi(mängija1, mängija2, ' ',
                    mängulaud.getAjalugu().toLogiString());
        } else {
            // Järgmise mängija kord
            char järgmine = mängulaud.getPraeguneMängija();
            String järgmiseNimi = (järgmine == 'X') ? mängija1 : mängija2;
            olekuSilt.setText(järgmiseNimi + " (" + järgmine + ") käik");
        }
    }

    /**
     * Töötleb käigu tagasivõtmise (undo).
     */
    private void töötleTagasivõtmist() {
        if (mängulaud.kontrolliVõitjat() != ' ') return; // mäng läbi, ei saa tagasi võtta
        boolean ok = mängulaud.võtaKäikTagasi();
        if (!ok) {
            olekuSilt.setText("Pole midagi tagasi võtta!");
            return;
        }
        // Uuenda laud visuaalselt
        char[][] laud = mängulaud.getLaud();
        for (int rida = 0; rida < 3; rida++) {
            for (int veerg = 0; veerg < 3; veerg++) {
                if (laud[rida][veerg] == ' ') {
                    lahtrid[rida][veerg].setText(" ");
                    lahtrid[rida][veerg].setStyle("");
                    lahtrid[rida][veerg].setDisable(false);
                }
            }
        }
        char praegune = mängulaud.getPraeguneMängija();
        String praeguseNimi = (praegune == 'X') ? mängija1 : mängija2;
        olekuSilt.setText(praeguseNimi + " (" + praegune + ") käik");
    }

    /**
     * Alustab uut mängu ilma mängijate nimesid muutmata.
     */
    private void töötleUutMängu() {
        mängulaud.lähtesta();
        for (Button[] rida : lahtrid) {
            for (Button nupp : rida) {
                nupp.setText(" ");
                nupp.setStyle("");
                nupp.setDisable(false);
            }
        }
        olekuSilt.setText(mängija1 + " (X) käik");
        skooriSilt.setText(getSkooriTekst());
    }

    /**
     * Kuvab logifaili viimased kirjed eraldi dialoogis.
     */
    private void näitaLogi() {
        String logi = FailiHaldur.loeViimasedRead(30);
        Alert teade = new Alert(Alert.AlertType.INFORMATION);
        teade.setTitle("Mängu logi");
        teade.setHeaderText("Viimased mängud:");
        TextArea tekstiAla = new TextArea(logi);
        tekstiAla.setEditable(false);
        tekstiAla.setWrapText(true);
        tekstiAla.setPrefSize(380, 300);
        teade.getDialogPane().setContent(tekstiAla);
        teade.showAndWait();
    }

    /**
     * Blokeerib kõik lahtrid pärast mängu lõppu.
     */
    private void keelustaLaud() {
        for (Button[] rida : lahtrid)
            for (Button nupp : rida)
                nupp.setDisable(true);
    }

    /**
     * Tagastab skooriribale kuvatava teksti.
     */
    private String getSkooriTekst() {
        return mängija1 + ": " + skoor1 + " võitu   |   " + mängija2 + ": " + skoor2 + " võitu";
    }
}