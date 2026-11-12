package integrado.prog2.ui;

import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.service.PedidoService;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.PersistenciaException;
import java.util.List;
import java.util.Scanner;

/**
 * Menú de pedidos en memoria. Los productos se consultan por JDBC
 * y la confirmación actualiza el stock por una transacción en el DAO.
 */
public class MenuPedidos {

    private final Scanner scanner;
    private final PedidoService servicio;

    public MenuPedidos(Scanner scanner, PedidoService servicio) {
        if (scanner == null || servicio == null) {
            throw new IllegalArgumentException("Faltan datos para el menú de pedidos.");
        }
        this.scanner = scanner;
        this.servicio = servicio;
    }

    /** Gestiona las operaciones de pedidos y vuelve al menú tras cada operación. */
    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== PEDIDOS Y DETALLES [MEMORIA] ===");
            System.out.println("1) Listar pedidos activos");
            System.out.println("2) Consultar pedidos por usuario (incluye historial)");
            System.out.println("3) Crear pedido y gestionar sus detalles");
            System.out.println("4) Cambiar forma de pago");
            System.out.println("5) Actualizar estado del pedido");
            System.out.println("6) Eliminar pedido (baja lógica)");
            System.out.println("0) Volver");
            opcion = leerEntero("Seleccione: ");
            try {
                switch (opcion) {
                    case 1 -> listar();
                    case 2 -> listarPorUsuario();
                    case 3 -> crear();
                    case 4 -> cambiarPago();
                    case 5 -> cambiarEstado();
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
        List<Usuario> usuarios = servicio.listarUsuariosConPedidos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay pedidos activos en memoria.");
            return;
        }
        for (Usuario usuario : usuarios) {
            for (Pedido pedido : usuario.getPedidos()) {
                if (!pedido.isEliminado()) {
                    mostrarPedido(usuario, pedido);
                }
            }
        }
    }

    /** Consulta el historial de un usuario, incluso si fue dado de baja. */
    private void listarPorUsuario() {
        long id = leerLong("ID del usuario (activo o eliminado): ");
        Usuario usuario = servicio.consultarHistorial(id);
        if (usuario.getPedidos().isEmpty()) {
            System.out.println("No hay pedidos para este usuario.");
            return;
        }
        for (Pedido pedido : usuario.getPedidos()) {
            mostrarPedido(usuario, pedido);
        }
    }

    /** Presenta el estado, forma de pago, total y detalles históricos del pedido. */
    private void mostrarPedido(Usuario usuario, Pedido pedido) {
        String situacion = pedido.isEliminado() ? " [BAJA LÓGICA]" : "";
        String usuarioEliminado = usuario.isEliminado() ? " [USUARIO ELIMINADO]" : "";
        System.out.println("\nPedido ID " + pedido.getId() + situacion);
        System.out.println("Usuario: " + usuario.getId() + " - "
                + usuario.getNombre() + " " + usuario.getApellido() + usuarioEliminado);
        System.out.println("Fecha: " + pedido.getFecha()
                + " | Estado: " + pedido.getEstado()
                + " | Pago: " + pedido.getFormaPago()
                + " | Total: " + pedido.getTotal());

        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.println("  - " + detalle.getProducto().getNombre()
                    + " (ID " + detalle.getProducto().getId() + ")"
                    + ", cantidad: " + detalle.getCantidad()
                    + ", subtotal: " + detalle.getSubtotal()
                    + (detalle.isEliminado() ? " [DETALLE ELIMINADO]" : ""));
        }
    }

    /** Guía la creación del borrador y su confirmación con descuento de stock. */
    private void crear() {
        List<Usuario> usuarios = servicio.listarUsuariosActivos();
        if (usuarios.isEmpty()) {
            System.out.println("Primero debés crear un usuario activo desde el menú Usuarios.");
            return;
        }
        System.out.println("\n--- NUEVO PEDIDO ---");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
        long usuarioId = leerLong("ID del usuario: ");
        FormaPago pago = seleccionarPago();
        Pedido borrador = servicio.iniciar(usuarioId, pago);

        int opcion;
        do {
            System.out.println("\n--- DETALLES DEL PEDIDO (BORRADOR EN MEMORIA) ---");
            System.out.println("1) Ver productos");
            System.out.println("2) Agregar detalle");
            System.out.println("3) Modificar cantidad");
            System.out.println("4) Quitar detalle");
            System.out.println("5) Ver resumen");
            System.out.println("6) Confirmar pedido y descontar stock");
            System.out.println("0) Descartar borrador");
            opcion = leerEntero("Seleccione: ");

            try {
                switch (opcion) {
                    case 1 -> mostrarProductos();
                    case 2 -> agregarDetalle(borrador);
                    case 3 -> modificarDetalle(borrador);
                    case 4 -> quitarDetalle(borrador);
                    case 5 -> mostrarResumen(borrador);
                    case 6 -> {
                        if (borrador.getDetallesActivos().isEmpty()) {
                            System.out.println("Agregá al menos un detalle antes de confirmar.");
                            break;
                        }
                        mostrarResumen(borrador);
                        String respuesta = leerTexto("¿Confirmar pedido? (S/N): ");
                        if (!respuesta.equalsIgnoreCase("S")) {
                            System.out.println("El borrador continúa disponible.");
                            break;
                        }
                        // Ante una falla de stock se revierte la transacción
                        // y se descarta el borrador: no queda un pedido registrado.
                        try {
                            Pedido confirmado = servicio.confirmar(usuarioId, borrador);
                            System.out.println("Pedido confirmado en memoria. ID: "
                                    + confirmado.getId() + ". Stock actualizado en MySQL.");
                        } catch (EntidadNoEncontradaException | PersistenciaException
                         | IllegalArgumentException | IllegalStateException ex) {
                            System.out.println("No se confirmó el pedido: " + ex.getMessage());
                            System.out.println("El borrador fue descartado sin guardarse.");
                        }
                        return;
                    }
                    case 0 -> System.out.println("Borrador descartado. No se descontó stock.");
                    default -> System.out.println("Opción fuera de rango.");
                }
            } catch (EntidadNoEncontradaException | PersistenciaException
                         | IllegalArgumentException | IllegalStateException ex) {
                System.out.println("No se pudo realizar la operación: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarProductos() {
        List<Producto> productos = servicio.listarProductos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        for (Producto producto : productos) {
            if (Boolean.TRUE.equals(producto.getDisponible())) {
                System.out.println(producto);
            }
        }
    }

    /** Solicita un producto y cantidad para incorporar al borrador. */
    private void agregarDetalle(Pedido borrador) {
        mostrarProductos();
        long idProducto = leerLong("ID del producto: ");
        int cantidad = leerEntero("Cantidad: ");
        servicio.agregarDetalle(borrador, idProducto, cantidad);
        System.out.println("Detalle agregado al pedido.");
    }

    /** Solicita una nueva cantidad para una línea del borrador. */
    private void modificarDetalle(Pedido borrador) {
        mostrarResumen(borrador);
        long id = leerLong("ID del producto a modificar: ");
        int cantidad = leerEntero("Nueva cantidad: ");
        servicio.cambiarCantidad(borrador, id, cantidad);
        System.out.println("Cantidad actualizada.");
    }

    /** Permite dar de baja una línea antes de confirmar el pedido. */
    private void quitarDetalle(Pedido borrador) {
        mostrarResumen(borrador);
        long id = leerLong("ID del producto a quitar: ");
        servicio.eliminarDetalle(borrador, id);
        System.out.println("Detalle eliminado lógicamente del borrador.");
    }

    /** Muestra el total recalculado a partir de los detalles activos. */
    private void mostrarResumen(Pedido borrador) {
        borrador.calcularTotal();
        System.out.println("\nResumen del pedido:");
        if (borrador.getDetallesActivos().isEmpty()) {
            System.out.println("Todavía no tiene detalles activos.");
        } else {
            for (DetallePedido detalle : borrador.getDetallesActivos()) {
                System.out.println("  - " + detalle.getProducto().getNombre()
                        + " x" + detalle.getCantidad()
                        + " = " + detalle.getSubtotal());
            }
        }
        System.out.println("Total: " + borrador.getTotal()
                + " | Forma de pago: " + borrador.getFormaPago());
    }

    /** Solicita un nuevo medio de pago para el pedido seleccionado. */
    private void cambiarPago() {
        if (!hayPedidosActivos()) {
            return;
        }
        listar();
        long id = leerLong("ID del pedido a editar: ");
        servicio.buscar(id);
        servicio.actualizarPago(id, seleccionarPago());
        System.out.println("Forma de pago actualizada.");
    }

    /** Permite indicar si el pedido se terminó o se canceló. */
    private void cambiarEstado() {
        if (!hayPedidosActivos()) {
            return;
        }
        listar();
        long id = leerLong("ID del pedido: ");
        servicio.buscar(id);
        System.out.println("1) TERMINADO    2) CANCELADO    0) Volver");
        int opcion = leerEntero("Nuevo estado: ");
        if (opcion == 1) {
            servicio.actualizarEstado(id, Estado.TERMINADO);
            System.out.println("Pedido terminado.");
        } else if (opcion == 2) {
            servicio.actualizarEstado(id, Estado.CANCELADO);
            System.out.println("Pedido cancelado. Stock repuesto en MySQL.");
        } else if (opcion == 0) {
            System.out.println("Sin cambios.");
        } else {
            System.out.println("Opción fuera de rango.");
        }
    }

    /** Confirma la baja lógica del pedido y delega la reposición cuando corresponde. */
    private void eliminar() {
        if (!hayPedidosActivos()) {
            return;
        }
        listar();
        long id = leerLong("ID del pedido a eliminar: ");
        servicio.buscar(id);
        String respuesta = leerTexto("¿Confirmar baja lógica del pedido? (S/N): ");
        if (respuesta.equalsIgnoreCase("S")) {
            servicio.eliminar(id);
            System.out.println("Pedido eliminado lógicamente.");
            System.out.println("Se repuso stock solo si el pedido estaba CONFIRMADO.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private boolean hayPedidosActivos() {
        if (servicio.listarUsuariosConPedidos().isEmpty()) {
            System.out.println("No hay pedidos activos en memoria.");
            return false;
        }
        return true;
    }

    private FormaPago seleccionarPago() {
        while (true) {
            System.out.println("Forma de pago: 1) TARJETA  2) TRANSFERENCIA  3) EFECTIVO");
            int opcion = leerEntero("Seleccione: ");
            switch (opcion) {
                case 1 -> { return FormaPago.TARJETA; }
                case 2 -> { return FormaPago.TRANSFERENCIA; }
                case 3 -> { return FormaPago.EFECTIVO; }
                default -> System.out.println("Seleccioná 1, 2 o 3.");
            }
        }
    }

    private String leerTexto(String pregunta) {
        System.out.print(pregunta);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String pregunta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(pregunta));
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un número entero válido.");
            }
        }
    }

    private long leerLong(String pregunta) {
        while (true) {
            try {
                return Long.parseLong(leerTexto(pregunta));
            } catch (NumberFormatException ex) {
                System.out.println("Ingresá un ID numérico válido.");
            }
        }
    }
}
