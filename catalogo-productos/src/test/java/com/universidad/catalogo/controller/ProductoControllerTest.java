package com.universidad.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService service;

    @Test
    @DisplayName("GET /{id} cuando el producto existe retorna HTTP 200 y el JSON del producto")
    void get_productoExiste_retorna200() throws Exception {
        Producto producto = new Producto(1L, "Laptop Gamer", 2500.0, "ELECTRONICA");
        when(service.buscarPorId(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Laptop Gamer"))
                .andExpect(jsonPath("$.precio").value(2500.0))
                .andExpect(jsonPath("$.categoria").value("ELECTRONICA"));
    }

    @Test
    @DisplayName("GET /{id} cuando el producto no existe retorna HTTP 404")
    void get_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99999L))
                .thenThrow(new EntityNotFoundException("Producto no encontrado: 99999"));

        mockMvc.perform(get("/api/productos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado: 99999"));
    }

    @Test
    @DisplayName("GET con parámetro categoria retorna HTTP 200 y la lista de productos")
    void get_listarPorCategoria_retorna200YLista() throws Exception {
        List<Producto> lista = Collections.singletonList(
                new Producto(1L, "Teclado mecánico", 120.0, "ELECTRONICA")
        );
        when(service.listarPorCategoria("ELECTRONICA")).thenReturn(lista);

        mockMvc.perform(get("/api/productos").param("categoria", "ELECTRONICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado mecánico"))
                .andExpect(jsonPath("$[0].categoria").value("ELECTRONICA"));
    }

    @Test
    @DisplayName("POST / con datos válidos retorna HTTP 201 y el producto persistido")
    void post_conDatosValidos_retorna201() throws Exception {
        Producto entrada = new Producto(null, "Teclado mecánico", 120.0, "ELECTRONICA");
        Producto guardado = new Producto(1L, "Teclado mecánico", 120.0, "ELECTRONICA");

        when(service.crear(any(Producto.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Teclado mecánico"))
                .andExpect(jsonPath("$.precio").value(120.0));
    }

    @Test
    @DisplayName("POST / con precio inválido retorna HTTP 400 y mensaje de error")
    void post_conPrecioInvalido_retorna400() throws Exception {
        Producto invalido = new Producto(null, "Teclado mecánico", -10.0, "ELECTRONICA");

        when(service.crear(any(Producto.class)))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El precio debe ser mayor a cero"));
    }

    @Test
    @DisplayName("PATCH /{id}/precio con precio válido retorna HTTP 200")
    void patch_conPrecioValido_retorna200() throws Exception {
        Producto actualizado = new Producto(1L, "Teclado mecánico", 150.0, "ELECTRONICA");
        when(service.actualizarPrecio(1L, 150.0)).thenReturn(actualizado);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/productos/1/precio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\": 150.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(150.0));
    }

    @Test
    @DisplayName("PUT /{id}/precio con precio válido retorna HTTP 200")
    void put_conPrecioValido_retorna200() throws Exception {
        Producto actualizado = new Producto(1L, "Teclado mecánico", 140.0, "ELECTRONICA");
        when(service.actualizarPrecio(1L, 140.0)).thenReturn(actualizado);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/productos/1/precio")
                        .param("precio", "140.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(140.0));
    }
}
