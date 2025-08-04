package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BloqueoUsuarioWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ManagerUserSession managerUserSession;

    @MockBean
    UsuarioService usuarioService;

    @Test
    public void admin_puede_bloquear_usuario() throws Exception {
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEsAdmin(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(admin);

        mockMvc.perform(post("/registrados/2/toggle-bloqueo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));

        verify(usuarioService).toggleBloqueoUsuario(2L);
    }

    @Test
    public void usuario_bloqueado_no_puede_hacer_login() throws Exception {
        UsuarioData bloqueado = new UsuarioData();
        bloqueado.setId(2L);
        bloqueado.setEmail("bloq@ua");
        bloqueado.setPassword("1234");
        bloqueado.setBloqueado(true);

        when(usuarioService.login("bloq@ua", "1234"))
                .thenReturn(UsuarioService.LoginStatus.USER_BLOCKED);

        mockMvc.perform(post("/login")
                .param("eMail", "bloq@ua")
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Tu cuenta ha sido bloqueada por el administrador.")
                ));
    }
}
