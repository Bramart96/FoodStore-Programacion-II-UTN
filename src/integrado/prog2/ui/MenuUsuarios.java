package integrado.prog2.ui;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.service.UsuarioService;
import java.util.List;
import java.util.Scanner;
import integrado.prog2.validation.ValidacionTexto;

/** Menú CRUD de usuarios; solo interactúa con la consola. */
public class MenuUsuarios {

    private final Scanner scanner;
    private final UsuarioService servicio;

    public MenuUsuarios(Scanner scanner, UsuarioService servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== USUARIOS [MEMORIA] ===");
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
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("No se pudo realizar la operación: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void listar() {
        List<Usuario> usuarios = servicio.listar();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados en memoria.");
            return;
        }
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    private void crear() {
        System.out.println("\n--- CREAR USUARIO [MEMORIA] ---");
        String nombre = leerNombre("Nombre: ", "nombre del usuario");
        String apellido = leerNombre("Apellido: ", "apellido del usuario");
        String mail = leerTexto("Email: ");
        String celular = leerTexto("Celular: ");
        String contrasena = leerTexto("Contraseña: ");
        Rol rol = seleccionarRol();
        Usuario usuario = servicio.crear(nombre, apellido, mail, celular, contrasena, rol);
        System.out.println("Usuario creado en memoria. ID generado: " + usuario.getId());
    }

    private void editar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay usuarios para editar.");
            return;
        }
        listar();
        long id = leerLong("ID del usuario: ");
        servicio.buscar(id);
        System.out.println("1) Nombre   2) Apellido   3) Email");
        System.out.println("4) Celular  5) Contraseña 6) Rol   0) Cancelar");
        int opcion = leerEntero("Campo: ");
        switch (opcion) {
            case 1 -> servicio.cambiarNombre(id, leerNombre("Nuevo nombre: ", "nombre del usuario"));
            case 2 -> servicio.cambiarApellido(id, leerNombre("Nuevo apellido: ", "apellido del usuario"));
            case 3 -> servicio.cambiarMail(id, leerTexto("Nuevo email: "));
            case 4 -> servicio.cambiarCelular(id, leerTexto("Nuevo celular: "));
            case 5 -> servicio.cambiarContrasena(id, leerTexto("Nueva contraseña: "));
            case 6 -> servicio.cambiarRol(id, seleccionarRol());
            case 0 -> { return; }
            default -> {
                System.out.println("Campo fuera de rango.");
                return;
            }
        }
        System.out.println("Usuario actualizado correctamente.");
    }

    private void eliminar() {
        if (servicio.listar().isEmpty()) {
            System.out.println("No hay usuarios para eliminar.");
            return;
        }
        listar();
        long id = leerLong("ID a eliminar: ");
        servicio.buscar(id);
        String respuesta = leerTexto("¿Confirma la baja lógica? (S/N): ");
        if (respuesta.equalsIgnoreCase("S")) {
            servicio.eliminar(id);
            System.out.println("Usuario eliminado lógicamente. Se conserva su historial.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Rol seleccionarRol() {
        while (true) {
            System.out.println("Rol: 1) ADMIN  2) USUARIO");
            int numero = leerEntero("Opción: ");
            if (numero == 1) {
                return Rol.ADMIN;
            }
            if (numero == 2) {
                return Rol.USUARIO;
            }
            System.out.println("Debe seleccionar 1 o 2.");
        }
    }

    private String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine();
    }

    private String leerNombre(String etiqueta, String campo) {
        while (true) {
            String valor = leerTexto(etiqueta);
            try {
                return ValidacionTexto.obligatorio(valor, campo, 100);
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
                System.out.println("Ingresá un ID numérico válido.");
            }
        }
    }
}
