package integrado.prog2;

import integrado.prog2.repository.UsuarioRepository;
import integrado.prog2.service.UsuarioService;
import integrado.prog2.ui.MenuUsuarios;
import java.util.Scanner;

/** Punto de entrada del sistema. La interacción se mantiene en consola. */
public class Main {

    public static void main(String[] args) {
        UsuarioService usuarios = new UsuarioService(new UsuarioRepository());
        try (Scanner scanner = new Scanner(System.in)) {
            MenuUsuarios menuUsuarios = new MenuUsuarios(scanner, usuarios);
            int opcion;
            do {
                System.out.println("\n=== FOOD STORE | PROGRAMACION II ===");
                System.out.println("1) Categorías ............ [BASE DE DATOS] (pendiente)");
                System.out.println("2) Productos ............. [BASE DE DATOS] (pendiente)");
                System.out.println("3) Usuarios .............. [MEMORIA]");
                System.out.println("4) Pedidos y detalles .... [MEMORIA] (pendiente)");
                System.out.println("5) Ver configuración de conexión (pendiente)");
                System.out.println("0) Salir");
                opcion = leerOpcion(scanner);
                switch (opcion) {
                    case 3 -> menuUsuarios.mostrar();
                    case 1, 2, 4, 5 -> System.out.println("Esta función todavía no está implementada.");
                    case 0 -> System.out.println("Fin del programa. Los datos en memoria se descartaron.");
                    default -> System.out.println("Opción fuera de rango.");
                }
            } while (opcion != 0);
        }
    }

    private static int leerOpcion(Scanner scanner) {
        while (true) {
            System.out.print("Seleccione: ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá una opción numérica válida.");
            }
        }
    }
}
