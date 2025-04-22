package util;

import org.jbibtex.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class BibtexUtil {

    public static String getCampo(String rutaArchivo, String campo) throws IOException, ParseException {
        BibTeXParser parser = new BibTeXParser();
        try (FileReader reader = new FileReader(rutaArchivo)) {
            BibTeXDatabase database = parser.parse(reader);

            for (Map.Entry<Key, BibTeXEntry> entry : database.getEntries().entrySet()) {
                BibTeXEntry bibEntry = entry.getValue();
                String valorCampo = getField(bibEntry, campo);
                if (!valorCampo.equals("No disponible")) {
                    return valorCampo;
                }

            }
            return "Campo no encontrado en el archivo";
        }
    }


    private static String getField (BibTeXEntry entry, String fieldName){
        Value value = entry.getField(new Key(fieldName));
        return (value != null) ? value.toUserString() : "No disponible";
    }


    public static void main (String[]args){
        String archivoBib = "C:\\Users\\Bryan\\Documents\\btw\\code\\Programacion\\AlgoritmosProyecto\\src\\main\\resources\\cita_ieee_1743563152.bib"; // Ruta del archivo BibTeX
        String campo = "abstract"; // Campo a extraer

        try {
            System.out.println(getCampo(archivoBib, campo));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
