package util;

import java.io.File;
import java.util.HashMap;

public class ContarPalabras {
    public static HashMap<String, Integer> contarPalabras(HashMap<String, Integer> mapa, String ruta) {
        File carpeta = new File(ruta);
        File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".bib")); // Filtra archivos .bib

        if (archivos == null) return mapa; // Si la carpeta está vacía o no existe

        for (File archivo : archivos) {
            try{
                String abstractText = BibtexUtil.getCampo(archivo.getAbsolutePath(), "abstract");
                if (!abstractText.equalsIgnoreCase("Campo no encontrado en el archivo")) {
                    procesarPalabras(mapa, abstractText);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return mapa;
    }

    private static void procesarPalabras(HashMap<String, Integer> mapa, String texto) {
        String[] palabras = texto.toLowerCase().replaceAll("[^a-záéíóúüñ]", " ").split("\\s+");
        for (String palabra : palabras) {
            if (mapa.containsKey(palabra)) {
                mapa.put(palabra, mapa.getOrDefault(palabra, 0) + 1);
            }
        }
    }
}
