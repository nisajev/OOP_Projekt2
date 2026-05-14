package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Peaklass – käivitab JavaFX rakenduse.
 * Loob esmalt tervitusdialoogi, seejärel avab mänguakna.
 */
public class Main extends Application {

    @Override
    public void start(Stage peaLava) {
        // Näita tervitusdialoog enne mängu algust
        TervitusDialoog tervitus = new TervitusDialoog();
        tervitus.näita(peaLava);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
