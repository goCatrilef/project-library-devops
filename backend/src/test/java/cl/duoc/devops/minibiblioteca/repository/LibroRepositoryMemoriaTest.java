package cl.duoc.devops.minibiblioteca.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibroRepositoryMemoriaTest {

    private final LibroRepositoryMemoria repository = new LibroRepositoryMemoria();

    @Test
    void findAll_deberiaRetornarCatalogoInicial() {
        assertEquals(4, repository.findAll().size());
    }

    @Test
    void findById_deberiaEncontrarLibroExistente() {
        assertTrue(repository.findById(2L).isPresent());
        assertEquals("The DevOps Handbook", repository.findById(2L).get().titulo());
    }

    @Test
    void findById_deberiaRetornarVacioSiNoExiste() {
        assertFalse(repository.findById(999L).isPresent());
    }
}
