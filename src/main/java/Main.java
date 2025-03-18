import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Biblioteca.inicializar();

        while (true) {
            System.out.println("\nSelecciona una opción:");
            System.out.println("1. Buscar y exportar BibTeX");
            System.out.println("2. Salir");

            System.out.print("Opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            if (opcion == 2) {
                break;
            }

            System.out.print("Ingrese el título del artículo: ");
            String titulo = scanner.nextLine();

            switch (opcion) {
                case 1:
                case 3:
                case 2:
                    //Biblioteca.buscarYExportarBibTeX(titulo);
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }

        //Biblioteca.cerrar();
        scanner.close();
    }
}
