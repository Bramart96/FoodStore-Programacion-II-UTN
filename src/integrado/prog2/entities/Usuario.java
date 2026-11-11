package integrado.prog2.entities;

import integrado.prog2.enums.Rol;
import java.util.ArrayList;
import java.util.List;

/**
 * Usuario administrado en memoria. Conserva los pedidos asociados,
 * sin agregar una referencia inversa desde Pedido.
 */
public class Usuario extends Base {

    private String nombre;
    private String apellido;
    private String mail;
    private String celular;
    private String contrasena;
    private Rol rol;
    private final List<Pedido> pedidos = new ArrayList<>();

    public Usuario(String nombre, String apellido, String mail,
                   String celular, String contrasena, Rol rol) {
        setNombre(nombre);
        setApellido(apellido);
        setMail(mail);
        setCelular(celular);
        setContrasena(contrasena);
        setRol(rol);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido del usuario es obligatorio.");
        }
        this.apellido = apellido.trim();
    }

    public String getMail() {
        return mail;
    }

    /** Valida un formato básico; la unicidad se controla en el repositorio. */
    public void setMail(String mail) {
        if (mail == null || mail.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        String valor = mail.trim();
        int arroba = valor.indexOf('@');
        int punto = valor.lastIndexOf('.');
        if (arroba <= 0 || arroba != valor.lastIndexOf('@')
                || punto <= arroba + 1 || punto >= valor.length() - 1
                || valor.contains(" ")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        this.mail = valor;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular == null ? "" : celular.trim();
    }

    /** La contraseña no se muestra en el listado de usuarios. */
    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        this.rol = rol;
    }

    /** Conserva también los pedidos eliminados para el historial. */
    public List<Pedido> getPedidos() {
        return List.copyOf(pedidos);
    }

    public void agregarPedido(Pedido pedido) {
        if (isEliminado()) {
            throw new IllegalStateException("No se pueden agregar pedidos a un usuario eliminado.");
        }
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        if (!pedidos.contains(pedido)) {
            pedidos.add(pedido);
        }
    }

    @Override
    public String toString() {
        return "Usuario {id=" + getId() + ", nombre='" + nombre
                + "', apellido='" + apellido + "', mail='" + mail
                + "', rol=" + rol + "}";
    }
}
