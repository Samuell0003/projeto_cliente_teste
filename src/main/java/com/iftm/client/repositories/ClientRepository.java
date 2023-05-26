package com.iftm.client.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    public Optional<Client> findByNameIgnoreCase(String nome);

    @Query(value = "select c from Client c where LOWER(c.name) like LOWER(CONCAT('%', :nome, '%'))")
    public List<Client> findAllByName(String nome);
    
    public List<Client> findByIncomeGreaterThan(Double valor);

    // public Page<ClientDTO> findByIncomeGreaterThan(Pageable pageble, Double valor);

    public List<Client> findByIncomeLessThan(Double valor);
    
    public Page<Client> findByIncomeLessThan(Double salarioI, Pageable pageable);

    public List<Client> findByIncomeBetween(Double valor1, Double valor2);

    public List<Client> findClientBybirthDateBetween(Instant DataInicio, Instant DataTermino);

}
