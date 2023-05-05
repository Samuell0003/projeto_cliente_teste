package com.iftm.client.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.iftm.client.entities.Client;

@DataJpaTest
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository repository;


    @Test
    @DisplayName("Testar um nome existente")
    public void testarBuscarPorNomeClienteExistente() {
        String name = "Lázaro Ramos";

        Optional<Client> client = repository.findByNameIgnoreCase(name);
        Assertions.assertThat(client.get().getName()).isEqualTo(name);
        
    }

    @Test
    @DisplayName("Testar um nome não existente")
    public void testarBuscarPorNomeClienteInexistente() {
        String name = "Samuel";

        Optional<Client> client = repository.findByNameIgnoreCase(name);
        Assertions.assertThat(client).isEmpty();
    }

    @Test
    @DisplayName("Testar um texto existente")
    public void testarBuscarPorNomeQualquerExistente() {
        String name = "ama";

        List<Client> client = repository.findAllByName(name);

        Assertions.assertThat(client.get(0).getId()).isEqualTo(7);
        Assertions.assertThat(client.get(1).getId()).isEqualTo(10);
        Assertions.assertThat(client.get(2).getId()).isEqualTo(12);
    }
    
    @Test
    @DisplayName("Testar um texto não existente")
    public void testarBuscarPorNomeQualquerInexistente() {
        String name = "eu";

        List<Client> client = repository.findAllByName(name);

        Assertions.assertThat(client).isEmpty();
    }
    
    @Test
    @DisplayName("Testar find para nome vazio")
    public void testarBuscarPorNomeVazio() {
        String name = "";

        List<Client> client = repository.findAllByName(name);

        Assertions.assertThat(client.size()).isEqualTo(12);
    }
    @Test
    @DisplayName("busca clientes com salários superiores a um valor")
    public void testarClientesComSalarioSuperior() {
        double valor = 3000;

        List<Client> client = repository.findByIncomeGreaterThan(valor);

        Assertions.assertThat(client.get(0).getId()).isEqualTo(3);
        Assertions.assertThat(client.get(1).getId()).isEqualTo(4);
        Assertions.assertThat(client.get(2).getId()).isEqualTo(6);
        Assertions.assertThat(client.get(3).getId()).isEqualTo(7);
        Assertions.assertThat(client.get(4).getId()).isEqualTo(8);
        Assertions.assertThat(client.get(5).getId()).isEqualTo(11);
    }
    
    @Test
    @DisplayName("busca clientes com salários inferiores a um valor")
    public void testarClientesComSalarioInferior() {
        double valor = 2000;

        List<Client> client = repository.findByIncomeLessThan(valor);

        Assertions.assertThat(client.get(0).getId()).isEqualTo(1);
        Assertions.assertThat(client.get(1).getId()).isEqualTo(9);
        Assertions.assertThat(client.get(2).getId()).isEqualTo(10);
        
    }
    
    @Test
    @DisplayName("busca clientes com salários que esteja no intervalo entre dois valores informados")
    public void testarClientesComSalarioEntreDoisValores() {
        double valor1 = 3000;
        double valor2 = 5500;

        List<Client> client = repository.findByIncomeBetween(valor1, valor2);

        Assertions.assertThat(client.get(0).getId()).isEqualTo(3);
        Assertions.assertThat(client.get(1).getId()).isEqualTo(6);
        Assertions.assertThat(client.get(2).getId()).isEqualTo(7);
        Assertions.assertThat(client.get(3).getId()).isEqualTo(11);
        
    }
    
    
    @Test
    @DisplayName("buscando clientes que nasceram entre duas datas")
    public void testarClientesComDataNascimentoEntreDoisValores() {
        Instant dataI = Instant.parse("2017-12-25T20:30:50Z");
        Instant dataT = Instant.now();

        List<Client> client = repository.findClientBybirthDateBetween(dataI, dataT);

        Assertions.assertThat(client.get(0).getId()).isEqualTo(1);
        
        
    }
    @Test
    @DisplayName("Testar o update (save) de um cliente.")
    public void testarClientesUpdate() {
        Optional<Client> client = repository.findById(3L);        
        
        if (client.isPresent()) {
            Client cliente = client.get();

            cliente.setName("Samuel Lucas Gomes");
            cliente.setIncome(850D);

            repository.save(cliente);
        }
        client = repository.findById(3L);        

        Assertions.assertThat(client.get().getName()).isEqualTo("Samuel Lucas Gomes");
        Assertions.assertThat(client.get().getIncome()).isEqualTo(850);
    }



}
