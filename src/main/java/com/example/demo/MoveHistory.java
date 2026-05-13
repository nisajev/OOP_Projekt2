package com.example.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * MoveHistory – salvestab kõik mängu käigud järjendina.
 * Võimaldab käike tagasi võtta (undo) ja ajalugu kustutada.
 */
public class MoveHistory {

    // Iga käik on kolmeelemendeline massiiv: [rida, veerg, mängija ASCII kood]
    private List<int[]> moves;

    public MoveHistory() {
        moves = new ArrayList<>();
    }

    /**
     * Lisab uue käigu ajalukku.
     * @param row    rida
     * @param col    veerg
     * @param player mängija märk ('X' või 'O')
     */
    public void addMove(int row, int col, char player) {
        moves.add(new int[]{row, col, player});
    }

    /**
     * Eemaldab ja tagastab viimase käigu.
     * @return viimane käik [rida, veerg, mängija] või null kui ajalugu tühi
     */
    public int[] removeLastMove() {
        if (moves.isEmpty()) return null;
        return moves.remove(moves.size() - 1);
    }

    /**
     * Kustutab kogu ajaloo.
     */
    public void clear() {
        moves.clear();
    }

    /**
     * Tagastab käikude arvu.
     */
    public int size() {
        return moves.size();
    }

    /**
     * Tagastab kogu käikude loendi (muutmiseks mõeldud koopia).
     */
    public List<int[]> getMoves() {
        return new ArrayList<>(moves);
    }

    /**
     * Vormindab ajaloo loetavaks tekstiks logifaili jaoks.
     * @return tekst kujul "Käik 1: X -> (0,0)\nKäik 2: O -> (1,1)\n..."
     */
    public String toLogString() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (int[] m : moves) {
            sb.append("Käik ").append(i++).append(": ")
              .append((char) m[2]).append(" -> (")
              .append(m[0]).append(",").append(m[1]).append(")\n");
        }
        return sb.toString();
    }
}
