package com.example.demo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Failihaldur – vastutab kõige failiga seotud toimingute eest.
 * Kirjutab mängu logi faili ja loeb varasemaid tulemusi.
 */
public class FailiHaldur {

    private static final String logifail= "game_log.txt";
    private static final String skoorifail = "scores.txt";
    private static final DateTimeFormatter vormindaja =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Kirjutab lõppenud mängu tulemuse ja käigud logifaili.
     * @param mängija1     esimese mängija nimi
     * @param mängija2     teise mängija nimi
     * @param võitja       võitja märk ('X', 'O' või ' ' viigi korral)
     * @param käigulogi     käikude tekstiline kirjeldus
     */
    public static void salvestaMänguLogi(String mängija1, String mängija2,
                                         char võitja, String käigulogi) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logifail, true))) {
            bw.write("=== Mäng " + LocalDateTime.now().format(vormindaja) + " ===");
            bw.newLine();
            bw.write("Mängijad: " + mängija1 + " (X) vs " + mängija2 + " (O)");
            bw.newLine();
            if (võitja == ' ') {
                bw.write("Tulemus: Viik!");
            } else {
                String võitjaNimi = (võitja == 'X') ? mängija1 : mängija2;
                bw.write("Võitja: " + võitjaNimi + " (" + võitja + ")");
            }
            bw.newLine();
            bw.write("Käigud:");
            bw.newLine();
            bw.write(käigulogi);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Viga logifaili kirjutamisel: " + e.getMessage());
        }
    }

    /**
     * Salvestab mängija skoori (võitude arv) faili.
     * @param mängijaNimi mängija nimi
     * @param võidud       võitude arv
     */
    public static void salvestaSkoor(String mängijaNimi, int võidud) {
        // Loe olemasolevad skoorid
        List<String> read = new ArrayList<>();
        boolean leitud = false;
        File f = new File(skoorifail);

        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String rida;
                while ((rida = br.readLine()) != null) {
                    if (rida.startsWith(mängijaNimi + ":")) {
                        read.add(mängijaNimi + ":" + võidud);
                        leitud = true;
                    } else {
                        read.add(rida);
                    }
                }
            } catch (IOException e) {
                System.err.println("Viga skoorifaili lugemisel: " + e.getMessage());
            }
        }

        if (!leitud) read.add(mängijaNimi + ":" + võidud);

        // Kirjuta uuendatud skoorid tagasi
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(skoorifail))) {
            for (String rida : read) {
                bw.write(rida);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Viga skoorifaili kirjutamisel: " + e.getMessage());
        }
    }

    /**
     * Loeb mängija varasema skoori failist.
     * @param mängijaNimi mängija nimi
     * @return võitude arv, 0 kui mängijat pole veel failis
     */
    public static int laadiSkoor(String mängijaNimi) {
        File f = new File(skoorifail);
        if (!f.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String rida;
            while ((rida = br.readLine()) != null) {
                String[] osad = rida.split(":");
                if (osad.length == 2 && osad[0].equals(mängijaNimi)) {
                    return Integer.parseInt(osad[1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Viga skoori lugemisel: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Loeb viimased N rida logifailist (näitamiseks UI-s).
     * @param maxRidu maksimaalne ridade arv
     * @return loetud read ühendatuna
     */
    public static String loeViimasedRead(int maxRidu) {
        File f = new File(logifail);
        if (!f.exists()) return "Logifail puudub.";

        List<String> read = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String rida;
            while ((rida = br.readLine()) != null) read.add(rida);
        } catch (IOException e) {
            return "Viga logifaili lugemisel: " + e.getMessage();
        }

        int algus = Math.max(0, read.size() - maxRidu);
        StringBuilder sb = new StringBuilder();
        for (int i = algus; i < read.size(); i++) {
            sb.append(read.get(i)).append("\n");
        }
        return sb.toString();
    }
}
