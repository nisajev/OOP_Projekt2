package com.example.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * MoveHistory – salvestab kõik mängu käigud järjendina.
 * Võimaldab käike tagasi võtta (undo) ja ajalugu kustutada.
 */
public class MoveHistory {

    // Iga käik on kolmeelemendiline massiiv: [rida, veerg, mängija ASCII kood]
    private List<int[]> käigud;

    public MoveHistory() {
        käigud = new ArrayList<>();
    }

    /**
     * Lisab uue käigu ajalukku.
     * @param rida    rida
     * @param veerg    veerg
     * @param mängija mängija märk ('X' või 'O')
     */
    public void lisaKäik(int rida, int veerg, char mängija) {
        käigud.add(new int[]{rida, veerg, mängija});
    }

    /**
     * Eemaldab ja tagastab viimase käigu.
     * @return viimane käik [rida, veerg, mängija] või null kui ajalugu tühi
     */
    public int[] eemaldaViimaneKäik() {
        if (käigud.isEmpty()) return null;
        return käigud.remove(käigud.size() - 1);
    }

    /**
     * Kustutab kogu ajaloo.
     */
    public void tühjenda() {
        käigud.clear();
    }

    /**
     * Tagastab käikude arvu.
     */
    public int suurus() {
        return käigud.size();
    }

    /**
     * Tagastab kogu käikude loendi (muutmiseks mõeldud koopia).
     */
    public List<int[]> getKäigud() {
        return new ArrayList<>(käigud);
    }

    /**
     * Vormindab ajaloo loetavaks tekstiks logifaili jaoks.
     * @return tekst kujul "Käik 1: X -> (0,0)\nKäik 2: O -> (1,1)\n..."
     */
    public String toLogiString() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (int[] m : käigud) {
            sb.append("Käik ").append(i++).append(": ")
                    .append((char) m[2]).append(" -> (")
                    .append(m[0]).append(",").append(m[1]).append(")\n");
        }
        return sb.toString();
    }
}
