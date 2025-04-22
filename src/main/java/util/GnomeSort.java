package util;

import model.Termino;

import java.util.List;

public class GnomeSort {
    // 10. Gnome Sort

    /**
     * usa una comparacion simple, pero si se se hace un intercambio para ordenar,
     * se retrocede una posicion cada vez, de modo que se realiza un ordenamiento con un solo ciclo
     */
    public static void ordenar(int[] arr) {
        int index = 0;
        while (index < arr.length) {
            if (index == 0 || arr[index] >= arr[index - 1]) {
                index++;
            } else {
                int temp = arr[index];
                arr[index] = arr[index - 1];
                arr[index - 1] = temp;
                index--;
            }
        }
    }
    public static void ordenarTermino(List<Termino> terminos) {
        int index = 0;
        while (index < terminos.size()) {
            if (index == 0 || compararTerminos(terminos.get(index), terminos.get(index - 1)) >= 0) {
                index++;
            } else {
                Termino temp = terminos.get(index);
                terminos.set(index, terminos.get(index - 1));
                terminos.set(index - 1, temp);
                index--;
            }
        }
    }

    private static int compararTerminos(Termino t1, Termino t2) {
        if (t1.getFrecuencia() != t2.getFrecuencia()) {
            return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
        }
        return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
    }
}
