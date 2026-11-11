package integrado.prog2.repository;

import integrado.prog2.entities.Usuario;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.MailDuplicadoException;
import java.util.ArrayList;
import java.util.List;

/** Almacena usuarios durante una ejecución, sin utilizar base de datos. */
public class UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();
    private long siguienteId = 1;

    public Usuario guardar(Usuario usuario) {
        if (usuario == null || usuario.getId() != null) {
            throw new IllegalArgumentException("Debe indicar un usuario nuevo.");
        }
        validarMailDisponible(usuario.getMail(), null);
        usuario.asignarId(siguienteId++);
        usuarios.add(usuario);
        return usuario;
    }

    public List<Usuario> listarActivos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (!usuario.isEliminado()) {
                activos.add(usuario);
            }
        }
        return List.copyOf(activos);
    }

    public Usuario buscarActivo(long id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id) && !usuario.isEliminado()) {
                return usuario;
            }
        }
        throw new EntidadNoEncontradaException("No existe un usuario activo con ID " + id + ".");
    }

    /** El ID indicado se excluye al editar el correo del propio usuario. */
    public void validarMailDisponible(String mail, Long idExcluido) {
        if (mail == null) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        for (Usuario usuario : usuarios) {
            boolean esOtro = idExcluido == null || !usuario.getId().equals(idExcluido);
            if (esOtro && usuario.getMail().equalsIgnoreCase(mail.trim())) {
                throw new MailDuplicadoException("El correo ya está registrado por otro usuario.");
            }
        }
    }

    public void eliminar(long id) {
        buscarActivo(id).eliminar();
    }
}
