package util;

import model.Termino;

import java.util.List;

public class TimSort {

    // 1. TimSort (Java usa Arrays.sort() basado en TimSort)
    /**
     * divide el arreglo y usa insertion sort y merge
     */
    private static final int RUN = 32;

    public static void ordenar(int[] arr) {
        int n = arr.length;

        // Ordenar pequeños subarrays con Insertion Sort
        for (int i = 0; i < n; i += RUN) {
            insertionSort(arr, i, Math.min(i + RUN - 1, n - 1));
        }

        // Fusionar bloques de tamaño RUN en potencias de 2
        for (int size = RUN; size < n; size *= 2) {
            for (int left = 0; left < n; left += 2 * size) {
                int mid = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) {
                    merge(arr, left, mid, right);
                }
            }
        }
    }

    // Insertion Sort para ordenar pequeños bloques
    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // Merge de dos subarrays ordenados
    private static void merge(int[] arr, int left, int mid, int right) {
        int len1 = mid - left + 1, len2 = right - mid;
        int[] leftArr = new int[len1];
        int[] rightArr = new int[len2];

        System.arraycopy(arr, left, leftArr, 0, len1);
        System.arraycopy(arr, mid + 1, rightArr, 0, len2);

        int i = 0, j = 0, k = left;
        while (i < len1 && j < len2) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
        }
        while (i < len1) arr[k++] = leftArr[i++];
        while (j < len2) arr[k++] = rightArr[j++];
    }

    public static void ordenarTerminos(List<Termino> terminos) {
        terminos.sort((t1, t2) -> {
            if (t1.getFrecuencia() != t2.getFrecuencia()) {
                return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
            }
            return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
        });
    }
}
