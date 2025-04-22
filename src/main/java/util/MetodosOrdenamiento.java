package util;

import java.util.*;
import java.util.function.Consumer;

public class MetodosOrdenamiento {

    // 1. TimSort (Java usa Arrays.sort() basado en TimSort)
    /**
     * divide el arreglo y usa insertion sort y merge
     */
    private static final int RUN = 32;

    public static void timSort(int[] arr) {
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

    // 2. Comb Sort

    /**
     * metodo burbuja con saltos gap
     */
    public static void combSort(int[] arr) {
        int gap = arr.length;
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = (gap * 10) / 13; // Factor de reducción recomendado: 1.3
            if (gap < 1) gap = 1;
            swapped = false;
            for (int i = 0; i + gap < arr.length; i++) {
                if (arr[i] > arr[i + gap]) {
                    int temp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp;
                    swapped = true;
                }
            }
        }
    }

    // 3. Selection Sort

    /**
     *  el elemento menor al principio del arreglo
     */
    public static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }

    // 4. Tree Sort

    /**
     * hace una insercion de arbol binario de busqueda
     */
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
    public static void treeSort(int[] arr) {
        TreeNode root = null;
        for (int num : arr) root = insert(root, num);
        List<Integer> sortedList = new ArrayList<>();
        inOrder(root, sortedList);
        for (int i = 0; i < arr.length; i++) arr[i] = sortedList.get(i);
    }
    private static TreeNode insert(TreeNode node, int val) {
        if (node == null) return new TreeNode(val);
        if (val < node.val) node.left = insert(node.left, val);
        else node.right = insert(node.right, val);
        return node;
    }
    private static void inOrder(TreeNode node, List<Integer> list) {
        if (node != null) {
            inOrder(node.left, list);
            list.add(node.val);
            inOrder(node.right, list);
        }
    }

    // 5. Pigeonhole Sort

    /**
     * se obtiene el maximo y minimo, y se almacenan los valores posibles en un
     * arreglo, luego se cuentan las repeticiones de cada uno y se insertan en el arreglo resultado
     */
    public static void pigeonholeSort(int[] arr) {
        int min = Arrays.stream(arr).min().getAsInt();
        int max = Arrays.stream(arr).max().getAsInt();
        int range = max - min + 1;
        int[] holes = new int[range];
        for (int num : arr) holes[num - min]++;
        int index = 0;
        for (int i = 0; i < range; i++) {
            while (holes[i]-- > 0) arr[index++] = i + min;
        }
    }

    // 6. Bucket Sort
    /**se crean varios arreglos (raiz del tamaño), cada cubeta recibe un rango de numeros,
     * y finalmente se ordenan individualmente para unirlas
     */
    public static void bucketSort(int[] arr) {
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

    // 7. QuickSort

    /**
     * usa divide y venceras, crea particiones a partir de un pivote que es el ultimo elemento del
     * arreglo, luego mueve los elementos menores al pivote a la izquierda y luego hace 2 llamadas
     * recursivas para hacer lo mismo a la parte izquiera y derecha del pivote
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
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


    
    // 8. HeapSort

    /**crea un heap minimo, que es un arbol binario no ordenado con el minimo elemento en la raiz,
     * luego extrae los elementos para insertarlos en el arreglo final
     */
    public static void heapSort(int[] arr) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int num : arr) pq.add(num);
        for (int i = 0; i < arr.length; i++) arr[i] = pq.poll();
    }



    // 9. Bitonic Sort

    /**
     * se divide el arreglo en 2 partes en cada llamada recursiva, se sigue dividiendo hasta
     * que count es 1, las mitades de los arreglos se ordenan uno en forma creciente, y la
     * otra en forma decreciente, se van ordenando mediante bitonicmerge, finalmente quedan
     * 2 arreglos ordenados inversamente, y esto se combina y ordena en forma ascendente con
     * bitonicmerge
     */
    public static void bitonicSort(int[] arr) {
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

    // 10. Gnome Sort

    /**
     * usa una comparacion simple, pero si se se hace un intercambio para ordenar,
     * se retrocede una posicion cada vez, de modo que se realiza un ordenamiento con un solo ciclo
     */
    public static void gnomeSort(int[] arr) {
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

    // 11. Binary Insertion Sort

    /**
     * se inicializa una key que es el valor a ordenar, luego se definen los limites de la busqueda
     * binaria, luego se determina la posicion en la que debe ir el valor key mediante una comparacion
     * dentro del ciclo while, luego se mueve el arreglo hacia la derecha para hacerle espacio al
     * valor key, se inserta key en la posicion que le corresponde y sigue a la siguiente iteracion
     * de este proceso hasta quedar completamente ordenado
     */
    public static void binaryInsertionSort(int[] arr) {
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


    // Métoodo para probar cada algoritmo con un array aleatorio
    private static void testSortingAlgorithm(String name, int[] originalArray, Consumer<int[]> sortingMethod) {
        int[] arr = Arrays.copyOf(originalArray, originalArray.length);
        long startTime = System.nanoTime();
        sortingMethod.accept(arr);
        long endTime = System.nanoTime();
        System.out.println(name + " tomó " + (endTime - startTime) / 1_000_000.0 + " ms");
    }

    public static void main(String[] args) {
        int size = 10000; // Cambiar para probar diferentes tamaños
        int[] originalArray = new Random().ints(size, 0, 100000).toArray();


        // testSortingAlgorithm("TimSort", originalArray, util.MetodosOrdenamiento::timSort);
        // testSortingAlgorithm("CombSort", originalArray, util.MetodosOrdenamiento::combSort);
        // testSortingAlgorithm("SelectionSort", originalArray, util.MetodosOrdenamiento::selectionSort);
        // testSortingAlgorithm("TreeSort", originalArray, util.MetodosOrdenamiento::treeSort);
        // testSortingAlgorithm("PigeonholeSort", originalArray, util.MetodosOrdenamiento::pigeonholeSort);
        // testSortingAlgorithm("BucketSort", originalArray, util.MetodosOrdenamiento::bucketSort);
        // testSortingAlgorithm("QuickSort", originalArray, arr -> quickSort(arr, 0, arr.length - 1));
        // testSortingAlgorithm("HeapSort", originalArray, util.MetodosOrdenamiento::heapSort);
        // testSortingAlgorithm("BitonicSort", originalArray, util.MetodosOrdenamiento::bitonicSort);
        // testSortingAlgorithm("GnomeSort", originalArray, util.MetodosOrdenamiento::gnomeSort);
        // testSortingAlgorithm("BinaryInsertionSort", originalArray, util.MetodosOrdenamiento::binaryInsertionSort);
        // testSortingAlgorithm("RadixSort", originalArray, util.MetodosOrdenamiento::radixSort);

    }
}
