package madstodolist.controller;

import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistroAdminWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsuarioService usuarioService;

    @Test
    public void muestra_checkbox_admin_si_no_existe_admin() throws Exception {
        when(usuarioService.existeAdministrador()).thenReturn(false);

        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registrarse como administrador")));
    }

    @Test
    public void no_muestra_checkbox_si_ya_hay_admin() throws Exception {
        when(usuarioService.existeAdministrador()).thenReturn(true);

        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Registrarse como administrador"))));
    }
}
