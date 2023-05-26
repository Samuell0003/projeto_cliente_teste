package com.iftm.client.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.exceptions.ResourceNotFoundException;



@ExtendWith(SpringExtension.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService service;

    @Mock
    private ClientRepository repository;


    
    @DisplayName("Testar se o método deleteById apaga um registro e não retorna outras informações")
    @Test
    public void testarApagarPorIdTemSucessoComIdExistente() {
        //cenário
        long idExistente = 1;
        //configurando mock : definindo que o método deleteById não retorna nada para esse id.
        Mockito.doNothing().when(repository).deleteById(idExistente);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);

    }

    @DisplayName("Testar se o método deleteById retorna exception para idInexistente")
    @Test
    public void testarApagarPorIdGeraExceptionComIdInexistente() {
        //cenário
        long idNaoExistente = 100;
        //configurando mock : definindo que o método deleteById retorna uma exception para esse id.
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(idNaoExistente);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idNaoExistente));

        Mockito.verify(repository, Mockito.times(1)).deleteById(idNaoExistente);

    }


    @Test
    public void testarFindByIdExitente() {

        long idExitente = 3;
        Client clientExistente = new Client(idExitente, "Samuel", "16489517671", 750D, Instant.now(), 1);
        Mockito.doReturn(Optional.of(clientExistente)).when(repository).findById(idExitente);
        
        ClientDTO client = service.findById(idExitente);

        Assertions.assertEquals(clientExistente.getId(), client.getId());
        Mockito.verify(repository, Mockito.times(1)).findById(idExitente);
    }

    @Test
    public void testarFindByIdInexistente() {
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(100L);
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(100L));
    
        Mockito.verify(repository, Mockito.times(1)).findById(100L);
    }

    @Test
    public void testarUpdateExitente() {

        Long idExitente = 3L;
        ClientDTO clientModificado = new ClientDTO(idExitente, "Samuel Lucas", "16489517671", 750D, Instant.now(), 1);
        Client clientExistente = new Client(idExitente, "Samuel", "16489517671", 750D, Instant.now(), 1);

        Mockito.doReturn(clientExistente).when(repository).getOne(idExitente);
        Mockito.doReturn(clientModificado.toEntity()).when(repository).save(clientExistente);
        
         
        ClientDTO clientReturn =  service.update(idExitente, clientModificado);

        Assertions.assertEquals(clientReturn.getName(), "Samuel Lucas");
        Mockito.verify(repository, Mockito.times(1)).getOne(idExitente);
        Mockito.verify(repository, Mockito.times(1)).save(clientExistente);
        
    }
    @Test
    public void testarUpdateInexistente() {
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(100L);
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(100L, new ClientDTO(100L, "Samuel Lucas", "16489517671", 750D, Instant.now(), 1)));
    
        Mockito.verify(repository, Mockito.times(1)).getOne(100L);
    }

    @Test
    public void testeFindAllPaged(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(1L,"Samuel", "123", 1000D, Instant.now(), 2),
                new Client(2L,"Lucas", "456", 1000D, Instant.now(), 1),
                new Client(3L,"Gomes", "789", 1000D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);

        Mockito.when(repository.findAll(pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findAllPaged(pageRequest);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(clientes.size(), resultado.getSize());
        Assertions.assertEquals(clientes.get(0).getId(), resultado.getContent().get(0).getId());
        Assertions.assertEquals(clientes.get(1).getId(), resultado.getContent().get(1).getId());
        Assertions.assertEquals(clientes.get(2).getId(), resultado.getContent().get(2).getId());
        Mockito.verify(repository , Mockito.times(1)).findAll(pageRequest);
    }

    @Test
    public void testeFindByIncomeLessThan(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(2L,"Samuel", "123", 6500D, Instant.now(), 0),
                new Client(3L,"Lucas", "456", 3500D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);
        int tamanhoEsperado = 2;

        Mockito.when(repository.findByIncomeLessThan(1500D, pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findByIncomeLessThan(1500D, pageRequest);

        Assertions.assertEquals(tamanhoEsperado, resultado.getContent().size());
        Assertions.assertTrue(resultado.getContent().get(0).getIncome()>1500);
        Assertions.assertTrue(resultado.getContent().get(1).getIncome()>1500);
        Mockito.verify(repository , Mockito.times(1)).findByIncomeLessThan(1500D, pageRequest);
    }

    @Test
    public void testeInsertClient() {
        ClientDTO newCliente = new ClientDTO(1L, "Samuel", "16489517671", 750D, Instant.now(), 1);
        Mockito.when(repository.save(newCliente.toEntity())).thenReturn(newCliente.toEntity());

        ClientDTO client = service.insert(newCliente);

        Assertions.assertEquals(newCliente.getId(), client.getId());
        Mockito.verify(repository, Mockito.times(1)).save(newCliente.toEntity());

    }
}


