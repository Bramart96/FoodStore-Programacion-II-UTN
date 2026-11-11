package integrado.prog2.entities;

import java.time.LocalDateTime;

/**
 * Superclase de las entidades del sistema.
 * Define el identificador, la baja lógica y la fecha de creación.
 */
public abstract class Base {

    private Long id;
    private boolean eliminado;
    private final LocalDateTime createdAt;

    protected Base() {
        this(null, false, LocalDateTime.now());
    }

    /**
     * Constructor destinado a recuperar una entidad persistida.
     */
    protected Base(Long id, boolean eliminado, LocalDateTime createdAt) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula.");
        }
        this.id = id;
        this.eliminado = eliminado;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Asigna el identificador generado por la base o por el repositorio.
     */
    public void asignarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo.");
        }
        if (this.id != null) {
            throw new IllegalStateException("El id ya fue asignado.");
        }
        this.id = id;
    }

    /**
     * Conserva la entidad y la marca como eliminada.
     */
    public void eliminar() {
        eliminado = true;
    }
}
