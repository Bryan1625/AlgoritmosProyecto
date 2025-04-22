package util;

import model.Termino;

import java.util.Collections;
import java.util.List;

public class CombSort {
    // 2. Comb Sort

    /**
     * metodo burbuja con saltos gap
     */
    public static void ordenar(int[] arr) {
        int gap = arr.length;
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = (gap * 10) / 13; // Factor de reducción recomendado: 1.3
            if (gap < 1) gap = 1;
            swapped = false;
            for (int i = 0; i + gap < arr.length; i++) {
                if (arr[i] > arr[i + gap]) {
                    int temp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp;
                    swapped = true;
                }
            }
        }
    }

    public static void ordenarTerminos(List<Termino> terminos) {
        int gap = terminos.size();
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = (gap * 10) / 13; // Factor de reducción recomendado: 1.3
            if (gap < 1) gap = 1;
            swapped = false;
            for (int i = 0; i + gap < terminos.size(); i++) {
                Termino t1 = terminos.get(i);
                Termino t2 = terminos.get(i + gap);
                boolean shouldSwap = t1.getFrecuencia() < t2.getFrecuencia() ||
                        (t1.getFrecuencia() == t2.getFrecuencia() && t1.getPalabra().compareTo(t2.getPalabra()) > 0);
                if (shouldSwap) {
                    Collections.swap(terminos, i, i + gap);
                    swapped = true;
                }
            }
        }
    }
}
