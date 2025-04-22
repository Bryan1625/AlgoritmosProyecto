package util;

import model.Termino;

import java.util.List;

public class QuickSort {
    // 7. QuickSort

    /**
     * usa divide y venceras, crea particiones a partir de un pivota que es el ultimo elemento del
     * arreglo, luego mueve los elementos menores al pivote a la izquierda y luego hace 2 llamadas
     * recursivas para hacer los mismo a la parte izquiera y derecha del pivote
     */
    public static void ordenar(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            ordenar(arr, low, pi - 1);
            ordenar(arr, pi + 1, high);
        }
    }
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void ordenarTermino(List<Termino> terminos, int low, int high) {
        if (low < high) {
            int pi = partitionTermino(terminos, low, high);
            ordenarTermino(terminos, low, pi - 1);
            ordenarTermino(terminos, pi + 1, high);
        }
    }

    private static int partitionTermino(List<Termino> terminos, int low, int high) {
        Termino pivot = terminos.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compararTerminos(terminos.get(j), pivot) <= 0) {
                i++;
                Termino temp = terminos.get(i);
                terminos.set(i, terminos.get(j));
                terminos.set(j, temp);
            }
        }
        Termino temp = terminos.get(i + 1);
        terminos.set(i + 1, terminos.get(high));
        terminos.set(high, temp);
        return i + 1;
    }

    private static int compararTerminos(Termino t1, Termino t2) {
        if (t1.getFrecuencia() != t2.getFrecuencia()) {
            return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
        }
        return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
    }
}
