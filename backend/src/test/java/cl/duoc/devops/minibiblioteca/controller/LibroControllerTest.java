package cl.duoc.devops.minibiblioteca.controller;

import cl.duoc.devops.minibiblioteca.exception.LibroNoEncontradoException;
import cl.duoc.devops.minibiblioteca.model.Libro;
import cl.duoc.devops.minibiblioteca.service.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroService libroService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // MockMvc "standalone": solo el controller + el manejador de errores,
        // sin levantar Spring completo ni abrir puertos.
        mockMvc = MockMvcBuilders
                .standaloneSetup(new LibroController(libroService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getLibros_deberiaResponder200ConLista() throws Exception {
        when(libroService.listar()).thenReturn(List.of(
                new Libro(1L, "Clean Code", "Robert C. Martin", true)));

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"));
    }

    @Test
    void getLibroPorId_deberiaResponder200CuandoExiste() throws Exception {
        when(libroService.buscarPorId(1L))
                .thenReturn(new Libro(1L, "Clean Code", "Robert C. Martin", true));

        mockMvc.perform(get("/api/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autor").value("Robert C. Martin"));
    }

    @Test
    void getLibroPorId_deberiaResponder404CuandoNoExiste() throws Exception {
        // Caso crítico: un id inexistente debe dar 404, nunca 500.
        when(libroService.buscarPorId(99L)).thenThrow(new LibroNoEncontradoException(99L));

        mockMvc.perform(get("/api/libros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un libro con id 99"));
    }

    @Test
    void getResumen_deberiaCalcularTotalYDisponibles() throws Exception {
        when(libroService.listar()).thenReturn(List.of(
                new Libro(1L, "A", "Autor A", true),
                new Libro(2L, "B", "Autor B", false)));
        when(libroService.contarDisponibles()).thenReturn(1L);

        mockMvc.perform(get("/api/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.disponibles").value(1));
    }

    @Test
    void getHealth_deberiaResponderUp() throws Exception {
        // Caso crítico: el healthcheck de Docker y el smoke test dependen de este endpoint.
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
