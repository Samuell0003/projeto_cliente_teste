package com.iftm.client.resouces;

import java.time.Instant;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftm.client.dto.ClientDTO;
import com.iftm.client.services.ClientService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTestsIT {
    @Autowired
    private MockMvc mockMvc;

    // @MockBean
    // private ClientService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testInsertResource() throws Exception {
        ClientDTO client = new ClientDTO(15L, "Reginaldo", "123456789", 2000D, Instant.now(), 1);

        String json = objectMapper.writeValueAsString(client);
        
        ResultActions result = mockMvc.perform(post("/clients/")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

                                result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.name").value(client.getName()))
            .andExpect(jsonPath("$.cpf").exists())
            .andExpect(jsonPath("$.cpf").value(client.getCpf()))
            .andExpect(jsonPath("$.income").exists())
            .andExpect(jsonPath("$.income").value(client.getIncome()))
            .andExpect(jsonPath("$.birthDate").exists())
            .andExpect(jsonPath("$.birthDate").value(client.getBirthDate().toString()))
            .andExpect(jsonPath("$.children").exists())
            .andExpect(jsonPath("$.children").value(client.getChildren()));
    }


    // public void testInsert
    @Test
    public void testaRetornoSucessoUpdate() throws Exception {
        ClientDTO client = new ClientDTO(1L, "Reginaldo", "123456789", 2000D, Instant.now(), 1);


        String json = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(put("/clients/{id}", client.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

                
        result.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value(client.getName()))
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.cpf").value(client.getCpf()));
    }

    @Test
    @DisplayName("Testa se update retorna erro ao alterar cliente inexistente")
    public void testaRetornoErroUpdate() throws Exception {
        ClientDTO client = new ClientDTO(99L,
                "User",
                "123123123",
                5000.0,
                Instant.parse("1996-12-23T07:00:00Z"),
                3);

        String json = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(put("/clients/{id}", client.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }


    @Test
    public void testaRetornoSucessoDelete() throws Exception {
        long idExistente = 1L;

        ResultActions resultado = mockMvc.perform(delete("/clients/{id}",idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testaRetornoErroDelete() throws Exception {
        long idExistente = 1L;

        ResultActions resultado = mockMvc.perform(delete("/clients/{id}",idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().is2xxSuccessful());
    }


    @Test
    public void testarEndPointRetornaRegistrosEspecificosFindByIncome() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/clients/incomeGreaterThan/")
                .param("income", String.valueOf(6000.00))
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 4L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 8L).exists())
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.first").value(true));
    }
}
