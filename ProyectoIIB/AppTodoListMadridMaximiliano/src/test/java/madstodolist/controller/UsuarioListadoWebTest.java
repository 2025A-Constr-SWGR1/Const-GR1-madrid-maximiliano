package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.authentication.ManagerUserSession;
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
public class UsuarioListadoWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ManagerUserSession managerUserSession;

    @MockBean
    UsuarioService usuarioService;

    @Test
    public void acceso_autorizado_para_admin_en_registrados() throws Exception {
        // Simulamos que el usuario 1 está logeado y es admin
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEsAdmin(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(admin);

        mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuarios Registrados")));
    }

    @Test
    public void acceso_denegado_si_no_esta_logeado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
    }
}
