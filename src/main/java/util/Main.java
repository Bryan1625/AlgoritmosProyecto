package util;

import model.Termino;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Integer> mapa = new HashMap<>();
        mapa.put("abstraction", 0);
        mapa.put("motivation", 0);
        mapa.put("algorithm", 0);
        mapa.put("persistence", 0);
        mapa.put("coding", 0);
        mapa.put("block", 0);
        mapa.put("creativity", 0);
        mapa.put("mobile application", 0);
        mapa.put("logic", 0);
        mapa.put("programming", 0);
        mapa.put("conditionals", 0);
        mapa.put("robotic", 0);
        mapa.put("loops", 0);
        mapa.put("scratch", 0);

        String ruta = "C:\\Users\\Bryan\\Documents\\btw\\code\\Programacion\\AlgoritmosProyecto\\src\\main\\resources";

        ContarPalabras.contarPalabras(mapa, ruta);

        ArrayList<Termino> terminos = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            Termino termino = new Termino(entry.getKey(), entry.getValue());
            terminos.add(termino);
        }
        List<Termino> copia = new ArrayList<>(terminos);
        ordenarBubbleSort(terminos);
        ordenarSacudida(copia);
    }

    private static void imprimirResultado(List<Termino> terminos){
        for (Termino termino : terminos) {
            System.out.println("Termino: " + termino.getPalabra() + "," + termino.getFrecuencia());
        }
    }

    private static void ordenarRadixSort(List<Termino> terminos) {
        long inicio = System.nanoTime();
        RadixSort.ordenarTermino(terminos);
        long fin = System.nanoTime();
        System.out.println("radix tomó: " + (fin - inicio) / 1e6 + " ms");
    }

    private static void ordenarBubbleSort(List<Termino> terminos) {
        long inicio = System.nanoTime();
        BubbleSort.ordenarTermino(terminos);
        long fin = System.nanoTime();
        System.out.println("bubble sort: " + (fin - inicio) / 1e6 + " ms");
    }

    private static void ordenarSacudida(List<Termino> terminos){
        long inicio = System.nanoTime();
        BubbleSort.ordenarTerminoSacudida(terminos);
        long fin = System.nanoTime();
        System.out.println("sacudida: " + (fin - inicio) / 1e6 + " ms");
    }

    private static void ordenarQuickSort(List<Termino> terminos) {
        QuickSort.ordenarTermino(terminos, 0, terminos.size() - 1);
    }

    private static void ordenarTimSort(List<Termino> terminos) {
        TimSort.ordenarTerminos(terminos);
    }

    private static void ordenarCombSort(List<Termino> terminos) {
        CombSort.ordenarTerminos(terminos);
    }

    private static void ordenarSelectionSort(List<Termino> terminos) {
        SelectionSort.ordenarTerminos(terminos);
    }

    private static void ordenarTreeSort(List<Termino> terminos) {
        TreeSort.ordenarTerminos(terminos);
    }

    private static void ordenarPigeonholeSort(List<Termino> terminos) {
        PigeonHole.ordenarTermino(terminos);
    }

    private static void ordenarBucketSort(List<Termino> terminos) {
        BucketSort.ordenarTermino(terminos);
    }

    private static void ordenarHeapSort(List<Termino> terminos) {
        HeapSort.ordenarTermino(terminos);
    }

    private static void ordenarBitonicSort(List<Termino> terminos) {
        BitonicSort.ordenarTermino(terminos);
    }

    private static void ordenarGnomeSort(List<Termino> terminos) {
        GnomeSort.ordenarTermino(terminos);
    }

    private static void ordenarBinaryInsertionSort(List<Termino> terminos) {
        BinaryInsertionSort.ordenarTermino(terminos);
    }
}