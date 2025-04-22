package util;

import model.Termino;

import java.util.*;

public class BucketSort {
    // 6. Bucket Sort
    /**se crean varios arreglos (raiz del tamaño), cada cubeta recibe un rango de numeros,
     * y finalmente se ordenan individualmente para unirlas
     */
    public static void ordenar(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();
        int numBuckets = (int) Math.sqrt(arr.length);
        List<Integer>[] buckets = new ArrayList[numBuckets];
        for (int i = 0; i < numBuckets; i++) buckets[i] = new ArrayList<>();
        for (int num : arr) buckets[num * numBuckets / (max + 1)].add(num);
        int index = 0;
        for (List<Integer> bucket : buckets) {
            Collections.sort(bucket);
            for (int num : bucket) arr[index++] = num;
        }
    }
    public static void ordenarTermino(List<Termino> terminos) {
        int max = terminos.stream().mapToInt(Termino::getFrecuencia).max().orElse(0);
        int numBuckets = (int) Math.sqrt(terminos.size());
        List<List<Termino>> buckets = new ArrayList<>(Collections.nCopies(numBuckets, new ArrayList<>()));

        for (Termino termino : terminos) {
            int index = (termino.getFrecuencia() * numBuckets) / (max + 1);
            buckets.get(index).add(termino);
        }

        terminos.clear();
        for (List<Termino> bucket : buckets) {
            bucket.sort(Comparator.comparing(Termino::getFrecuencia).reversed()
                    .thenComparing(Termino::getPalabra));
            terminos.addAll(bucket);
        }
    }
}
