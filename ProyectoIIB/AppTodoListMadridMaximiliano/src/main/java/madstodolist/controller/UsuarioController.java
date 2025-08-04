package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import madstodolist.authentication.ManagerUserSession;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ManagerUserSession managerUserSession;

    public UsuarioController(UsuarioService usuarioService, ManagerUserSession managerUserSession) {
        this.usuarioService = usuarioService;
        this.managerUserSession = managerUserSession;
    }

    @GetMapping("/registrados")
    public String verUsuariosRegistrados(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás logeado");
        }

        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
        if (!Boolean.TRUE.equals(usuario.getEsAdmin())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso no autorizado. Solo administradores.");
        }

        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "registrados";
    }

    @GetMapping("/registrados/{id}")
    public String verDescripcionUsuario(@PathVariable Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás logeado");
        }

        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
        if (!Boolean.TRUE.equals(usuario.getEsAdmin())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso no autorizado. Solo administradores.");
        }

        UsuarioData usuarioConsultado = usuarioService.findById(id);
        if (usuarioConsultado == null) {
            return "redirect:/registrados?error=notfound";
        }

        model.addAttribute("usuario", usuarioConsultado);
        return "descripcionUsuario";
    }

    @PostMapping("/registrados/{id}/toggle-bloqueo")
    public String toggleBloqueoUsuario(@PathVariable Long id) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás logeado");
        }

        UsuarioData admin = usuarioService.findById(idUsuarioLogeado);
        if (!Boolean.TRUE.equals(admin.getEsAdmin())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Solo el administrador puede realizar esta acción");
        }

        if (idUsuarioLogeado.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes bloquearte a ti mismo");
        }

        usuarioService.toggleBloqueoUsuario(id);
        return "redirect:/registrados";
    }

}
