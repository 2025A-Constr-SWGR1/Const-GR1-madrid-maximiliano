# Cambios realizados por Kevin – Exportar a CSV

Se ha añadido la funcionalidad para exportar las tareas de un usuario a formato CSV.

## 1. Modificacion de la Vista (`listaTareas.html`)

Se añadió un botón de exportación en la plantilla Thymeleaf:

```html
<!-- src/main/resources/templates/listaTareas.html -->
<div class="row mt-3">
    <div class="col text-center">
        <a th:href="@{'/usuarios/' + ${usuario.id} + '/tareas/exportar'}" 
           class="btn btn-success">
            Exportar a CSV
        </a>
    </div>
</div>
```

## 2. Modificacion en el Controller (`TareaController`)

```java
// src/main/madstodolist/controller/TareaController.java
@GetMapping("/usuarios/{id}/tareas/exportar")
public void exportarTareas(@PathVariable(value="id") Long idUsuario, 
                         HttpServletResponse response) throws IOException {
    
    // Obtener las tareas del usuario
    List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
    
    // Configurar la respuesta HTTP
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=\"tareas_usuario_" + idUsuario + ".csv\"");
    
    // Crear el escritor CSV
    try (PrintWriter writer = response.getWriter()) {
        // Escribir cabeceras
        writer.println("ID,Título,Completada,Fecha Creación");
        
        // Escribir datos
        for (TareaData tarea : tareas) {
            writer.println(String.format("%d,%s,%s,%s",
                tarea.getId(),
                tarea.getTitulo(),
                tarea.isCompletada() ? "Sí" : "No",
                tarea.getFechaCreacion().toString()));
        }
    }
}
```

## 3. Actualización del Servicio (`TareaService`)

```java
// src/main/madstodolist/service/TareaService.java
public String generarCSV(List<TareaData> tareas) {
    StringBuilder csv = new StringBuilder();
    csv.append("ID,Título,Completada,Fecha Creación\n");
    
    for (TareaData tarea : tareas) {
        csv.append(String.format("%d,%s,%s,%s\n",
            tarea.getId(),
            tarea.getTitulo(),
            tarea.isCompletada() ? "Sí" : "No",
            tarea.getFechaCreacion().toString()));
    }
    
    return csv.toString();
}
```

## 4. Agregamiento Tests

### Test de Controller (`TareaWebTest`)

```java
// src/test/java/madstodolist/controller/TareaWebTest.java
@Test
public void exportarTareasDevuelveCSV() throws Exception {
    // GIVEN
    Long usuarioId = addUsuarioTareasBD().get("usuarioId");
    when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

    // WHEN, THEN
    this.mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas/exportar"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/csv"))
        .andExpect(header().string("Content-Disposition", 
            "attachment; filename=\"tareas_usuario_" + usuarioId + ".csv\""))
        .andExpect(content().string(containsString("ID,Título,Completada,Fecha Creación")))
        .andExpect(content().string(containsString("Lavar coche")));
}
```

### Test de Servicio (`TareaServiceTest`)

```java
// src/test/java/madstodolist/service/TareaServiceTest.java
@Test
public void testGenerarCSV() {
    // GIVEN
    Long usuarioId = addUsuarioTareasBD().get("usuarioId");
    List<TareaData> tareas = tareaService.allTareasUsuario(usuarioId);

    // WHEN
    String csv = tareaService.generarCSV(tareas);

    // THEN
    assertThat(csv).contains("ID,Título,Completada,Fecha Creación");
    assertThat(csv).contains("Lavar coche");
    assertThat(csv.split("\n").length).isEqualTo(tareas.size() + 1);
}
```