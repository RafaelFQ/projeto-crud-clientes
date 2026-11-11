package com.rafaelfq.crud_clientes.controllers;

import com.rafaelfq.crud_clientes.dto.ClientDTO;
import com.rafaelfq.crud_clientes.services.ClientService;
import com.rafaelfq.crud_clientes.services.exceptions.DatabaseException;
import com.rafaelfq.crud_clientes.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTests {

   @Autowired
   private MockMvc mockMvc;

   // Uso correto do @MockitoBean para o Spring Boot 4.x
   @MockitoBean
   private ClientService service;

   @Autowired
   private ObjectMapper objectMapper;

   private Long existingId;
   private Long nonExistingId;
   private Long dependentId;
   private ClientDTO clientDTO;
   private PageImpl<ClientDTO> page;

   @BeforeEach
   void setUp() throws Exception {
      existingId = 1L;
      nonExistingId = 2L;
      dependentId = 3L;

      clientDTO = new ClientDTO(existingId, "Maria Silva", "12345678901", 6500.0, LocalDate.of(1994, 7, 20), 2);
      page = new PageImpl<>(List.of(clientDTO));

      // Mock do findAll (Busca paginada)
      Mockito.when(service.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

      // Mocks do findById
      Mockito.when(service.findById(existingId)).thenReturn(clientDTO);
      Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

      // Mocks do insert
      Mockito.when(service.insert(ArgumentMatchers.any(ClientDTO.class))).thenReturn(clientDTO);

      // Mocks do update
      Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any(ClientDTO.class))).thenReturn(clientDTO);
      Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any(ClientDTO.class))).thenThrow(ResourceNotFoundException.class);

      // Mocks do delete
      Mockito.doNothing().when(service).delete(existingId);
      Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
      Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
   }

   @Test
   public void findAllShouldReturnPage() throws Exception {
      mockMvc.perform(get("/clients")
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
   }

   @Test
   public void findByIdShouldReturnClientWhenIdExists() throws Exception {
      mockMvc.perform(get("/clients/{id}", existingId)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(existingId))
            .andExpect(jsonPath("$.name").value("Maria Silva"))
            .andExpect(jsonPath("$.cpf").value("12345678901"));
   }

   @Test
   public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
      mockMvc.perform(get("/clients/{id}", nonExistingId)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
   }

   @Test
   public void insertShouldReturnClientCreated() throws Exception {
      String jsonBody = objectMapper.writeValueAsString(clientDTO);

      mockMvc.perform(post("/clients")
                  .content(jsonBody)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(existingId))
            .andExpect(jsonPath("$.name").value("Maria Silva"));
   }

   @Test
   public void updateShouldReturnClientWhenIdExists() throws Exception {
      String jsonBody = objectMapper.writeValueAsString(clientDTO);

      mockMvc.perform(put("/clients/{id}", existingId)
                  .content(jsonBody)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(existingId))
            .andExpect(jsonPath("$.name").value("Maria Silva"));
   }

   @Test
   public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
      String jsonBody = objectMapper.writeValueAsString(clientDTO);

      mockMvc.perform(put("/clients/{id}", nonExistingId)
                  .content(jsonBody)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
   }

   @Test
   public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
      mockMvc.perform(delete("/clients/{id}", existingId))
            .andExpect(status().isNoContent());
   }

   @Test
   public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
      mockMvc.perform(delete("/clients/{id}", nonExistingId))
            .andExpect(status().isNotFound());
   }

   @Test
   public void insertShouldReturnUnprocessableEntityWhenDataIsInvalid() throws Exception {
      // Criando um DTO inválido (exemplo: nome em branco) para forçar o Bean Validation a falhar
      ClientDTO invalidDTO = new ClientDTO(null, "", "12345678901", 6500.0, LocalDate.of(1994, 7, 20), 2);
      String jsonBody = objectMapper.writeValueAsString(invalidDTO);

      mockMvc.perform(post("/clients")
                  .content(jsonBody)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity()); // Espera o erro 422
   }
}
