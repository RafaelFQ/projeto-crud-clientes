package com.rafaelfq.crud_clientes.services;

import com.rafaelfq.crud_clientes.dto.ClientDTO;
import com.rafaelfq.crud_clientes.entities.Client;
import com.rafaelfq.crud_clientes.repositories.ClientRepository;
import com.rafaelfq.crud_clientes.services.exceptions.DatabaseException;
import com.rafaelfq.crud_clientes.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTests {

   @InjectMocks
   private ClientService service;

   @Mock
   private ClientRepository repository;

   private final Long existingId = 1L;
   private final Long nonExistingId = 2L;
   private final Long dependentId = 3L;

   private Client client;

   @BeforeEach
   void setUp() {
      client = new Client(existingId, "Maria Silva", "12345678900", 5000.0, LocalDate.parse("1990-05-15"), 2);
   }

   @Test
   void findByIdShouldReturnClientDTOWhenIdExists() {
      when(repository.findById(existingId)).thenReturn(Optional.of(client));

      ClientDTO result = service.findById(existingId);

      assertThat(result)
            .isNotNull()
            .usingRecursiveComparison()
            .isEqualTo(client);
      verify(repository).findById(existingId);
   }

   @Test
   void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
      when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.findById(nonExistingId))
            .isInstanceOf(ResourceNotFoundException.class);
   }

   @Test
   void findAllShouldReturnPageOfClientDTO() {
      Pageable pageable = Pageable.unpaged();
      when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(client)));

      Page<ClientDTO> result = service.findAll(pageable);

      assertThat(result).isNotEmpty();
      assertThat(result.getContent().get(0).getName()).isEqualTo("Maria Silva");
   }

   @Test
   void insertShouldReturnClientDTO() {
      ClientDTO dto = new ClientDTO(null, "João Silva", "98765432100", 3000.0, LocalDate.parse("1995-10-20"), 1);
      when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

      ClientDTO result = service.insert(dto);

      assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(dto);
   }

   @Test
   void updateShouldReturnClientDTOWhenIdExists() {
      ClientDTO dto = new ClientDTO(null, "Maria Atualizada", "12345678900", 6000.0, LocalDate.parse("1990-05-15"), 3);
      when(repository.getReferenceById(existingId)).thenReturn(client);
      when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

      ClientDTO result = service.update(existingId, dto);

      assertThat(result.getName()).isEqualTo("Maria Atualizada");
      assertThat(result.getIncome()).isEqualTo(6000.0);
   }

   @Test
   void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
      when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

      assertThatThrownBy(() -> service.update(nonExistingId, new ClientDTO()))
            .isInstanceOf(ResourceNotFoundException.class);
   }

   @Test
   void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
      when(repository.existsById(nonExistingId)).thenReturn(false);

      assertThatThrownBy(() -> service.delete(nonExistingId))
            .isInstanceOf(ResourceNotFoundException.class);

      verify(repository, never()).deleteById(nonExistingId);
   }

   @Test
   void deleteShouldThrowDatabaseExceptionWhenDependentId() {
      when(repository.existsById(dependentId)).thenReturn(true);
      doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

      assertThatThrownBy(() -> service.delete(dependentId))
            .isInstanceOf(DatabaseException.class);
   }

   @Test
   void deleteShouldDeleteClientWhenIdExists() {
      when(repository.existsById(existingId)).thenReturn(true);

      service.delete(existingId);

      verify(repository).deleteById(existingId);
   }
}

