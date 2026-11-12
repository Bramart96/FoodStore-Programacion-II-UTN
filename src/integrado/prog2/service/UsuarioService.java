package integrado.prog2.service;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.repository.UsuarioRepository;
import java.util.List;
import integrado.prog2.validation.ValidacionTexto;

/** Coordina las reglas de alta, consulta, edición y baja de usuarios. */
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio es obligatorio.");
        }
        this.repositorio = repositorio;
    }

    /** Crea un usuario en memoria tras validar sus datos y la unicidad de su correo. */
    public Usuario crear(String nombre, String apellido, String mail,
                         String celular, String contrasena, Rol rol) {
        Usuario usuario = new Usuario(
                ValidacionTexto.obligatorio(nombre, "nombre del usuario", 100),
                ValidacionTexto.obligatorio(apellido, "apellido del usuario", 100),
                mail, celular, contrasena, rol);
        return repositorio.guardar(usuario);
    }

    /** Devuelve los usuarios que no han sido dados de baja. */
    public List<Usuario> listar() {
        return repositorio.listarActivos();
    }

    /** Busca un usuario activo; el repositorio informa si no existe. */
    public Usuario buscar(long id) {
        return repositorio.buscarActivo(id);
    }

    public void cambiarNombre(long id, String nombre) {
        buscar(id).setNombre(ValidacionTexto.obligatorio(
                nombre, "nombre del usuario", 100));
    }

    public void cambiarApellido(long id, String apellido) {
        buscar(id).setApellido(ValidacionTexto.obligatorio(
                apellido, "apellido del usuario", 100));
    }

    /** Actualiza el correo sin permitir duplicados, excluyendo al usuario que se edita. */
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

    /** Marca al usuario como eliminado sin borrar su historial de pedidos. */
    public void eliminar(long id) {
        repositorio.eliminar(id);
    }
}
