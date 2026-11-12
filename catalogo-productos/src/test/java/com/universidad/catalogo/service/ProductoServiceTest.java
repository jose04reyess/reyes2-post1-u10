package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repo;

    @InjectMocks
    private ProductoService service;

    @Test
    @DisplayName("crear con datos válidos guarda y retorna el producto")
    void crear_conDatosValidos_guardaYRetorna() {
        Producto nuevo = new Producto(null, "Mouse Inalámbrico", 25.5, "ELECTRONICA");
        Producto guardado = new Producto(1L, "Mouse Inalámbrico", 25.5, "ELECTRONICA");
        when(repo.save(any(Producto.class))).thenReturn(guardado);

        Producto resultado = service.crear(nuevo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Inalámbrico", resultado.getNombre());
        verify(repo).save(nuevo);
    }

    @Test
    @DisplayName("crear con nombre vacío lanza IllegalArgumentException y no persiste")
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Producto sinNombre = new Producto(null, "   ", 50.0, "ELECTRONICA");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crear(sinNombre));
        assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("crear con precio negativo o cero lanza IllegalArgumentException y no persiste")
    void crear_conPrecioNegativo_lanzaIllegalArgumentException() {
        Producto precioNegativo = new Producto(null, "Monitor", -10.0, "ELECTRONICA");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crear(precioNegativo));
        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId cuando existe retorna el producto correspondiente")
    void buscarPorId_cuandoExiste_retornaProducto() {
        Producto producto = new Producto(1L, "Laptop", 1200.0, "ELECTRONICA");
        when(repo.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId cuando no existe lanza EntityNotFoundException")
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
        assertEquals("Producto no encontrado: 99", ex.getMessage());
        verify(repo).findById(99L);
    }

    @Test
    @DisplayName("listarPorCategoria retorna la lista filtrada de productos")
    void listarPorCategoria_retornaListaFiltrada() {
        List<Producto> lista = Arrays.asList(
                new Producto(1L, "Laptop", 1200.0, "ELECTRONICA"),
                new Producto(2L, "Mouse", 25.0, "ELECTRONICA")
        );
        when(repo.findByCategoria("ELECTRONICA")).thenReturn(lista);

        List<Producto> resultado = service.listarPorCategoria("ELECTRONICA");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repo).findByCategoria("ELECTRONICA");
    }

    @Test
    @DisplayName("actualizarPrecio con valor válido actualiza y guarda el producto")
    void actualizarPrecio_conValorValido_actualizaYGuarda() {
        Producto existente = new Producto(1L, "Teclado", 50.0, "ELECTRONICA");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = service.actualizarPrecio(1L, 75.0);

        assertNotNull(resultado);
        assertEquals(75.0, resultado.getPrecio());
        verify(repo).findById(1L);
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("actualizarPrecio con precio inválido lanza IllegalArgumentException y no persiste")
    void actualizarPrecio_conPrecioInvalido_lanzaIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.actualizarPrecio(1L, -15.0));
        assertEquals("El nuevo precio debe ser mayor a cero", ex.getMessage());
        verify(repo, never()).save(any());
    }
}

