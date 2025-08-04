package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String titulo;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion = LocalDate.now();

    // NUEVO CAMPO: Fecha de vencimiento
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    // NUEVO CAMPO: Estado de completitud
    @Column(name = "completada")
    private Boolean completada = false;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Constructor vacío
    public Tarea() {
    }

    // Constructor sin fecha vencimiento
    public Tarea(Usuario usuario, String titulo) {
        this.titulo = titulo;
        setUsuario(usuario);
    }

    // Nuevo constructor CON fecha vencimiento
    public Tarea(Usuario usuario, String titulo, LocalDate fechaVencimiento) {
        this.titulo = titulo;
        this.fechaVencimiento = fechaVencimiento;
        setUsuario(usuario);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (this.usuario != usuario) {
            this.usuario = usuario;
            usuario.addTarea(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            return Objects.equals(id, tarea.id);
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }
}
