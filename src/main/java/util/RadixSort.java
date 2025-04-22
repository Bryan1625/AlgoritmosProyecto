package util;

import model.Termino;

import java.util.Arrays;
import java.util.List;

public class RadixSort {
    // 12. Radix Sort

    /**
     * usa un ordenamiento de digitos, se obtiene cada digito y se determina cuantas veces se repite,
     * luego calcula la posicion final de cada digito en output, luego se colocan los numeros en
     * la posicion correcta empezando por la derecha, por ultimo coloca los elementos ordenados
     * en el arreglo final
     */
    public static void radixSort(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt(), exp = 1;
        while (max / exp > 0) {
            countingSortByDigit(arr, exp);
            exp *= 10;
        }
    }
    private static void countingSortByDigit(int[] arr, int exp) {
        int[] output = new int[arr.length], count = new int[10];
        for (int num : arr) count[(num / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = arr.length - 1; i >= 0; i--) output[--count[(arr[i] / exp) % 10]] = arr[i];
        System.arraycopy(output, 0, arr, 0, arr.length);
    }
    public static void ordenarTermino(List<Termino> terminos) {
        int maxFrecuencia = terminos.stream().mapToInt(Termino::getFrecuencia).max().orElse(0);
        int exp = 1;
        while (maxFrecuencia / exp > 0) {
            countingSortByFrecuencia(terminos, exp);
            exp *= 10;
        }
    }

    private static void countingSortByFrecuencia(List<Termino> terminos, int exp) {
        int n = terminos.size();
        Termino[] output = new Termino[n];
        int[] count = new int[10];

        for (Termino termino : terminos) count[(termino.getFrecuencia() / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) output[--count[(terminos.get(i).getFrecuencia() / exp) % 10]] = terminos.get(i);
        for (int i = 0; i < n; i++) terminos.set(i, output[i]);
    }
}
