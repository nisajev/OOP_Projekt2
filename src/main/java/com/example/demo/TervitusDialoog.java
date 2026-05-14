package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * WelcomeDialog – tervitusaken mängu alguses.
 * Küsib mängijate nimed ja kuvab lühikese kasutusjuhise.
 * Sisaldab erinditöötlust tühjade nimede kontrollimiseks.
 */
public class TervitusDialoog {

    /**
     * Kuvab tervitusdialoogi.
     * @param peaLava peaaken, mis pärast dialoogi aktiveeritakse
     */
    public void näita(Stage peaLava) {
        Stage dialoog = new Stage();
        dialoog.setTitle("Ristkülik-Nullmäng – Tere tulemast!");

        VBox juur = new VBox(15);
        juur.setPadding(new Insets(25));
        juur.setAlignment(Pos.CENTER);

        // Pealkiri
        Label pealkiri = new Label("❌ Ristkülik-Nullmäng ⭕");
        pealkiri.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Kasutusjuhis
        TextArea teave = new TextArea(
                "Mängu eesmärk: saada kolm oma märki (X või O) järjest " +
                        "– horisontaalselt, vertikaalselt või diagonaalselt.\n\n" +
                        "Juhised:\n" +
                        "  • Klõpsa lahtril, et teha käik\n" +
                        "  • Vajuta Ctrl+Z, et viimane käik tagasi võtta\n" +
                        "  • Mängijate nimed salvestatakse skoorifaili\n" +
                        "  • Iga mängu käigud salvestatakse logifaili"
        );
        teave.setEditable(false);
        teave.setWrapText(true);
        teave.setPrefHeight(150);
        teave.setStyle("-fx-font-size: 13px;");

        // Mängijate nimede sisestamine
        Label nimedeSilt = new Label("Sisesta mängijate nimed:");
        nimedeSilt.setStyle("-fx-font-weight: bold;");

        GridPane nimedeVõrk = new GridPane();
        nimedeVõrk.setHgap(10);
        nimedeVõrk.setVgap(8);
        nimedeVõrk.setAlignment(Pos.CENTER);

        TextField m1Väli = new TextField();
        m1Väli.setPromptText("Mängija 1 nimi (X)");

        TextField m2Väli = new TextField();
        m2Väli.setPromptText("Mängija 2 nimi (O)");

        nimedeVõrk.add(new Label("Mängija X:"), 0, 0);
        nimedeVõrk.add(m1Väli, 1, 0);
        nimedeVõrk.add(new Label("Mängija O:"), 0, 1);
        nimedeVõrk.add(m2Väli, 1, 1);

        // Veateate silt
        Label veaTeade = new Label("");
        veaTeade.setStyle("-fx-text-fill: red;");

        // Alusta mängu nupp
        Button algusNupp = new Button("Alusta mängu!");
        algusNupp.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
        algusNupp.setDefaultButton(true);

        algusNupp.setOnAction(e -> {
            try {
                String nimi1 = m1Väli.getText().trim();
                String nimi2 = m2Väli.getText().trim();

                // Erinditöötlus: nimed ei tohi olla tühjad
                if (nimi1.isEmpty() || nimi2.isEmpty()) {
                    throw new IllegalArgumentException("Mõlemad mängijate nimed peavad olema täidetud!");
                }
                // Nimed peavad olema erinevad
                if (nimi1.equalsIgnoreCase(nimi2)) {
                    throw new IllegalArgumentException("Mängijate nimed peavad olema erinevad!");
                }

                // Sulge dialoog ja ava mänguaken
                dialoog.close();
                MänguKasutajaliides mänguLiides = new MänguKasutajaliides(nimi1, nimi2, peaLava);
                mänguLiides.näita();

            } catch (IllegalArgumentException ex) {
                veaTeade.setText(ex.getMessage());
            }
        });

        juur.getChildren().addAll(pealkiri, teave, nimedeSilt, nimedeVõrk, veaTeade, algusNupp);

        Scene stseen = new Scene(juur, 450, 420);
        dialoog.setScene(stseen);
        dialoog.setResizable(false);
        dialoog.show();
    }
}