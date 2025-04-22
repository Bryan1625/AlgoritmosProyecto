package util;

import model.Termino;

import java.util.List;

public class BinaryInsertionSort {
    // 11. Binary Insertion Sort

    /**
     * se inicializa una key que es el valor a ordenar, luego se definen los limites de la busqueda
     * binaria, luego se determina la posicion en la que debe ir el valor key mediante una comparacion
     * dentro del ciclo while, luego se mueve el arreglo hacia la derecha para hacerle espacio al
     * valor key, se inserta key en la posicion que le corresponde y sigue a la siguiente iteracion
     * de este proceso hasta quedar completamente ordenado
     */
    public static void ordenar(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i], left = 0, right = i;
            while (left < right) {
                int mid = (left + right) / 2;
                if (arr[mid] > key) right = mid;
                else left = mid + 1;
            }
            System.arraycopy(arr, left, arr, left + 1, i - left);
            arr[left] = key;
        }
    }
    public static void ordenarTermino(List<Termino> terminos) {
        for (int i = 1; i < terminos.size(); i++) {
            Termino key = terminos.get(i);
            int left = 0, right = i;
            while (left < right) {
                int mid = (left + right) / 2;
                if (compararTerminos(terminos.get(mid), key) > 0) right = mid;
                else left = mid + 1;
            }
            terminos.add(left, key);
            terminos.remove(i + 1);
        }
    }

    private static int compararTerminos(Termino t1, Termino t2) {
        if (t1.getFrecuencia() != t2.getFrecuencia()) {
            return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
        }
        return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
    }
}
