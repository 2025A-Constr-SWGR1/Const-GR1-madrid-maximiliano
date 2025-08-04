package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class TareaWebTest {

        @Autowired
        private MockMvc mockMvc;

        // Declaramos los servicios como Autowired
        @Autowired
        private TareaService tareaService;

        @Autowired
        private UsuarioService usuarioService;

        // Moqueamos el managerUserSession para poder moquear el usuario logeado
        @MockBean
        private ManagerUserSession managerUserSession;

        // Método para inicializar los datos de prueba en la BD
        // Devuelve un mapa con los identificadores del usuario y de la primera tarea
        // añadida

        Map<String, Long> addUsuarioTareasBD() {
                // Añadimos un usuario a la base de datos
                UsuarioData usuario = new UsuarioData();
                usuario.setEmail("user@ua");
                usuario.setPassword("123");
                usuario = usuarioService.registrar(usuario);

                // Y añadimos dos tareas asociadas a ese usuario
                TareaData tarea1 = tareaService.nuevaTareaUsuario(usuario.getId(), "Lavar coche", java.time.LocalDate.now());
                tareaService.nuevaTareaUsuario(usuario.getId(), "Renovar DNI", java.time.LocalDate.now());

                // Devolvemos los ids del usuario y de la primera tarea añadida
                Map<String, Long> ids = new HashMap<>();
                ids.put("usuarioId", usuario.getId());
                ids.put("tareaId", tarea1.getId());
                return ids;

        }

        @Test
        public void listaTareas() throws Exception {
                // GIVEN
                // Un usuario con dos tareas en la BD
                Long usuarioId = addUsuarioTareasBD().get("usuarioId");

                // Moqueamos el método usuarioLogeado para que devuelva el usuario 1L,
                // el mismo que se está usando en la petición. De esta forma evitamos
                // que salte la excepción de que el usuario que está haciendo la
                // petición no está logeado.
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                // se realiza la petición GET al listado de tareas del usuario,
                // el HTML devuelto contiene las descripciones de sus tareas.

                String url = "/usuarios/" + usuarioId.toString() + "/tareas";

                this.mockMvc.perform(get(url))
                                .andExpect((content().string(allOf(
                                                containsString("Lavar coche"),
                                                containsString("Renovar DNI")))));
        }

        @Test
        public void getNuevaTareaDevuelveForm() throws Exception {
                // GIVEN
                // Un usuario con dos tareas en la BD
                Long usuarioId = addUsuarioTareasBD().get("usuarioId");

                // Ver el comentario en el primer test
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                // si ejecutamos una petición GET para crear una nueva tarea de un usuario,
                // el HTML resultante contiene un formulario y la ruta con
                // la acción para crear la nueva tarea.

                String urlPeticion = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
                String urlAction = "action=\"/usuarios/" + usuarioId.toString() + "/tareas/nueva\"";

                this.mockMvc.perform(get(urlPeticion))
                                .andExpect((content().string(allOf(
                                                containsString("form method=\"post\""),
                                                containsString(urlAction)))));
        }

        @Test
        public void postNuevaTareaDevuelveRedirectYAñadeTarea() throws Exception {
                // GIVEN
                // Un usuario con dos tareas en la BD
                Long usuarioId = addUsuarioTareasBD().get("usuarioId");

                // Ver el comentario en el primer test
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                // realizamos la petición POST para añadir una nueva tarea,
                // el estado HTTP que se devuelve es un REDIRECT al listado
                // de tareas.

                String urlPost = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
                String urlRedirect = "/usuarios/" + usuarioId.toString() + "/tareas";

                this.mockMvc.perform(post(urlPost)
                                .param("titulo", "Estudiar examen MADS"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl(urlRedirect));

                // y si después consultamos el listado de tareas con una petición
                // GET el HTML contiene la tarea añadida.

                this.mockMvc.perform(get(urlRedirect))
                                .andExpect((content().string(containsString("Estudiar examen MADS"))));
        }

        @Test
        public void deleteTareaDevuelveOKyBorraTarea() throws Exception {
                // GIVEN
                // Un usuario con dos tareas en la BD
                Map<String, Long> ids = addUsuarioTareasBD();
                Long usuarioId = ids.get("usuarioId");
                Long tareaLavarCocheId = ids.get("tareaId");

                // Ver el comentario en el primer test
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                // realizamos la petición DELETE para borrar una tarea,
                // se devuelve el estado HTTP que se devuelve es OK,

                String urlDelete = "/tareas/" + tareaLavarCocheId.toString();

                this.mockMvc.perform(delete(urlDelete))
                                .andExpect(status().isOk());

                // y cuando se pide un listado de tareas del usuario, la tarea borrada ya no
                // aparece.

                String urlListado = "/usuarios/" + usuarioId + "/tareas";

                this.mockMvc.perform(get(urlListado))
                                .andExpect(content().string(
                                                allOf(not(containsString("Lavar coche")),
                                                                containsString("Renovar DNI"))));
        }

        @Test
        public void editarTareaActualizaLaTarea() throws Exception {
                // GIVEN
                // Un usuario con dos tareas en la BD
                Map<String, Long> ids = addUsuarioTareasBD();
                Long usuarioId = ids.get("usuarioId");
                Long tareaLavarCocheId = ids.get("tareaId");

                // Ver el comentario en el primer test
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                // realizamos una petición POST al endpoint para editar una tarea

                String urlEditar = "/tareas/" + tareaLavarCocheId + "/editar";
                String urlRedirect = "/usuarios/" + usuarioId + "/tareas";

                this.mockMvc.perform(post(urlEditar)
                                .param("titulo", "Limpiar cristales coche"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl(urlRedirect));

                // Y si realizamos un listado de las tareas del usuario
                // ha cambiado el título de la tarea modificada

                String urlListado = "/usuarios/" + usuarioId + "/tareas";

                this.mockMvc.perform(get(urlListado))
                                .andExpect(content().string(containsString("Limpiar cristales coche")));
        }

        @Test
        public void exportarTareasDevuelveCSV() throws Exception {
                // GIVEN
                Long usuarioId = addUsuarioTareasBD().get("usuarioId");
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // WHEN, THEN
                this.mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas/exportar"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("text/csv"))
                                .andExpect(header().string("Content-Disposition","attachment; filename=\"tareas_usuario_" + usuarioId + ".csv\""))
                                .andExpect(content().string(containsString("ID,Título")))
                                .andExpect(content().string(containsString("Lavar coche")));
        }

        @Test
        public void borrarMultiplesTareasPorFormulario() throws Exception {
                Map<String, Long> ids = addUsuarioTareasBD();
                Long usuarioId = ids.get("usuarioId");
                Long tareaId1 = ids.get("tareaId");
                // Creamos una tercera tarea
                TareaData tareaExtra = tareaService.nuevaTareaUsuario(usuarioId, "Tarea extra", java.time.LocalDate.now());
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // Borramos dos tareas por el endpoint de formulario
                String url = "/usuarios/" + usuarioId + "/tareas/borrar-multiples";
                String tareasIds = tareaId1 + "," + tareaExtra.getId();
                this.mockMvc.perform(post(url).param("tareasIds", tareasIds))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/usuarios/" + usuarioId + "/tareas"));

                // Comprobamos que solo queda una tarea
                this.mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas"))
                                .andExpect(content().string(not(containsString("Lavar coche"))))
                                .andExpect(content().string(not(containsString("Tarea extra"))))
                                .andExpect(content().string(containsString("Renovar DNI")));
        }

        @Test
        public void borrarMultiplesTareasPorJson() throws Exception {
                Map<String, Long> ids = addUsuarioTareasBD();
                Long usuarioId = ids.get("usuarioId");
                Long tareaId1 = ids.get("tareaId");
                TareaData tareaExtra = tareaService.nuevaTareaUsuario(usuarioId, "Tarea extra", java.time.LocalDate.now());
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                // Borramos dos tareas por el endpoint JSON
                String url = "/tareas/multiple";
                String jsonBody = "[" + tareaId1 + "," + tareaExtra.getId() + "]";
                this.mockMvc.perform(delete(url)
                                .contentType("application/json")
                                .content(jsonBody))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("Tareas borradas correctamente")))
                                .andExpect(content().string(containsString("\"cantidad\":2")));

                // Comprobamos que solo queda una tarea
                this.mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas"))
                                .andExpect(content().string(not(containsString("Lavar coche"))))
                                .andExpect(content().string(not(containsString("Tarea extra"))))
                                .andExpect(content().string(containsString("Renovar DNI")));
        }

        @Test
        public void testEndpointTestMultiple() throws Exception {
                Map<String, Long> ids = addUsuarioTareasBD();
                Long usuarioId = ids.get("usuarioId");
                Long tareaId1 = ids.get("tareaId");
                TareaData tareaExtra = tareaService.nuevaTareaUsuario(usuarioId, "Tarea extra", java.time.LocalDate.now());
                when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

                String url = "/tareas/test-multiple";
                String jsonBody = "[" + tareaId1 + "," + tareaExtra.getId() + "]";
                this.mockMvc.perform(post(url)
                                .contentType("application/json")
                                .content(jsonBody))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("Test exitoso")))
                                .andExpect(content().string(containsString("\"usuario\":" + usuarioId)))
                                .andExpect(content().string(containsString("\"tareas\":2")));
        }

        @Test
        public void marcarTareaComoCompletada() throws Exception {
            // GIVEN
            // Un usuario con una tarea en la BD
            Map<String, Long> ids = addUsuarioTareasBD();
            Long usuarioId = ids.get("usuarioId");
            Long tareaId = ids.get("tareaId");
            when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

            // WHEN, THEN
            // realizamos la petición POST para marcar la tarea como completada
            String url = "/tareas/" + tareaId + "/completar";
            this.mockMvc.perform(post(url)
                            .param("completada", "true"))
                            .andExpect(status().isOk())
                            .andExpect(content().string(containsString("Tarea actualizada correctamente")));

            // y verificamos que la tarea está marcada como completada
            TareaData tareaCompletada = tareaService.findById(tareaId);
            assertThat(tareaCompletada.getCompletada()).isTrue();
        }

        @Test
        public void desmarcarTareaCompletada() throws Exception {
            // GIVEN
            // Un usuario con una tarea completada en la BD
            Map<String, Long> ids = addUsuarioTareasBD();
            Long usuarioId = ids.get("usuarioId");
            Long tareaId = ids.get("tareaId");
            tareaService.marcarCompletada(tareaId, true);
            when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

            // WHEN, THEN
            // realizamos la petición POST para desmarcar la tarea como completada
            String url = "/tareas/" + tareaId + "/completar";
            this.mockMvc.perform(post(url)
                            .param("completada", "false"))
                            .andExpect(status().isOk())
                            .andExpect(content().string(containsString("Tarea actualizada correctamente")));

            // y verificamos que la tarea ya no está marcada como completada
            TareaData tareaDescompletada = tareaService.findById(tareaId);
            assertThat(tareaDescompletada.getCompletada()).isFalse();
        }

        @Test
        public void postMarcarTareaComoCompletadaConUsuarioLogeado() throws Exception {
            // GIVEN
            // Un usuario registrado y logueado y una tarea en la BD
            Map<String, Long> ids = addUsuarioTareasBD();
            Long usuarioId = ids.get("usuarioId");
            Long tareaId = ids.get("tareaId");

            // Moqueamos que hay un usuario logeado
            when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

            // Verificamos que la tarea inicialmente no está completada
            TareaData tareaInicial = tareaService.findById(tareaId);
            assertThat(tareaInicial.getCompletada()).isFalse();

            // WHEN, THEN
            // realizamos la petición POST para marcar la tarea como completada
            String url = "/tareas/" + tareaId + "/completar";
            this.mockMvc.perform(post(url)
                            .param("completada", "true"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType("application/json"))
                            .andExpect(jsonPath("$.message", is("Tarea actualizada correctamente")))
                            .andExpect(jsonPath("$.completada", is(true)));

            // y verificamos que la tarea está marcada como completada en la BD
            TareaData tareaCompletada = tareaService.findById(tareaId);
            assertThat(tareaCompletada.getCompletada()).isTrue();
        }

        @Test
        public void postMarcarTareaComoNoCompletadaConUsuarioLogeado() throws Exception {
            // GIVEN
            // Un usuario registrado y logueado y una tarea completada en la BD
            Map<String, Long> ids = addUsuarioTareasBD();
            Long usuarioId = ids.get("usuarioId");
            Long tareaId = ids.get("tareaId");

            // Moqueamos que hay un usuario logeado
            when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

            // Marcamos la tarea como completada primero
            tareaService.marcarCompletada(tareaId, true);
            TareaData tareaCompletada = tareaService.findById(tareaId);
            assertThat(tareaCompletada.getCompletada()).isTrue();

            // WHEN, THEN
            // realizamos la petición POST para desmarcar la tarea como completada
            String url = "/tareas/" + tareaId + "/completar";
            this.mockMvc.perform(post(url)
                            .param("completada", "false"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType("application/json"))
                            .andExpect(jsonPath("$.message", is("Tarea actualizada correctamente")))
                            .andExpect(jsonPath("$.completada", is(false)));

            // y verificamos que la tarea ya no está marcada como completada en la BD
            TareaData tareaDescompletada = tareaService.findById(tareaId);
            assertThat(tareaDescompletada.getCompletada()).isFalse();
        }

        @Test
        public void postMarcarTareaInexistenteDevuelveError() throws Exception {
            // GIVEN
            // Un usuario registrado y logueado
            Map<String, Long> ids = addUsuarioTareasBD();
            Long usuarioId = ids.get("usuarioId");

            // Moqueamos que hay un usuario logeado
            when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

            // WHEN, THEN
            // realizamos la petición POST para marcar una tarea inexistente
            String url = "/tareas/999999/completar";
            this.mockMvc.perform(post(url)
                            .param("completada", "true"))
                            .andExpect(status().isNotFound());
        }
}
