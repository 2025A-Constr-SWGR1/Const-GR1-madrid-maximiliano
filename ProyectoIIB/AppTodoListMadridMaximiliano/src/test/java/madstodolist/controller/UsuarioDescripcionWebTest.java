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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioDescripcionWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ManagerUserSession managerUserSession;

    @MockBean
    UsuarioService usuarioService;

    @Test
    public void admin_puede_ver_descripcion_usuario() throws Exception {
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEsAdmin(true);

        UsuarioData usuarioConsultado = new UsuarioData();
        usuarioConsultado.setId(2L);
        usuarioConsultado.setEmail("test@ua");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(admin);
        when(usuarioService.findById(2L)).thenReturn(usuarioConsultado);

        mockMvc.perform(get("/registrados/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test@ua")));
    }

    @Test
    public void acceso_denegado_si_no_es_admin() throws Exception {
        UsuarioData normalUser = new UsuarioData();
        normalUser.setId(3L);
        normalUser.setEsAdmin(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(3L);
        when(usuarioService.findById(3L)).thenReturn(normalUser);

        mockMvc.perform(get("/registrados/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void redirige_si_usuario_no_existe() throws Exception {
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEsAdmin(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(admin);
        when(usuarioService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/registrados/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados?error=notfound"));
    }
}
