package util;

import model.Termino;

import java.util.List;
import java.util.PriorityQueue;

public class HeapSort {
    // 8. HeapSort

    /**crea un heap minimo, que es un arbol binario no ordenado con el minimo elemento en la raiz,
     * luego extrae los elementos para insertarlos en el arreglo final
     */
    public static void ordenar(int[] arr) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int num : arr) pq.add(num);
        for (int i = 0; i < arr.length; i++) arr[i] = pq.poll();
    }

    public static void ordenarTermino(List<Termino> terminos) {
        PriorityQueue<Termino> pq = new PriorityQueue<>(HeapSort::compararTerminos);
        pq.addAll(terminos);
        terminos.clear();
        while (!pq.isEmpty()) {
            terminos.add(pq.poll());
        }
    }

    private static int compararTerminos(Termino t1, Termino t2) {
        if (t1.getFrecuencia() != t2.getFrecuencia()) {
            return Integer.compare(t2.getFrecuencia(), t1.getFrecuencia()); // Ordenar por frecuencia descendente
        }
        return t1.getPalabra().compareTo(t2.getPalabra()); // Si hay empate, ordenar alfabéticamente
    }
}
