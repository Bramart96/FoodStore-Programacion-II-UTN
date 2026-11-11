package integrado.prog2.ui;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.PersistenciaException;
import integrado.prog2.service.ProductoService;
import java.util.List;
import java.util.Scanner;
import integrado.prog2.validation.ValidacionTexto;

/** Menú de gestión de productos persistidos por JDBC. */
public class MenuProductos {

    private final Scanner scanner;
    private final ProductoService servicio;

    public MenuProductos(Scanner scanner, ProductoService servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== PRODUCTOS [BASE DE DATOS] ===");
            System.out.println("1) Listar productos");
            System.out.println("2) Listar productos de una categoría");
            System.out.println("3) Crear producto");
            System.out.println("4) Editar producto");
            System.out.println("5) Reasignar categoría");
            System.out.println("6) Eliminar (baja lógica)");
            System.out.println("0) Volver");
            opcion = leerEntero("Seleccione: ");
            try {
                switch (opcion) {
                    case 1 -> listar();
                    case 2 -> listarPorCategoria();
                    case 3 -> crear();
                    case 4 -> editar();
                    case 5 -> reasignarCategoria();
                    case 6 -> eliminar();
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
        List<Producto> productos = servicio.listar();
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        for (Producto producto : productos) {
            System.out.println(producto);
        }
    }

    private void listarPorCategoria() {
        if (!mostrarCategorias()) {
            return;
        }
        long id = leerLong("ID de la categoría: ");
        Categoria categoria = servicio.obtenerCategoriaConProductos(id);
        System.out.println("Categoría: " + categoria.getNombre());
        if (categoria.getProductos().isEmpty()) {
            System.out.println("No hay productos activos en esta categoría.");
            return;
        }
        for (Producto producto : categoria.getProductos()) {
            System.out.println(producto);
        }
    }

    private void crear() {
        if (!mostrarCategorias()) {
            System.out.println("Primero debés crear una categoría activa.");
            return;
        }
        long idCategoria = leerLong("ID de la categoría: ");
        servicio.buscarCategoria(idCategoria);

        System.out.println("\n--- NUEVO PRODUCTO ---");
        String nombre = leerTextoValidado("Nombre: ", "nombre del producto", 120, true);
        String descripcion = leerTextoValidado(
                "Descripción: ", "descripción del producto", 255, true);
        Double precio = leerDecimal("Precio: ");
        int stock = leerEntero("Stock: ");
        String imagen = leerTextoValidado("Imagen (ruta o URL, opcional): ", "imagen", 255, false);
        boolean disponible = leerDisponibilidad();

        Producto creado = servicio.crear(
                nombre, precio, descripcion, stock, imagen, disponible, idCategoria);
        System.out.println("Producto guardado en la base. ID generado: " + creado.getId());
    }

    private void editar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay productos para editar.");
            return;
        }
        listar();
        long id = leerLong("ID del producto a editar: ");
        Producto actual = servicio.buscar(id);
        System.out.println("Editando: " + actual);
        System.out.println("Dejá vacío el campo que no quieras modificar.");

        String nombre = leerTextoValidado("Nuevo nombre: ", "nombre del producto", 120, false);
        String descripcion = leerTextoValidado(
                "Nueva descripción: ", "descripción del producto", 255, false);
        Double precio = leerDecimalOpcional("Nuevo precio: ");
        Integer stock = leerEnteroOpcional("Nuevo stock: ");
        String imagen = leerTextoValidado("Nueva imagen: ", "imagen", 255, false);
        Boolean disponible = leerDisponibilidadOpcional();
        servicio.editar(id, nombre, precio, descripcion, stock, imagen, disponible);
        System.out.println("Producto actualizado correctamente.");
    }

    private void reasignarCategoria() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay productos para reasignar.");
            return;
        }
        listar();
        long idProducto = leerLong("ID del producto: ");
        servicio.buscar(idProducto);
        if (!mostrarCategorias()) {
            return;
        }
        long idCategoria = leerLong("ID de la nueva categoría: ");
        servicio.reasignarCategoria(idProducto, idCategoria);
        System.out.println("Producto reasignado correctamente.");
    }

    private void eliminar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay productos para eliminar.");
            return;
        }
        listar();
        long id = leerLong("ID a eliminar: ");
        servicio.buscar(id);
        String respuesta = leerTexto("¿Confirmar baja lógica? (S/N): ");
        if (respuesta.equalsIgnoreCase("S")) {
            servicio.eliminar(id);
            System.out.println("Producto eliminado lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private boolean mostrarCategorias() {
        List<Categoria> categorias = servicio.listarCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas.");
            return false;
        }
        for (Categoria categoria : categorias) {
            System.out.println(categoria);
        }
        return true;
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /** Repite la pregunta si un texto está vacío o contiene solo números. */
    private String leerTextoValidado(String mensaje, String campo,
                                    int maximo, boolean obligatorio) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return obligatorio
                        ? ValidacionTexto.obligatorio(valor, campo, maximo)
                        : ValidacionTexto.opcional(valor, campo, maximo);
            } catch (IllegalArgumentException ex) {
                System.out.println("Dato inválido: " + ex.getMessage());
            }
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un número entero válido.");
            }
        }
    }

    private long leerLong(String mensaje) {
        while (true) {
            try {
                return Long.parseLong(leerTexto(mensaje).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un ID numérico válido.");
            }
        }
    }

    private Double leerDecimal(String mensaje) {
        String valor = leerTexto(mensaje).trim().replace(',', '.');
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser numérico.");
        }
    }

    private Double leerDecimalOpcional(String mensaje) {
        String valor = leerTexto(mensaje).trim();
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser numérico.");
        }
    }

    private Integer leerEnteroOpcional(String mensaje) {
        String valor = leerTexto(mensaje).trim();
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El stock debe ser un entero.");
        }
    }

    private boolean leerDisponibilidad() {
        while (true) {
            int opcion = leerEntero("Disponibilidad: 1) Sí  2) No: ");
            if (opcion == 1) {
                return true;
            }
            if (opcion == 2) {
                return false;
            }
            System.out.println("Seleccioná 1 o 2.");
        }
    }

    private Boolean leerDisponibilidadOpcional() {
        while (true) {
            String valor = leerTexto("Disponibilidad (1 = Sí, 2 = No, Enter = conservar): ").trim();
            if (valor.isEmpty()) {
                return null;
            }
            if (valor.equals("1")) {
                return true;
            }
            if (valor.equals("2")) {
                return false;
            }
            System.out.println("Seleccioná 1, 2 o Enter.");
        }
    }
}
