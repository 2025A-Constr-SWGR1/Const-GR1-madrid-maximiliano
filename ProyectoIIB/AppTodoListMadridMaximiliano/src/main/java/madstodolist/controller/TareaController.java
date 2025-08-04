package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TareaController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado)) {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable("id") Long idUsuario,
            Model model) {

        comprobarUsuarioLogeado(idUsuario);

        // Inicializamos tareaData y asignamos usuarioId para el formulario
        TareaData tareaData = new TareaData();
        tareaData.setUsuarioId(idUsuario);

        model.addAttribute("tareaData", tareaData);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);

        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable("id") Long idUsuario,
            @ModelAttribute TareaData tareaData,
            RedirectAttributes flash) {

        try {
            comprobarUsuarioLogeado(idUsuario);

            // Debug para ver qué datos llegan
            System.out.println("DEBUG - Usuario ID: " + idUsuario);
            System.out.println("DEBUG - Título: " + tareaData.getTitulo());
            System.out.println("DEBUG - Fecha vencimiento: " + tareaData.getFechaVencimiento());
            System.out.println("DEBUG - Usuario ID del DTO: " + tareaData.getUsuarioId());

            // Crear la tarea
            tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo(), tareaData.getFechaVencimiento());

            flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
            return "redirect:/usuarios/" + idUsuario + "/tareas";

        } catch (Exception e) {
            System.err.println("ERROR al crear tarea: " + e.getMessage());
            e.printStackTrace();

            flash.addFlashAttribute("error", "Error al crear la tarea: " + e.getMessage());
            return "redirect:/usuarios/" + idUsuario + "/tareas/nueva";
        }
    }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable("id") Long idUsuario,
            Model model) {

        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
        long tareasActivas = tareaService.contarTareasActivasUsuario(idUsuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("tareas", tareas);
        model.addAttribute("listaVacia", tareas.isEmpty());
        model.addAttribute("tareasActivas", tareasActivas);

        return "listaTareas";
    }

    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable("id") Long idTarea, Model model) {
        TareaData tareaData = tareaService.findById(idTarea);
        if (tareaData == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tareaData.getUsuarioId());

        model.addAttribute("tareaData", tareaData);
        model.addAttribute("tarea", tareaData); // 🔧 Necesario para el botón "Cancelar"

        UsuarioData usuario = usuarioService.findById(tareaData.getUsuarioId());
        model.addAttribute("usuario", usuario);

        return "formEditarTarea";
    }

    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable("id") Long idTarea,
            @ModelAttribute TareaData tareaData,
            RedirectAttributes flash) {

        TareaData tareaExistente = tareaService.findById(idTarea);
        if (tareaExistente == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tareaExistente.getUsuarioId();
        comprobarUsuarioLogeado(idUsuario);

        try {
            tareaService.modificaTarea(idTarea, tareaData.getTitulo(), tareaData.getFechaVencimiento());
            flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
            return "redirect:/usuarios/" + idUsuario + "/tareas";

        } catch (Exception e) {
            e.printStackTrace();
            flash.addFlashAttribute("error", "Error al modificar la tarea: " + e.getMessage());
            return "redirect:/tareas/" + idTarea + "/editar";
        }
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    public String borrarTarea(@PathVariable("id") Long idTarea) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        tareaService.borraTarea(idTarea);
        return "";
    }

    @PostMapping("/tareas/{id}/completar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marcarCompletada(@PathVariable("id") Long idTarea,
            @RequestParam Boolean completada) {
        TareaData tarea = tareaService.marcarCompletada(idTarea, completada);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tarea actualizada correctamente");
        response.put("completada", tarea.getCompletada());
        response.put("tareaId", tarea.getId()); // Añadimos el ID para referencia en frontend

        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios/{id}/tareas/borrar-multiples")
    public String borrarMultiplesTareas(@PathVariable("id") Long idUsuario,
            @RequestParam String tareasIds,
            RedirectAttributes flash) {

        comprobarUsuarioLogeado(idUsuario);

        String[] ids = tareasIds.split(",");
        for (String id : ids) {
            if (!id.isEmpty()) {
                tareaService.borraTarea(Long.parseLong(id));
            }
        }

        flash.addFlashAttribute("mensaje", "Tareas borradas correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
    }

    @DeleteMapping("/tareas/multiple")
    @ResponseBody
    public String borrarTareasMultiples(@RequestBody List<Long> idsTareas) {

        if (idsTareas == null || idsTareas.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una tarea para borrar");
        }

        Long usuarioLogueado = managerUserSession.usuarioLogeado();
        if (usuarioLogueado == null) {
            throw new UsuarioNoLogeadoException();
        }

        for (Long idTarea : idsTareas) {
            TareaData tarea = tareaService.findById(idTarea);
            if (tarea == null) {
                throw new TareaNotFoundException();
            }
            comprobarUsuarioLogeado(tarea.getUsuarioId());
        }

        for (Long idTarea : idsTareas) {
            tareaService.borraTarea(idTarea);
        }

        return "{\"mensaje\":\"Tareas borradas correctamente\",\"cantidad\":" + idsTareas.size() + "}";
    }

    @PostMapping("/tareas/test-multiple")
    @ResponseBody
    public String testBorradoMultiple(@RequestBody List<Long> idsTareas) {
        return "{\"mensaje\":\"Test exitoso\",\"usuario\":" + managerUserSession.usuarioLogeado() + ",\"tareas\":"
                + idsTareas.size() + "}";
    }

    @GetMapping("/usuarios/{id}/tareas/exportar")
    public void exportarTareas(@PathVariable("id") Long idUsuario,
            HttpServletResponse response) throws IOException {

        List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"tareas_usuario_" + idUsuario + ".csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,Título,Fecha Vencimiento");

            for (TareaData tarea : tareas) {
                writer.println(String.format("%d,%s,%s",
                        tarea.getId(),
                        tarea.getTitulo(),
                        tarea.getFechaVencimiento() != null ? tarea.getFechaVencimiento().toString() : ""));
            }
        }
    }
        // ⚠️ Manejo de excepciones de servicio cuando no se encuentra la tarea
    @ExceptionHandler(madstodolist.service.TareaServiceException.class)
    @ResponseBody
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public Map<String, String> handleTareaServiceException(madstodolist.service.TareaServiceException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}
