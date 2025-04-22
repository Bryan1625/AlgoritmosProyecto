package util;

import model.Termino;

import java.util.ArrayList;
import java.util.List;

public class TreeSort {
    // 4. Tree Sort

    /**
     * hace una insercion de arbol binario de busqueda
     */
    static class TreeNode {
        int val;
        MetodosOrdenamiento.TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
    public static void ordenar(int[] arr) {
        MetodosOrdenamiento.TreeNode root = null;
        for (int num : arr) root = insert(root, num);
        List<Integer> sortedList = new ArrayList<>();
        inOrder(root, sortedList);
        for (int i = 0; i < arr.length; i++) arr[i] = sortedList.get(i);
    }
    private static MetodosOrdenamiento.TreeNode insert(MetodosOrdenamiento.TreeNode node, int val) {
        if (node == null) return new MetodosOrdenamiento.TreeNode(val);
        if (val < node.val) node.left = insert(node.left, val);
        else node.right = insert(node.right, val);
        return node;
    }
    private static void inOrder(MetodosOrdenamiento.TreeNode node, List<Integer> list) {
        if (node != null) {
            inOrder(node.left, list);
            list.add(node.val);
            inOrder(node.right, list);
        }
    }

    public static void ordenarTerminos(List<Termino> terminos) {
        TreeNodeTermino root = null;
        for (Termino termino : terminos) {
            root = insertTermino(root, termino);
        }
        List<Termino> sortedList = new ArrayList<>();
        inOrderTermino(root, sortedList);
        terminos.clear();
        terminos.addAll(sortedList);
    }

    private static TreeNodeTermino insertTermino(TreeNodeTermino node, Termino termino) {
        if (node == null) return new TreeNodeTermino(termino.getPalabra(), termino.getFrecuencia());

        if (termino.getFrecuencia() < node.frecuencia ||
                (termino.getFrecuencia() == node.frecuencia && termino.getPalabra().compareTo(node.palabra) < 0)) {
            node.left = insertTermino(node.left, termino);
        } else {
            node.right = insertTermino(node.right, termino);
        }
        return node;
    }
    private static void inOrderTermino(TreeNodeTermino node, List<Termino> list) {
        if (node != null) {
            inOrderTermino(node.left, list);
            list.add(new Termino(node.palabra, node.frecuencia));
            inOrderTermino(node.right, list);
        }
    }

    static class TreeNodeTermino {
        String palabra;
        int frecuencia;
        TreeNodeTermino left, right;

        TreeNodeTermino(String palabra, int frecuencia) {
            this.palabra = palabra;
            this.frecuencia = frecuencia;
        }
    }
}
