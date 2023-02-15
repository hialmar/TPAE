package org.miage.tpae.exposition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miage.tpae.dao.ClientRepository;
import org.miage.tpae.dao.CompteRepository;
import org.miage.tpae.dao.OperationCompteRepository;
import org.miage.tpae.entities.Client;
import org.miage.tpae.entities.Compte;
import org.miage.tpae.metier.ServiceClient;
import org.miage.tpae.metier.ServiceCompte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;



@WebMvcTest(RestClient.class)
class RestClientTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServiceClient serviceClient;

    @MockBean
    private ServiceCompte serviceCompte;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private CompteRepository compteRepository;

    @MockBean
    OperationCompteRepository operationCompteRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getClient() throws Exception {
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);

        given(serviceClient.recupererClient(0L)).willReturn(client);

        mvc.perform(get("/api/clients/0")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is(client.getNom())));
    }

    @Test
    void creerClient() throws Exception {
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);

        given(serviceClient.creerClient("Jean", "Test")).willReturn(client);

        mvc.perform(post("/api/clients")
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"nom\" : \"Test\", \"prenom\":\"Jean\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)));
    }

    @Test
    void ouvrirCompte() throws Exception {
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);

        Compte compte = new Compte();
        compte.setId(1L);
        compte.setClient(client);
        compte.setSolde(1000);
        compte.setDateInterrogation(null);

        given(serviceClient.recupererClient(0L)).willReturn(client);
        given(serviceCompte.ouvrir(0L, 1000)).willReturn(compte);

        mvc.perform(post("/api/clients/0/comptes")
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"solde\" : 1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void listerComptes() throws Exception {
        Client client = new Client();
        client.setPrenom("Jean");
        client.setNom("Test");
        client.setId(0L);

        Compte compte = new Compte();
        compte.setId(1L);
        compte.setClient(client);
        compte.setSolde(1000);
        compte.setDateInterrogation(null);
        List<Compte> compteList = Arrays.asList(compte);
        client.setComptes(compteList);

        given(serviceClient.recupererClient(0L)).willReturn(client);

        mvc.perform(get("/api/clients/0/comptes")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

}