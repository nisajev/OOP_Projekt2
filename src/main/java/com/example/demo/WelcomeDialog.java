package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * WelcomeDialog – tervitusaken mängu alguses.
 * Küsib mängijate nimed ja kuvab lühikese kasutusjuhise.
 * Sisaldab erinditöötlust tühjade nimede kontrollimiseks.
 */
public class WelcomeDialog {

    /**
     * Kuvab tervitusdialoogi.
     * @param primaryStage peaaken, mis pärast dialoogi aktiveeritakse
     */
    public void show(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Ristkülik-Nullmäng – Tere tulemast!");

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        // Pealkiri
        Label title = new Label("❌ Ristkülik-Nullmäng ⭕");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Kasutusjuhis
        TextArea info = new TextArea(
            "Mängu eesmärk: saada kolm oma märki (X või O) järjest " +
            "– horisontaalselt, vertikaalselt või diagonaalselt.\n\n" +
            "Juhised:\n" +
            "  • Klõpsa lahtril, et teha käik\n" +
            "  • Vajuta Ctrl+Z, et viimane käik tagasi võtta\n" +
            "  • Mängijate nimed salvestatakse skoorifaili\n" +
            "  • Iga mängu käigud salvestatakse logifaili"
        );
        info.setEditable(false);
        info.setWrapText(true);
        info.setPrefHeight(150);
        info.setStyle("-fx-font-size: 13px;");

        // Mängijate nimede sisestamine
        Label nameLabel = new Label("Sisesta mängijate nimed:");
        nameLabel.setStyle("-fx-font-weight: bold;");

        GridPane nameGrid = new GridPane();
        nameGrid.setHgap(10);
        nameGrid.setVgap(8);
        nameGrid.setAlignment(Pos.CENTER);

        TextField p1Field = new TextField();
        p1Field.setPromptText("Mängija 1 nimi (X)");

        TextField p2Field = new TextField();
        p2Field.setPromptText("Mängija 2 nimi (O)");

        nameGrid.add(new Label("Mängija X:"), 0, 0);
        nameGrid.add(p1Field, 1, 0);
        nameGrid.add(new Label("Mängija O:"), 0, 1);
        nameGrid.add(p2Field, 1, 1);

        // Veateate silt
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");

        // Alusta mängu nupp
        Button startBtn = new Button("Alusta mängu!");
        startBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 20;");
        startBtn.setDefaultButton(true);

        startBtn.setOnAction(e -> {
            try {
                String name1 = p1Field.getText().trim();
                String name2 = p2Field.getText().trim();

                // Erinditöötlus: nimed ei tohi olla tühjad
                if (name1.isEmpty() || name2.isEmpty()) {
                    throw new IllegalArgumentException("Mõlemad mängijate nimed peavad olema täidetud!");
                }
                // Nimed peavad olema erinevad
                if (name1.equalsIgnoreCase(name2)) {
                    throw new IllegalArgumentException("Mängijate nimed peavad olema erinevad!");
                }

                // Sulge dialoog ja ava mänguaken
                dialog.close();
                GameUI gameUI = new GameUI(name1, name2, primaryStage);
                gameUI.show();

            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        root.getChildren().addAll(title, info, nameLabel, nameGrid, errorLabel, startBtn);

        Scene scene = new Scene(root, 450, 420);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }
}
