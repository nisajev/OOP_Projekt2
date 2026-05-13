package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Peaklass – käivitab JavaFX rakenduse.
 * Loob esmalt tervitusdialoogi, seejärel avab mänguakna.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Näita tervitusdialoog enne mängu algust
        WelcomeDialog welcome = new WelcomeDialog();
        welcome.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
