package com.rafaelfq.crud_clientes.repositories;

import com.rafaelfq.crud_clientes.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}