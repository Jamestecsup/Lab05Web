package com.tecsup;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void crearProducto() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Monitor\",\"precio\":50,\"stock\":20,\"categoria\":\"Tecnologia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Monitor"))
                .andExpect(jsonPath("$.precio").value(50.0))
                .andExpect(jsonPath("$.stock").value(20))
                .andExpect(jsonPath("$.categoria").value("Tecnologia"));
    }

    @Test
    @Order(2)
    void listarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(3)
    void buscarPorNombreParcial() throws Exception {
        mockMvc.perform(get("/api/productos/buscar").param("nombre", "mon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Monitor"));
    }

    @Test
    @Order(4)
    void validacionRechazaProductoInvalido() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"precio\":0,\"stock\":-1,\"categoria\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.precio").exists())
                .andExpect(jsonPath("$.stock").exists())
                .andExpect(jsonPath("$.categoria").value("La categoria es obligatoria"));
    }

    @Test
    @Order(5)
    void obtenerProductoPorId() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Monitor"));
    }

    @Test
    @Order(6)
    void actualizarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Monitor actualizado\",\"precio\":50,\"stock\":20,\"categoria\":\"Perifericos\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Monitor actualizado"))
                .andExpect(jsonPath("$.categoria").value("Perifericos"));
    }

    @Test
    @Order(7)
    void obtenerProductoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void eliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}