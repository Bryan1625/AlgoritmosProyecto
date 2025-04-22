package util;

import model.Termino;

import java.util.List;

public class BitonicSort {
    // 9. Bitonic Sort

    /**
     * se divide el arreglo en 2 partes en cada llamada recursiva, se sigue dividiendo hasta
     * que count es 1, las mitades de los arreglos se ordenan un en forma creciente, y la
     * otra en forma decreciente, se van ordenando mediante bitonicmerge, finalmente quedan
     * 2 arreglos ordenados inversamente, y esto se combina y ordena en forma ascendente con
     * bitonicmerge
     */
    public static void ordenar(int[] arr) {
        bitonicSortHelper(arr, 0, arr.length, 1);
    }
    private static void bitonicSortHelper(int[] arr, int low, int count, int dir) {
        if (count > 1) {
            int k = count / 2;
            bitonicSortHelper(arr, low, k, 1);
            bitonicSortHelper(arr, low + k, k, 0);
            bitonicMerge(arr, low, count, dir);
        }
    }
    private static void bitonicMerge(int[] arr, int low, int count, int dir) {
        if (count > 1) {
            int k = count / 2;
            for (int i = low; i < low + k; i++) {
                if ((dir == 1 && arr[i] > arr[i + k]) || (dir == 0 && arr[i] < arr[i + k])) {
                    int temp = arr[i];
                    arr[i] = arr[i + k];
                    arr[i + k] = temp;
                }
            }
            bitonicMerge(arr, low, k, dir);
            bitonicMerge(arr, low + k, k, dir);
        }
    }

    public static void ordenarTermino(List<Termino> terminos) {
        Termino[] array = terminos.toArray(new Termino[0]);
        bitonicSortHelperTermino(array, 0, array.length, 1);
        terminos.clear();
        for (Termino t : array) {
            terminos.add(t);
        }
    }

    private static void bitonicSortHelperTermino(Termino[] arr, int low, int count, int dir) {
        if (count > 1) {
            int k = count / 2;
            bitonicSortHelperTermino(arr, low, k, 1);
            bitonicSortHelperTermino(arr, low + k, k, 0);
            bitonicMergeTermino(arr, low, count, dir);
        }
    }

    private static void bitonicMergeTermino(Termino[] arr, int low, int count, int dir) {
        if (count > 1) {
            int k = count / 2;
            for (int i = low; i < low + k; i++) {
                if ((dir == 1 && compararTerminos(arr[i], arr[i + k]) > 0) ||
                        (dir == 0 && compararTerminos(arr[i], arr[i + k]) < 0)) {
                    Termino temp = arr[i];
                    arr[i] = arr[i + k];
                    arr[i + k] = temp;
                }
            }
            bitonicMergeTermino(arr, low, k, dir);
            bitonicMergeTermino(arr, low + k, k, dir);
        }
    }

    private static int compararTerminos(Termino t1, Termino t2) {
        if (t1.getFrecuencia() != t2.getFrecuencia()) {
            return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
        }
        return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
    }
}
