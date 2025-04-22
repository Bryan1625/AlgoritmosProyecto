package util;

import model.Termino;

import java.util.Collections;
import java.util.List;

public class SelectionSort {
    // 3. Selection Sort

    /**
     *  el elemento menor al principio del arreglo
     */
    public static void ordenar(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
    public static void ordenarTerminos(List<Termino> terminos) {
        for (int i = 0; i < terminos.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < terminos.size(); j++) {
                Termino t1 = terminos.get(j);
                Termino tMin = terminos.get(minIndex);
                boolean shouldSwap = t1.getFrecuencia() > tMin.getFrecuencia() ||
                        (t1.getFrecuencia() == tMin.getFrecuencia() && t1.getPalabra().compareTo(tMin.getPalabra()) < 0);
                if (shouldSwap) {
                    minIndex = j;
                }
            }
            Collections.swap(terminos, i, minIndex);
        }
    }
}
