package integrado.prog2;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.dao.ConexionDB;
import integrado.prog2.dao.CategoriaDAO;
import integrado.prog2.exception.PersistenciaException;
import integrado.prog2.repository.UsuarioRepository;
import integrado.prog2.service.ConexionService;
import integrado.prog2.service.CategoriaService;
import integrado.prog2.service.UsuarioService;
import integrado.prog2.ui.MenuUsuarios;
import integrado.prog2.ui.MenuCategorias;
import java.util.Scanner;

/** Punto de entrada del sistema. La interacción se mantiene en consola. */
public class Main {

    public static void main(String[] args) {
        UsuarioService usuarios = new UsuarioService(new UsuarioRepository());
        ConexionService conexionService = new ConexionService(new ConexionDB());

        ConfiguracionDB configuracion = null;
        try {
            configuracion = ConfiguracionDB.cargar();
            conexionService.verificar(configuracion);
            System.out.println("Conexión a MySQL verificada correctamente.");
        } catch (PersistenciaException ex) {
            System.out.println("Aviso de conexión: " + ex.getMessage());
            System.out.println("Puede utilizar el menú de usuarios mientras revisa la conexión.");
        }

        try (Scanner scanner = new Scanner(System.in)) {
            MenuUsuarios menuUsuarios = new MenuUsuarios(scanner, usuarios);
            MenuCategorias menuCategorias = configuracion == null ? null
                    : new MenuCategorias(scanner,
                            new CategoriaService(new CategoriaDAO(new ConexionDB(), configuracion)));
            int opcion;
            do {
                System.out.println("\n=== FOOD STORE | PROGRAMACION II ===");
                System.out.println("1) Categorías ............ [BASE DE DATOS]");
                System.out.println("2) Productos ............. [BASE DE DATOS] (pendiente)");
                System.out.println("3) Usuarios .............. [MEMORIA]");
                System.out.println("4) Pedidos y detalles .... [MEMORIA] (pendiente)");
                System.out.println("5) Ver configuración de conexión");
                System.out.println("0) Salir");
                opcion = leerOpcion(scanner);
                switch (opcion) {
                    case 1 -> {
                        if (menuCategorias == null) {
                            System.out.println("No se pudo cargar la configuración JDBC.");
                        } else {
                            menuCategorias.mostrar();
                        }
                    }
                    case 3 -> menuUsuarios.mostrar();
                    case 5 -> mostrarConfiguracion(configuracion);
                    case 2, 4 -> System.out.println("Esta función todavía no está implementada.");
                    case 0 -> System.out.println("Fin del programa. Los datos en memoria se descartaron.");
                    default -> System.out.println("Opción fuera de rango.");
                }
            } while (opcion != 0);
        }
    }

    private static void mostrarConfiguracion(ConfiguracionDB configuracion) {
        if (configuracion == null) {
            System.out.println("No fue posible cargar persistence.xml.");
            return;
        }
        System.out.println("Unidad de persistencia: " + configuracion.getUnidad());
        System.out.println("URL JDBC: " + configuracion.getUrl());
        System.out.println("Driver: " + configuracion.getDriver());
        // La contraseña no se muestra en consola.
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
