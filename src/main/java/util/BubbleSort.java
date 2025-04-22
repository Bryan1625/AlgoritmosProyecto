package util;

import model.Termino;

import java.util.List;

public class BubbleSort {

    /**
     * BUBBLESORT
     * metodo de ordenamiento sencillo, recorre el arreglo comprobando si el siguiente elemento
     * es menor que el elemento actual, si lo es, se intercambian posiciones y sigue recorriendo,
     * sigue realizando este proceso hasta que ya no ocurren mas intercambios, lo que significa
     * que ya esta ordenado
     * @param arr
     */
    public static void ordenar(int[] arr) {
        int n = arr.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Intercambiar elementos
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            // Si no hubo intercambios, el arreglo ya está ordenado
            if (!swapped) break;
        }
    }
    public static void ordenarTermino(List<Termino> terminos) {
        int n = terminos.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (terminos.get(j).getFrecuencia() > terminos.get(j + 1).getFrecuencia()) {
                    Termino temp = terminos.get(j);
                    terminos.set(j, terminos.get(j + 1));
                    terminos.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static void ordenarTerminoSacudida(List<Termino> terminos) {
        int n = terminos.size(), primero = 1, ultimo = terminos.size()-1, dir = terminos.size()-1;
        while(ultimo >= primero){
            for (int i = ultimo; i >= primero; i--) {
                if (terminos.get(i-1).getFrecuencia() > terminos.get(i).getFrecuencia()) {
                    Termino temp = terminos.get(i-1);
                    terminos.set(i-1, terminos.get(i));
                    terminos.set(i, temp);
                    dir = i;
                }
            }
            primero = dir + 1;
            for (int i = primero; i <= ultimo; i++) {
                if(terminos.get(i-1).getFrecuencia() > terminos.get(i).getFrecuencia()){
                    Termino temp = terminos.get(i-1);
                    terminos.set(i-1, terminos.get(i));
                    terminos.set(i, temp);
                    dir = i;
                }
            }
            ultimo = dir - 1;
        }
    }
}
