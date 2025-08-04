package madstodolist.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

public class TareaData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "El título es obligatorio")
    @Size(min = 1, max = 100, message = "El título debe tener entre 1 y 100 caracteres")
    private String titulo;

    private Long usuarioId;

    private LocalDate fechaCreacion;

    // ESTA ES LA LÍNEA CLAVE QUE FALTABA
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaVencimiento;

    // NUEVO CAMPO: Estado de completitud
    private Boolean completada = false;

    // Constructor vacío
    public TareaData() {
    }

    // Constructor con parámetros
    public TareaData(Long id, String titulo, Long usuarioId, LocalDate fechaCreacion, LocalDate fechaVencimiento) {
        this.id = id;
        this.titulo = titulo;
        this.usuarioId = usuarioId;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.completada = false;
    }

    // Getters y setters
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TareaData))
            return false;
        TareaData tareaData = (TareaData) o;
        return Objects.equals(id, tareaData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TareaData{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", usuarioId=" + usuarioId +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaVencimiento=" + fechaVencimiento +
                ", completada=" + completada +
                '}';
    }
}