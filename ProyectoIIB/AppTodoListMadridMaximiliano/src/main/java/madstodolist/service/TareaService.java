package madstodolist.service;

import madstodolist.model.Tarea;
import madstodolist.repository.TareaRepository;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.dto.TareaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import java.time.LocalDate;

import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class TareaService {

    Logger logger = LoggerFactory.getLogger(TareaService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TareaData nuevaTareaUsuario(Long idUsuario, String tituloTarea, LocalDate fechaVencimiento) {
        logger.debug("Añadiendo tarea " + tituloTarea + " al usuario " + idUsuario + " con fecha de vencimiento "
                + fechaVencimiento);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario, tituloTarea, fechaVencimiento);
        tareaRepository.save(tarea);
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional(readOnly = true)
    public List<TareaData> allTareasUsuario(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        List<TareaData> tareas = usuario.getTareas().stream()
                .map(tarea -> modelMapper.map(tarea, TareaData.class))
                .collect(Collectors.toList());
        // Ordenamos la lista por id de tarea
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return tareas;
    }

    @Transactional(readOnly = true)
    public long contarTareasActivasUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe");
        }

        return usuario.getTareas().stream()
                .filter(t -> !t.getCompletada())
                .count();
    }

    @Transactional(readOnly = true)
    public TareaData findById(Long tareaId) {
        logger.debug("Buscando tarea " + tareaId);
        Tarea tarea = tareaRepository.findById(tareaId).orElse(null);
        if (tarea == null)
            return null;
        else
            return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public TareaData modificaTarea(Long idTarea, String nuevoTitulo, LocalDate nuevaFechaVencimiento) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo + " con fecha vencimiento "
                + nuevaFechaVencimiento);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTitulo(nuevoTitulo);
        tarea.setFechaVencimiento(nuevaFechaVencimiento);
        tarea = tareaRepository.save(tarea);
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public TareaData marcarCompletada(Long idTarea, Boolean completada) {
        logger.debug("Marcando tarea " + idTarea + " como completada: " + completada);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setCompletada(completada);
        tarea = tareaRepository.save(tarea);
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public void borraTarea(Long idTarea) {
        logger.debug("Borrando tarea " + idTarea);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tareaRepository.delete(tarea);
    }

    @Transactional
    public void borrarTareasMultiples(Long idUsuario, List<Long> idsTareas) {
        logger.debug("Borrando múltiples tareas del usuario " + idUsuario + ": " + idsTareas);

        if (idsTareas == null || idsTareas.isEmpty()) {
            throw new TareaServiceException("La lista de tareas a borrar no puede estar vacía");
        }

        // Verificar que todas las tareas pertenecen al usuario
        for (Long idTarea : idsTareas) {
            Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
            if (tarea == null) {
                throw new TareaServiceException("No existe tarea con id " + idTarea);
            }
            if (!tarea.getUsuario().getId().equals(idUsuario)) {
                throw new TareaServiceException("La tarea " + idTarea + " no pertenece al usuario " + idUsuario);
            }
        }

        // Si todas las validaciones pasan, borrar las tareas
        for (Long idTarea : idsTareas) {
            Tarea tarea = tareaRepository.findById(idTarea).get();
            tareaRepository.delete(tarea);
        }

        logger.debug("Se borraron " + idsTareas.size() + " tareas correctamente");
    }

    @Transactional
    public boolean usuarioContieneTarea(Long usuarioId, Long tareaId) {
        Tarea tarea = tareaRepository.findById(tareaId).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (tarea == null || usuario == null) {
            throw new TareaServiceException("No existe tarea o usuario id");
        }
        return usuario.getTareas().contains(tarea);
    }

    public String generarCSV(List<TareaData> tareas) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Título\n");

        for (TareaData tarea : tareas) {
            csv.append(String.format("%d,%s\n",
                    tarea.getId(),
                    tarea.getTitulo()));
        }

        return csv.toString();
    }
}
