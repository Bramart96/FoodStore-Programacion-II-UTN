package integrado.prog2.service;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.repository.UsuarioRepository;
import java.util.List;

/** Coordina las reglas de alta, consulta, edición y baja de usuarios. */
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio es obligatorio.");
        }
        this.repositorio = repositorio;
    }

    public Usuario crear(String nombre, String apellido, String mail,
                         String celular, String contrasena, Rol rol) {
        Usuario usuario = new Usuario(nombre, apellido, mail, celular, contrasena, rol);
        return repositorio.guardar(usuario);
    }

    public List<Usuario> listar() {
        return repositorio.listarActivos();
    }

    public Usuario buscar(long id) {
        return repositorio.buscarActivo(id);
    }

    public void cambiarNombre(long id, String nombre) {
        buscar(id).setNombre(nombre);
    }

    public void cambiarApellido(long id, String apellido) {
        buscar(id).setApellido(apellido);
    }

    public void cambiarMail(long id, String mail) {
        Usuario usuario = buscar(id);
        // El propio objeto valida el formato antes de reemplazar el valor.
        // Verificamos unicidad sin modificar el registro si existe un duplicado.
        repositorio.validarMailDisponible(mail, usuario.getId());
        usuario.setMail(mail);
    }

    public void cambiarCelular(long id, String celular) {
        buscar(id).setCelular(celular);
    }

    public void cambiarContrasena(long id, String contrasena) {
        buscar(id).setContrasena(contrasena);
    }

    public void cambiarRol(long id, Rol rol) {
        buscar(id).setRol(rol);
    }

    public void eliminar(long id) {
        repositorio.eliminar(id);
    }
}
