package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persist(new Producto(null, "Laptop", 1500.0, "ELECTRONICA"));
        em.persist(new Producto(null, "Cuaderno", 5.0, "PAPELERIA"));
        em.flush();
    }

    @Test
    @DisplayName("findByCategoria con ELECTRONICA retorna un producto")
    void findByCategoria_electronica_retornaUnProducto() {
        List<Producto> resultado = repo.findByCategoria("ELECTRONICA");

        assertEquals(1, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("ELECTRONICA", resultado.get(0).getCategoria());
        assertEquals(1500.0, resultado.get(0).getPrecio());
    }

    @Test
    @DisplayName("findByCategoria sin coincidencias retorna lista vacía")
    void findByCategoria_sinCoincidencias_retornaListaVacia() {
        List<Producto> resultado = repo.findByCategoria("DEPORTES");

        assertTrue(resultado.isEmpty());
    }
}

