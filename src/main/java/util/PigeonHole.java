package util;

import model.Termino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PigeonHole {
    // 5. Pigeonhole Sort

    /**
     * se obtiene el maximo y minimo, y se almacenan repetidos en un arreglo,
     * luego se insertan ordenados
     */
    public static void ordenar(int[] arr) {
        int min = Arrays.stream(arr).min().getAsInt();
        int max = Arrays.stream(arr).max().getAsInt();
        int range = max - min + 1;
        int[] holes = new int[range];
        for (int num : arr) holes[num - min]++;
        int index = 0;
        for (int i = 0; i < range; i++) {
            while (holes[i]-- > 0) arr[index++] = i + min;
        }
    }

    public static void ordenarTermino(List<Termino> terminos) {
        int min = terminos.stream().mapToInt(Termino::getFrecuencia).min().orElse(0);
        int max = terminos.stream().mapToInt(Termino::getFrecuencia).max().orElse(0);
        int range = max - min + 1;
        List<Termino>[] holes = new ArrayList[range];
        for (int i = 0; i < range; i++) {
            holes[i] = new ArrayList<>();
        }
        for (Termino termino : terminos) {
            holes[termino.getFrecuencia() - min].add(termino);
        }
        terminos.clear();
        for (List<Termino> bucket : holes) {
            bucket.sort(Comparator.comparing(Termino::getPalabra));
            terminos.addAll(bucket);
        }
    }
}
