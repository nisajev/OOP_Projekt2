package com.example.demo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager – vastutab kõige failiga seotud toimingute eest.
 * Kirjutab mängu logi faili ja loeb varasemaid tulemusi.
 */
public class FileManager {

    private static final String LOG_FILE = "game_log.txt";
    private static final String SCORES_FILE = "scores.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Kirjutab lõppenud mängu tulemuse ja käigud logifaili.
     * @param player1     esimese mängija nimi
     * @param player2     teise mängija nimi
     * @param winner      võitja märk ('X', 'O' või ' ' viigi korral)
     * @param moveLog     käikude tekstiline kirjeldus
     */
    public static void saveGameLog(String player1, String player2,
                                   char winner, String moveLog) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("=== Mäng " + LocalDateTime.now().format(FORMATTER) + " ===");
            bw.newLine();
            bw.write("Mängijad: " + player1 + " (X) vs " + player2 + " (O)");
            bw.newLine();
            if (winner == ' ') {
                bw.write("Tulemus: Viik!");
            } else {
                String winnerName = (winner == 'X') ? player1 : player2;
                bw.write("Võitja: " + winnerName + " (" + winner + ")");
            }
            bw.newLine();
            bw.write("Käigud:");
            bw.newLine();
            bw.write(moveLog);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Viga logifaili kirjutamisel: " + e.getMessage());
        }
    }

    /**
     * Salvestab mängija skoori (võitude arv) faili.
     * @param playerName mängija nimi
     * @param wins       võitude arv
     */
    public static void saveScore(String playerName, int wins) {
        // Loe olemasolevad skoorid
        List<String> lines = new ArrayList<>();
        boolean found = false;
        File f = new File(SCORES_FILE);

        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(playerName + ":")) {
                        lines.add(playerName + ":" + wins);
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Viga skoorifaili lugemisel: " + e.getMessage());
            }
        }

        if (!found) lines.add(playerName + ":" + wins);

        // Kirjuta uuendatud skoorid tagasi
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SCORES_FILE))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Viga skoorifaili kirjutamisel: " + e.getMessage());
        }
    }

    /**
     * Loeb mängija varasema skoori failist.
     * @param playerName mängija nimi
     * @return võitude arv, 0 kui mängijat pole veel failis
     */
    public static int loadScore(String playerName) {
        File f = new File(SCORES_FILE);
        if (!f.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(playerName)) {
                    return Integer.parseInt(parts[1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Viga skoori lugemisel: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Loeb viimased N rida logifailist (näitamiseks UI-s).
     * @param maxLines maksimaalne ridade arv
     * @return loetud read ühendatuna
     */
    public static String readLastLog(int maxLines) {
        File f = new File(LOG_FILE);
        if (!f.exists()) return "Logifail puudub.";

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            return "Viga logifaili lugemisel: " + e.getMessage();
        }

        int start = Math.max(0, lines.size() - maxLines);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < lines.size(); i++) {
            sb.append(lines.get(i)).append("\n");
        }
        return sb.toString();
    }
}
