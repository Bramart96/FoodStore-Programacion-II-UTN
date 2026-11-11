package integrado.prog2.ui;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.PersistenciaException;
import integrado.prog2.service.CategoriaService;
import java.util.List;
import java.util.Scanner;
import integrado.prog2.validation.ValidacionTexto;

/** Operaciones CRUD de categorías desde consola. */
public class MenuCategorias {

    private final Scanner scanner;
    private final CategoriaService servicio;

    public MenuCategorias(Scanner scanner, CategoriaService servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== CATEGORÍAS [BASE DE DATOS] ===");
            System.out.println("1) Listar");
            System.out.println("2) Crear");
            System.out.println("3) Editar");
            System.out.println("4) Eliminar (baja lógica)");
            System.out.println("0) Volver");
            opcion = leerEntero("Seleccione: ");
            try {
                switch (opcion) {
                    case 1 -> listar();
                    case 2 -> crear();
                    case 3 -> editar();
                    case 4 -> eliminar();
                    case 0 -> { }
                    default -> System.out.println("Opción fuera de rango.");
                }
            } catch (EntidadNoEncontradaException | PersistenciaException
                     | IllegalArgumentException | IllegalStateException ex) {
                System.out.println("No se pudo realizar la operación: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void listar() {
        List<Categoria> categorias = servicio.listar();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías cargadas.");
            return;
        }
        for (Categoria categoria : categorias) {
            System.out.println(categoria);
        }
    }

    private void crear() {
        System.out.println("\n--- NUEVA CATEGORÍA ---");
        String nombre = leerTextoValidado("Nombre: ", "nombre de categoría", 100, true);
        String descripcion = leerTextoValidado(
                "Descripción: ", "descripción de categoría", 255, true);
        Categoria creada = servicio.crear(nombre, descripcion);
        System.out.println("Categoría registrada en la base. ID: " + creada.getId());
    }

    private void editar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay categorías para editar.");
            return;
        }
        listar();
        long id = leerLong("ID a editar: ");
        servicio.buscar(id);
        System.out.println("Dejá un campo vacío para conservar el valor anterior.");
        String nombre = leerTextoValidado("Nuevo nombre: ", "nombre de categoría", 100, false);
        String descripcion = leerTextoValidado(
                "Nueva descripción: ", "descripción de categoría", 255, false);
        servicio.editar(id, nombre, descripcion);
        System.out.println("Categoría actualizada correctamente.");
    }

    private void eliminar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay categorías para eliminar.");
            return;
        }
        listar();
        long id = leerLong("ID a eliminar: ");
        servicio.buscar(id);
        String respuesta = leerTexto("¿Confirmar baja lógica? (S/N): ");
        if (respuesta.equalsIgnoreCase("S")) {
            servicio.eliminar(id);
            System.out.println("Categoría eliminada lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine();
    }

    private String leerTextoValidado(String etiqueta, String campo,
                                    int maximo, boolean obligatorio) {
        while (true) {
            String valor = leerTexto(etiqueta);
            try {
                return obligatorio
                        ? ValidacionTexto.obligatorio(valor, campo, maximo)
                        : ValidacionTexto.opcional(valor, campo, maximo);
            } catch (IllegalArgumentException ex) {
                System.out.println("Dato inválido: " + ex.getMessage());
            }
        }
    }

    private int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(etiqueta).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un número entero válido.");
            }
        }
    }

    private long leerLong(String etiqueta) {
        while (true) {
            try {
                return Long.parseLong(leerTexto(etiqueta).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un ID válido.");
            }
        }
    }
}
