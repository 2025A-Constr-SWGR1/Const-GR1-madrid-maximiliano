package madstodolist.config;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Añade al modelo “currentUser” antes de cada controlador, si hay usuario
     * logueado.
     * Thymeleaf podrá leer ${currentUser.nombre} en los fragmentos.
     * Si no hay usuario logueado, currentUser será null.
     */
    @ModelAttribute("currentUser")
    public UsuarioData currentUser() {
        Long idUsuario = managerUserSession.usuarioLogeado();
        if (idUsuario != null) {
            // UsuarioData debe contener, como mínimo, el nombre para mostrar
            return usuarioService.findById(idUsuario);
        } else {
            return null;
        }
    }
}
