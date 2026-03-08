package com.rafaelfq.crud_clientes.controllers;

import com.rafaelfq.crud_clientes.dto.ClientDTO;
import com.rafaelfq.crud_clientes.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private ClientService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity <ClientDTO> findById(@PathVariable Long id) {
        ClientDTO dto = service.findById(id);
        return ResponseEntity.ok(dto); // 200 OK com corpo
    }

    @GetMapping
    public ResponseEntity <Page<ClientDTO>> findAll(Pageable pageable) {
        Page <ClientDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto); // 200 OK com corpo paginado
    }

    @PostMapping
    public ResponseEntity <ClientDTO > insert(@Valid @RequestBody ClientDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto); // 201 Recurso Criado
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity <ClientDTO> update(@PathVariable Long id, @Valid @RequestBody ClientDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto); // 200 OK com corpo
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity <Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }
}
